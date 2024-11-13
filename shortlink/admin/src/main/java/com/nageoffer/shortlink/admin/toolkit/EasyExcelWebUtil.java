package com.nageoffer.shortlink.admin.toolkit;

import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * EasyExcel Web操作工具类
 * 
 * 该工具类封装了在Web环境下使用EasyExcel导出Excel文件的常用操作
 * 提供了简化的API接口，使得在Controller层可以方便地实现Excel导出功能
 */
public class EasyExcelWebUtil {

    /**
     * Excel文件导出方法
     * 
     * @param response HTTP响应对象，用于向客户端发送文件
     * @param fileName 导出的Excel文件名（不含扩展名）
     * @param clazz    Excel数据对应的实体类Class对象，用于确定导出的数据结构
     * @param data     需要导出的数据列表
     * 
     * 实现说明：
     * 1. 设置响应的Content-Type为Excel文件类型
     * 2. 设置字符编码为UTF-8
     * 3. 对文件名进行URL编码，确保中文文件名可以正确显示
     * 4. 设置文件下载的响应头，使浏览器将响应内容作为附件下载
     * 5. 使用EasyExcel将数据写入响应流
     */
    @SneakyThrows  // 使用Lombok注解自动处理异常
    public static void write(HttpServletResponse response, String fileName, Class<?> clazz, List<?> data) {
        // 设置响应的内容类型为Excel文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        // 设置响应的字符编码
        response.setCharacterEncoding("utf-8");
        // 对文件名进行URL编码，并将空格替换为%20
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        // 设置文件下载的响应头
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        // 使用EasyExcel将数据写入响应输出流，创建名为"Sheet"的工作表
        EasyExcel.write(response.getOutputStream(), clazz).sheet("Sheet").doWrite(data);
    }
}
