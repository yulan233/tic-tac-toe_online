package com.example.zouye_4.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class TokenManage {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //设置token
    public boolean setToken(int id,String token){
        boolean ok=false;
        if (redisTemplate!=null){
            redisTemplate.opsForValue().set(Integer.toString(id),token, 60 * 60 * 3, TimeUnit.SECONDS);
            if (Objects.equals(redisTemplate.opsForValue().get(Integer.toString(id)), token)){
                ok=true;
            }
        }
        return ok;
    }
    //验证token
    public boolean verifyToken(int id,String token){
        if (redisTemplate!=null){
            return Objects.equals(redisTemplate.opsForValue().get(Integer.toString(id)), token);
        }
        return false;
    }
}
