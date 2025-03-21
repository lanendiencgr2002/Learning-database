// 重写 alert 为空函数
alert = function () {
    // 不执行任何操作
    return;
};

// 防止 hook 检测
alert.toString = function () {
    return "function alert() { [native code] }";
}; 