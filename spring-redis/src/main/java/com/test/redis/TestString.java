package com.test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
public class TestString {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testSetValue(){
        stringRedisTemplate.boundValueOps("name").set("itheima");
    }

    @Test
    public void testGetValue(){

        String s = stringRedisTemplate.boundValueOps("name").get();
        System.out.println(s);
    }









}
