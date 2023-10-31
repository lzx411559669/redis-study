package com.example.redisstudy.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CheckUtils {
    @Resource
    private RedisTemplate redisTemplate;

    public boolean checkWithBloomFilter(String checkItem,String key){
        int hashValue = Math.abs(key.hashCode());
        long index = (long) (hashValue % Math.pow(2,32));
        Boolean existOk = redisTemplate.opsForValue().getBit(checkItem, index);
        log.info("----->key:"+key+"\t对应坑位index:"+index+"\t是否存在:"+existOk);
        return existOk;
    }
}
