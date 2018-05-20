package com.test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author congzi
 * @Description:
 * @create 2018-05-20
 * @Version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/applicationContext-redis.xml")
public class TestSet {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void testSetValue(){

        redisTemplate.boundSetOps("SET1").add("AJ");
        redisTemplate.boundSetOps("SET1").add("CGX");
        redisTemplate.boundSetOps("SET1").add("ZBZ");

    }

    @Test
    public void remove(){

        redisTemplate.boundSetOps("SET1").remove("ZBZ");

    }
}
