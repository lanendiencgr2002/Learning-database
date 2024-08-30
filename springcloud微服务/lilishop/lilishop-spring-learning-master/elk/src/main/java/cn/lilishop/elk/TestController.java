package cn.lilishop.elk;

import org.springframework.web.bind.annotation.*;


    @RestController
    public class TestController {

        @GetMapping
        public void test() {

            //抛出异常
            System.out.println(1 / 0);
        }

    }
