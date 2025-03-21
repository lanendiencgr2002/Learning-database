// Redisson是一个基于Redis的Java客户端，提供了强大的分布式锁实现：
// 实现原理：Redisson使用Redis的SETNX命令和Lua脚本实现分布式锁
// 看门狗机制：自动延长锁的过期时间，防止任务执行时间过长导致锁过期
// 可重入特性：同一线程可以多次获取同一把锁
public void decreaseStock(String skuId, int quantity) {
    // 减库存
    RLock lock = redissonClient.getLock("inventory:sku:" + skuId);
    try {
        // 尝试获取锁，等待100毫秒，锁有效期30秒
        if (lock.tryLock(100, 30000, TimeUnit.MILLISECONDS)) {
            // 执行库存操作
            inventoryService.decreaseStock(skuId, quantity);
        }
    } finally {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
// 采用锁粒度控制策略防止资源竞争
// 锁粒度控制是指根据业务需求选择合适的锁定范围：
// 全局锁：锁定整个库存系统，适用于全局盘点等操作
// 仓库级锁：锁定特定仓库的所有操作，适用于仓库调拨等场景
// SKU级锁：只锁定特定商品，允许不同商品并行操作  行锁
// 复合锁：根据操作类型动态选择锁粒度
// 实现方式：
// 根据操作类型选择锁粒度
private RLock selectLock(InventoryOperation operation) {
    switch (operation.getType()) {
        case GLOBAL_CHECK:  // 全局锁
            return redissonClient.getLock("inventory:global");
        case WAREHOUSE_TRANSFER:  // 仓库级锁
            return redissonClient.getLock("inventory:warehouse:" + operation.getWarehouseId());
        case SKU_OPERATION:  // 商品级锁
            return redissonClient.getLock("inventory:sku:" + operation.getSkuId());
        default:  // 默认锁
            return redissonClient.getLock("inventory:default");
    }
}

// 针对热点商品采用库存分片技术提升并发处理能力
// 热点商品是指短时间内被大量请求的商品，如秒杀商品、促销商品等：
// 库存分片技术：
// 将单个商品的库存分散到多个分片中
// 每个请求只需锁定一个分片，而非整个库存
// 大幅提高并发处理能力
// 实现方式：
// 库存分片数量
private static final int INVENTORY_SHARDS = 10;
// 获取分片锁
public boolean decreaseStockWithSharding(String skuId, int quantity) {
    // 计算需要的分片数
    int shardsNeeded = Math.min(quantity, INVENTORY_SHARDS);
    List<RLock> locks = new ArrayList<>();
    try {
        // 尝试锁定足够的分片
        for (int i = 0; i < INVENTORY_SHARDS; i++) {
            RLock lock = redissonClient.getLock("inventory:sku:" + skuId + ":shard:" + i);
            if (lock.tryLock(10, TimeUnit.MILLISECONDS)) {
                locks.add(lock);
                if (locks.size() >= shardsNeeded) {
                    // 获得足够的分片，执行扣减
                    return executeDecrease(skuId, quantity, locks);
                }
            }
        }
        // 未获得足够的分片，释放已获得的锁
        return false;
    } finally {
        // 释放所有已获得的锁
        for (RLock lock : locks) {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}


// 使用try-finally结构确保锁释放与事务提交的正确顺序
// 锁释放与事务提交的顺序对数据一致性至关重要：
// 正确的顺序：
public void decreaseStock(String skuId, int quantity) {
    RLock lock = redissonClient.getLock("inventory:sku:" + skuId);
    try {
        if (lock.tryLock()) {
            // 开始事务
            transactionManager.begin();
            try {
                // 执行库存操作
                inventoryService.decreaseStock(skuId, quantity);
                // 提交事务
                transactionManager.commit();
            } catch (Exception e) {
                // 回滚事务
                transactionManager.rollback();
                throw e;
            }
        }
    } finally {
        // 确保锁释放
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}

// 通过锁超时自动释放机制防止死锁
// 死锁防止是分布式系统中的关键问题：
// 锁超时机制：
// 设置锁的最大持有时间，超时自动释放
// Redisson的看门狗机制会自动延长锁时间，但有上限
// 防止因程序崩溃导致锁永不释放
// 实现方式：
// 设置锁最大持有时间为30秒
// lock.tryLock(0, 30, TimeUnit.SECONDS);
// 锁获取超时：
// 设置获取锁的最大等待时间
// 防止线程长时间阻塞
// 最多等待100毫秒获取锁
/*
    if (!lock.tryLock(100, TimeUnit.MILLISECONDS)) {
    // 获取锁超时，执行备选逻辑
    handleLockTimeout();
    }
*/

// 保障数据一致性的综合措施
// 除了上述技术外，还采用以下措施保障数据一致性：
// 乐观锁与版本控制：
@Version
private Long version;

// 使用JPA的乐观锁机制
@Transactional
public void updateStock(Long skuId, int quantity) {
    InventoryEntity inventory = repository.findById(skuId).orElseThrow();
    inventory.setQuantity(inventory.getQuantity() - quantity);
    repository.save(inventory);
    // 如果版本不匹配，JPA会抛出异常
}
// 库存操作幂等性设计：
   // 使用操作ID确保幂等性
@Transactional
public boolean decreaseStock(String operationId, Long skuId, int quantity) {
    // 检查操作是否已执行
    if (operationRepository.existsById(operationId)) {
        return true; // 操作已执行，直接返回成功
    }
    
    // 执行库存扣减
    boolean result = doDecreaseStock(skuId, quantity);
    
    // 记录操作ID
    operationRepository.save(new OperationRecord(operationId));
    
    return result;
}
分布式事务：对于跨多个资源的操作，使用XA事务或TCC模式确保一致性
通过这些综合措施，库存系统能够在高并发环境下保持数据一致性，同时提供高性能的库存操作服务。


