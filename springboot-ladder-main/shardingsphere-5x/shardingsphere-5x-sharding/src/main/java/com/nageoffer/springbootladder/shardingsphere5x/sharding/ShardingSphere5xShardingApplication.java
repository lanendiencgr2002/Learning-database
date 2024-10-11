package com.nageoffer.springbootladder.shardingsphere5x.sharding;

import com.alibaba.fastjson2.JSON;
import com.nageoffer.springbootladder.shardingspherecore.dao.entity.UserDO;
import com.nageoffer.springbootladder.shardingspherecore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@MapperScan("com.nageoffer.springbootladder.shardingspherecore.dao.mapper")
// 这里因为还引用了 SpringBoot-Ladder-ShardingSphere-Core 包，所以需要修改基础扫描路径
@SpringBootApplication(scanBasePackages = "com.nageoffer.springbootladder")
public class ShardingSphere5xShardingApplication {

    private final UserService userService;

    @PostMapping("/test/shardingsphere-jdbc/user/save")
    @Operation(summary = "ShardingSphere分片测试之用户新增")
    public String saveUser() {
        long userId = System.currentTimeMillis();
        UserDO userDO = UserDO.builder()
                .id(userId)
                .username("公众号@马丁玩编程")
                .idType(0)
                .idCard("110101202309305156")
                .phone("15601166692")
                .password("mqEc1mududm63JuxHmpm6jSUHy8xRRsL")
                .address("杭州市阿里巴巴园区")
                .createTime(new Date())
                .updateTime(new Date())
                .delFlag(0)
                .build();
        boolean saveResult = userService.save(userDO);
        return saveResult ? "新增用户返回成功，用户ID：" + userId : "新增用户返回失败";
    }

    @GetMapping("/test/shardingsphere-jdbc/user")
    @Operation(summary = "ShardingSphere分片测试之用户查询")
    public String findUserById(@RequestParam Long userId) {
        UserDO result = userService.getById(userId);
        return JSON.toJSONString(result);
    }

    public static void main(String[] args) {
        SpringApplication.run(ShardingSphere5xShardingApplication.class, args);
    }
}
