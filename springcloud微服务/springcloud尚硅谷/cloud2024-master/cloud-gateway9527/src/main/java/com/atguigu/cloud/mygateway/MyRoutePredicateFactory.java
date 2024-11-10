package com.atguigu.cloud.mygateway;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.AfterRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * @auther zzyy
 * @create 2023-12-31 11:11
 * 需求说明：自定义配置会员等级userTpye，按照钻/金/银和yml配置的会员等级，以适配是否可以访问
 */
@Component
public class MyRoutePredicateFactory extends AbstractRoutePredicateFactory<MyRoutePredicateFactory.Config>
{

    // 调用父类的构造方法，传入config类的信息
    public MyRoutePredicateFactory()
    {
        super(MyRoutePredicateFactory.Config.class);
    }

    //这个Config类就是我们的路由断言规则，重要
    // 比如访问：http://localhost:9527/api/vip?userType=diamond 网关会检查userType参数
    // 如果不匹配或没有userType参数，就拒绝访问
    @Validated // 启用参数校验
    public static class Config
    {
        @Setter@Getter@NotEmpty
        private String userType; //检查userType参数 钻/金/银和yml配置的会员等级
    }
    
    /** 当有shortcutFieldOrder()时，可以简短格式 在配置文件中可以用简短格式 不然要用kv完整格式 ，配置了这个 两个格式都可以使用
     * # 简短格式(有shortcutFieldOrder()时)
     * predicates:
     *  - MyRoute=diamond
     * 
     * # 或完整格式
     * predicates:
     *  - name: MyRoute
     *    args:
     *      userType: diamond
     * 
     */
    // 处理短格式，比如 userType=diamond
    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("userType");
    }


    
    @Override
    public Predicate<ServerWebExchange> apply(MyRoutePredicateFactory.Config config)
    {
        return new Predicate<ServerWebExchange>()
        {
            @Override
            public boolean test(ServerWebExchange serverWebExchange)
            {
                //检查request的参数里面，userType是否为指定的值，符合配置就通过
                //http://localhost:9527/pay/gateway/get/1?userType=diamond
                // 是否存在userType参数
                String userType = serverWebExchange.getRequest().getQueryParams().getFirst("userType");
                if (userType == null) { // 不存在userType参数
                    return false; // 返回false，拒绝访问
                }
                //如果说参数存在，就和config的数据进行比较
                if(userType.equalsIgnoreCase(config.getUserType())){ //能匹配上对应值 config.getUserType() 是yml配置的会员等级 MyRoute=diamond
                    return true; // 返回true，允许访问
                }
                return false; // 返回false，拒绝访问
            }
        };
    }
}