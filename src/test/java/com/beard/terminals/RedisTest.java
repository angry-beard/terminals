package com.beard.terminals;

import com.beard.terminals.redis.vo.RedisVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;

/**
 * Created by angry_beard on 2020/7/20.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> strRedisTemplate;
    @Autowired
    private RedisTemplate<String, Serializable> serializableRedisTemplate;

    @Test
    public void testString() {
        strRedisTemplate.opsForValue().set("strKey", "zwqh");
        System.out.println(strRedisTemplate.opsForValue().get("strKey"));
    }

    @Test
    public void testSerializable() {
        RedisVo user = new RedisVo();
        user.setId(1L);
        user.setUserName("朝雾轻寒");
        user.setUserSex("男");
        serializableRedisTemplate.opsForValue().set("user", user);
        RedisVo user2 = (RedisVo) serializableRedisTemplate.opsForValue().get("user");
        System.out.println("user:" + user2.getId() + "," + user2.getUserName() + "," + user2.getUserSex());
    }

}
