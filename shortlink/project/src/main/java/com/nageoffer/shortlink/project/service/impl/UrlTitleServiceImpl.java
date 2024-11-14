package com.nageoffer.shortlink.project.service.impl;

import com.nageoffer.shortlink.project.service.UrlTitleService;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * URL 标题获取服务实现类
 * 该服务用于从指定URL获取网页的标题
 * 使用 HTTP 请求和 Jsoup 库实现网页标题的提取
 */
@Service
public class UrlTitleServiceImpl implements UrlTitleService {

    /**
     * 根据URL获取网页标题
     * @param url 需要获取标题的网页URL
     * @return 返回网页的标题，如果获取失败则返回错误信息
     * 
     * @SneakyThrows 注解用于自动处理可能发生的检查型异常，简化代码
     * 可能的异常包括：MalformedURLException, IOException 等
     */
    @SneakyThrows
    @Override
    public String getTitleByUrl(String url) {
        // 创建 URL 对象用于建立连接
        URL targetUrl = new URL(url);
        
        // 建立 HTTP 连接
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        // 设置请求方法为 GET
        connection.setRequestMethod("GET");
        // 建立实际的网络连接
        connection.connect();
        
        // 获取响应状态码
        int responseCode = connection.getResponseCode();
        
        // 检查响应是否成功（状态码 200）
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 使用 Jsoup 连接URL并获取网页文档对象
            Document document = Jsoup.connect(url).get();
            // 提取并返回网页标题
            return document.title();
        }
        // 如果响应不成功，返回错误信息
        return "Error while fetching title.";
    }
}
