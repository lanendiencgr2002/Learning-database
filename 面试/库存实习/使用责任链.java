public class 使用责任链 {
    
}
public class InventoryManager {
    private static volatile InventoryManager instance;
    private final InventoryHandler inventoryChain;
    
    private InventoryManager() {
        // 初始化责任链
        this.inventoryChain = InventoryChainBuilder.buildChain();
    }
    
    // 获取单例实例的方法...
    
    public void processInventoryOperation(InventoryOperation operation) {
        // 启动责任链处理
        inventoryChain.handle(operation);
    }
}