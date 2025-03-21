// 集成RocketMQ实现库存变更异步通知
// 基于事务消息确保数据一致性：
// 1. 事务消息机制：确保库存操作与消息发送的原子性
// 2. 异步通知：提升系统响应性能
// 3. 实时推送：支持库存状态实时同步到其他系统

// RocketMQ消息生产者配置
@Configuration
public class RocketMQConfig {
    
    @Value("${rocketmq.name-server}")
    private String nameServer;
    
    @Value("${rocketmq.producer.group}")
    private String producerGroup;
    
    @Bean
    public RocketMQTemplate rocketMQTemplate() {
        RocketMQTemplate template = new RocketMQTemplate();
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setSendMsgTimeout(3000);
        template.setProducer(producer);
        return template;
    }
}

// 库存变更消息实体
@Data
@AllArgsConstructor
public class InventoryChangeMessage {
    private String skuId;
    private int quantity;
    private OperationType operationType; // DECREASE, INCREASE, ADJUST
    private String warehouseId;
    private Date timestamp;
    private String operationId; // 用于幂等性控制
    
    public enum OperationType {
        DECREASE, INCREASE, ADJUST
    }
}

// 库存服务中集成消息发送
@Service
public class InventoryServiceImpl implements InventoryService {
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    @Autowired
    private TransactionManager transactionManager;
    
    @Autowired
    private RedissonClient redissonClient;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    private static final String INVENTORY_TOPIC = "inventory-change-topic";
    
    // 使用事务消息确保库存操作与消息发送的原子性
    @Override
    public void decreaseStock(String skuId, int quantity) {
        RLock lock = redissonClient.getLock("inventory:sku:" + skuId);
        try {
            if (lock.tryLock(100, 30000, TimeUnit.MILLISECONDS)) {
                // 构建库存变更消息
                InventoryChangeMessage message = new InventoryChangeMessage(
                    skuId, 
                    quantity, 
                    InventoryChangeMessage.OperationType.DECREASE,
                    "DEFAULT_WAREHOUSE", 
                    new Date(),
                    UUID.randomUUID().toString()
                );
                
                // 使用RocketMQ事务消息
                rocketMQTemplate.sendMessageInTransaction(
                    INVENTORY_TOPIC, 
                    MessageBuilder.withPayload(message).build(),
                    new LocalTransactionExecuter() {
                        @Override
                        public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                            try {
                                // 执行本地事务
                                transactionManager.begin();
                                try {
                                    // 执行库存扣减
                                    Inventory inventory = inventoryRepository.findBySkuId(skuId);
                                    if (inventory.getQuantity() < quantity) {
                                        return RocketMQLocalTransactionState.ROLLBACK_MESSAGE;
                                    }
                                    inventory.setQuantity(inventory.getQuantity() - quantity);
                                    inventoryRepository.save(inventory);
                                    
                                    // 记录操作日志
                                    saveOperationLog(message.getOperationId(), skuId, quantity, "DECREASE");
                                    
                                    // 提交事务
                                    transactionManager.commit();
                                    return RocketMQLocalTransactionState.COMMIT_MESSAGE;
                                } catch (Exception e) {
                                    // 回滚事务
                                    transactionManager.rollback();
                                    return RocketMQLocalTransactionState.ROLLBACK_MESSAGE;
                                }
                            } catch (Exception e) {
                                return RocketMQLocalTransactionState.ROLLBACK_MESSAGE;
                            }
                        }
                    }
                );
            } else {
                throw new RuntimeException("获取锁失败，库存操作无法执行");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("线程被中断，库存操作终止", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    
    // 记录操作日志，用于事务回查
    private void saveOperationLog(String operationId, String skuId, int quantity, String operationType) {
        InventoryOperationLog log = new InventoryOperationLog();
        log.setOperationId(operationId);
        log.setSkuId(skuId);
        log.setQuantity(quantity);
        log.setOperationType(operationType);
        log.setTimestamp(new Date());
        operationLogRepository.save(log);
    }
    
    // 事务状态回查监听器
    @RocketMQTransactionListener
    class InventoryTransactionListener implements RocketMQLocalTransactionListener {
        
        @Autowired
        private InventoryOperationLogRepository operationLogRepository;
        
        @Override
        public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
            try {
                // 从消息中获取操作ID
                InventoryChangeMessage message = JSON.parseObject(
                    new String((byte[]) msg.getPayload()), 
                    InventoryChangeMessage.class
                );
                
                // 查询操作日志确认事务状态
                boolean exists = operationLogRepository.existsByOperationId(message.getOperationId());
                return exists ? 
                    RocketMQLocalTransactionState.COMMIT_MESSAGE : 
                    RocketMQLocalTransactionState.ROLLBACK_MESSAGE;
            } catch (Exception e) {
                return RocketMQLocalTransactionState.UNKNOWN;
            }
        }
        
        @Override
        public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            // 由上面的匿名实现处理，这里不需要实现
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }
}

// 消息消费者示例 - 用于其他系统接收库存变更通知
@Component
@RocketMQMessageListener(
    topic = "inventory-change-topic", 
    consumerGroup = "inventory-change-consumer"
)
public class InventoryChangeConsumer implements RocketMQListener<InventoryChangeMessage> {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryChangeConsumer.class);
    
    @Autowired
    private CacheService cacheService; // 缓存服务，用于更新缓存
    
    @Autowired
    private NotificationService notificationService; // 通知服务，用于推送消息
    
    @Override
    public void onMessage(InventoryChangeMessage message) {
        try {
            logger.info("收到库存变更消息: {}", message);
            
            // 1. 更新本地缓存
            cacheService.updateInventoryCache(message.getSkuId(), message.getQuantity(), message.getOperationType());
            
            // 2. 推送实时通知
            notificationService.pushInventoryChange(message);
            
            // 3. 同步到其他相关系统
            syncToOtherSystems(message);
            
        } catch (Exception e) {
            logger.error("处理库存变更消息失败", e);
            // 根据业务需求决定是否重试或记录失败
        }
    }
    
    private void syncToOtherSystems(InventoryChangeMessage message) {
        // 同步到订单系统、商品系统等
        // 实现方式可以是HTTP调用、消息转发等
    }
}