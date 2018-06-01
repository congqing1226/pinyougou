package com.pinyougou.sms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author congzi
 * @Description:
 * @create 2018-06-01
 * @Version 1.0
 */
@RestController
public class HelloController {


    @RequestMapping("/hello")
    public String hello(){
        return "<h1>helloWord!</h1>";
    }
}
