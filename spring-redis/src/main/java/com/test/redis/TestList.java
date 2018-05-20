package com.test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author congzi
 * @Description:
 * @create 2018-05-20
 * @Version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/applicationContext-redis.xml")
public class TestList {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 右压栈
     */
    @Test
    public void testSetValue(){
        redisTemplate.boundListOps("List1").rightPush("AJ");
        redisTemplate.boundListOps("List1").rightPush("CGX");
        redisTemplate.boundListOps("List1").rightPush("ZBZ");
    }

    @Test
    public  void testGet(){
        List<String> list1 = redisTemplate.boundListOps("List1").range(0, 100);
        System.out.println(list1);
    }
}
