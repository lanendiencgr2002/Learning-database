var my_stringify = JSON.stringify;
JSON.stringify = function (params) {
    //这里可以添加其他逻辑比如 
    debugger
    console.log("json_stringify params:",params);
    return my_stringify(params);
};
