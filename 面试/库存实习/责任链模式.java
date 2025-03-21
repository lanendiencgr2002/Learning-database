// 处理器接口
public interface InventoryHandler {
    void setNext(InventoryHandler next);
    void handle(InventoryOperation operation);
}

// 抽象处理器基类
public abstract class AbstractInventoryHandler implements InventoryHandler {
    protected InventoryHandler nextHandler;
    
    @Override
    public void setNext(InventoryHandler next) {
        this.nextHandler = next;
    }
    
    // 传递给下一个处理器
    protected void passToNext(InventoryOperation operation) {
        if (nextHandler != null) {
            nextHandler.handle(operation);
        }
    }
}

// 具体处理器示例
public class ValidationHandler extends AbstractInventoryHandler {
    @Override
    public void handle(InventoryOperation operation) {
        // 验证库存操作的合法性
        boolean isValid = validateOperation(operation);
        if (isValid) {
            passToNext(operation);
        } else {
            throw new InvalidOperationException("Invalid inventory operation");
        }
    }
    
    private boolean validateOperation(InventoryOperation operation) {
        // 验证逻辑
        return true;
    }
}

public class LockingHandler extends AbstractInventoryHandler {
    // 1. 解决并发问题 
    // 2. 保证数据一致性 防止超卖 防止库存错误 保证库存操作的原子性
    // 3. 支持复杂的库存操作 
    // 库存预占：为订单锁定库存但暂不扣减
    // 库存冻结：临时冻结部分库存用于特定活动
    // 库存盘点：核对实际库存与系统记录
    // 4. 分布式系统的挑战
    // 多服务实例可能同时操作库存
    // 跨数据中心的库存同步
    // 微服务架构下的库存一致性保证
    // 5. 库存锁的实现
    // 锁获取与释放分离：使用try-finally确保无论处理过程是否异常，锁都会被释放
    // 锁粒度控制：可以根据operation参数实现不同粒度的锁（如SKU级、仓库级）
    // 锁超时处理：可以在acquireLock中实现锁获取超时逻辑
    // 责任分离：锁逻辑与业务逻辑分离，提高代码可维护性
    // 6.实际应用中的库存锁实现
    // 数据库锁：8
    // 悲观锁：SELECT ... FOR UPDATE
    // 乐观锁：基于版本号或时间戳
    // 分布式锁：
    // Redis实现：SETNX + 过期时间
    // Zookeeper实现：临时节点
    // Redisson框架：提供更完善的分布式锁实现
    // 内存锁：
    // Java的synchronized或ReentrantLock
    // 适用于单机部署场景
    // 7. 库存锁的优化策略
    // 高性能系统中常用的库存锁优化策略：
    // 细粒度锁：按SKU或仓库分别加锁，而非全局锁
    // 锁分段：将库存分段管理，减少锁竞争
    // 读写分离：查询操作不加锁，修改操作加锁
    // 异步处理：非关键路径使用消息队列异步处理
    // 本地缓存：减少锁操作频率 
    @Override
    public void handle(InventoryOperation operation) {
        // 获取库存锁 
        boolean locked = acquireLock(operation);
        if (locked) {
            try {
                passToNext(operation);
            } finally {
                // 确保锁释放
                releaseLock(operation);
            }
        } else {
            throw new LockAcquisitionException("Failed to acquire inventory lock");
        }
    }
    
    private boolean acquireLock(InventoryOperation operation) {
        // 锁定逻辑
        return true;
    }
    
    private void releaseLock(InventoryOperation operation) {
        // 释放锁逻辑
    }
}

public class OperationHandler extends AbstractInventoryHandler {
    @Override
    public void handle(InventoryOperation operation) {
        // 执行实际的库存操作
        executeOperation(operation);
        passToNext(operation);
    }
    
    private void executeOperation(InventoryOperation operation) {
        // 根据操作类型执行不同的库存操作
        switch (operation.getType()) {
            case IN:
                processInbound(operation);
                break;
            case OUT:
                processOutbound(operation);
                break;
            case LOCK:
                processLocking(operation);
                break;
            case UNLOCK:
                processUnlocking(operation);
                break;
        }
    }
    
    // 各种操作的具体实现方法...
}

public class RecordingHandler extends AbstractInventoryHandler {
    @Override
    public void handle(InventoryOperation operation) {
        // 记录库存操作日志
        recordOperation(operation);
        passToNext(operation);
    }
    
    private void recordOperation(InventoryOperation operation) {
        // 记录操作到库存流水表
    }
}

public class NotificationHandler extends AbstractInventoryHandler {
    @Override
    public void handle(InventoryOperation operation) {
        // 发送库存变更通知
        sendNotification(operation);
    }
    
    private void sendNotification(InventoryOperation operation) {
        // 发送消息到消息队列
    }
}

// 责任链构建
public class InventoryChainBuilder {
    public static InventoryHandler buildChain() {
        ValidationHandler validationHandler = new ValidationHandler();
        LockingHandler lockingHandler = new LockingHandler();
        OperationHandler operationHandler = new OperationHandler();
        RecordingHandler recordingHandler = new RecordingHandler();
        NotificationHandler notificationHandler = new NotificationHandler();
        
        validationHandler.setNext(lockingHandler);
        lockingHandler.setNext(operationHandler);
        operationHandler.setNext(recordingHandler);
        recordingHandler.setNext(notificationHandler);
        
        return validationHandler;
    }
}