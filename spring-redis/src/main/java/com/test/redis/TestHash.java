package com.test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

/**
 * @author congzi
 * @Description:
 * @create 2018-05-20
 * @Version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/applicationContext-redis.xml")
public class TestHash {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void setValue(){
        redisTemplate.boundHashOps("hash1").put("a","libai");
        redisTemplate.boundHashOps("hash1").put("b","dufu");
        redisTemplate.boundHashOps("hash1").put("c","wangzhihaun");
    }

    @Test
    public void getKey(){
        Set<Object> hash1 = redisTemplate.boundHashOps("hash1").keys();
        System.out.println(hash1);
    }

    @Test
    public void getValue(){
        List<Object> hash1 = redisTemplate.boundHashOps("hash1").values();
        System.out.println(hash1);
    }
    @Test
    public void del(){

        redisTemplate.boundHashOps("hash1").delete("c");
    }


    @Test
    public void getByKey(){

        Object o =(List<String>) redisTemplate.boundHashOps("hash1");
        System.out.println(o);
    }

}


