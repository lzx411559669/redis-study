package com.example.redisstudy.filter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BloomFilterInit {

    @Resource
    private RedisTemplate redisTemplate;
    @PostConstruct
    private void init(){
        //白名单客户预加载到布隆过滤器
        String id = "customer:11";
        //1 计算hashcode，由于可能有负数，直接取绝对值
        int hashValue = Math.abs(id.hashCode());
        //2 对2的32次方取余得到对应的下标坑位
        long index = (long)(hashValue % Math.pow(2,32));
        log.info(id+" 对应------坑位index:{}",index);
        //3 设置redis里面bitmap对应坑位，该有值设置为1
        redisTemplate.opsForValue().setBit("whitelistCustomer",index,true);
    }
}
