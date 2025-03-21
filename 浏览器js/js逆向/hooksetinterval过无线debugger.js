setInterval_ = setInterval;
// hook setInterval定时器
setInterval = function (func, timer) {
      
    // 若定时器函数中不包含debugger关键字，则正常执行定时器，否则不执行
    if (func.toString().indexOf('debugger') === -1) {
      
        setInterval_(func, timer)
    } else {
      
        console.log('检测到无限debugger， 已绕过!')
    }
};
setInterval.toString = function () {
      
    return "function setInterval() { [native code] }"
}