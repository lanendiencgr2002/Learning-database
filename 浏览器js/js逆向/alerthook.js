alert_ = alert; // 备份需要 hook 的方法
alert = function () {
   
    // 方法执行前执行的内容
    console.log('alert初始化');
    // 执行原函数
    alert_.apply(this, arguments);
    // 方法执行后执行的内容
    console.log('alert执行结束');
};
// 防止 hook 检测
alert.toString = function () {
   return "function alert() { [native code] }"}
