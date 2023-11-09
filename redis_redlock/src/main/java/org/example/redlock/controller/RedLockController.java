package org.example.redlock.controller;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class RedLockController {

    public static final String CACHE_KEY_REDLOCK = "LZX_REDLOCK";


    @Autowired
    private RedissonClient redissonClient1;
    @Autowired
    private RedissonClient redissonClient2;
    @Autowired
    private RedissonClient redissonClient3;

    boolean isLockBoolean;

    @GetMapping("/mutiLock")
    public String getMutiLock(){
        String uuid = IdUtil.simpleUUID();
        String uuidValue = uuid+Thread.currentThread().getId();

        RLock lock1 = redissonClient1.getLock(CACHE_KEY_REDLOCK);

        RLock lock2 = redissonClient2.getLock(CACHE_KEY_REDLOCK);

        RLock lock3 = redissonClient3.getLock(CACHE_KEY_REDLOCK);

        RedissonMultiLock redLock = new RedissonMultiLock(lock1,lock2,lock3);

        redLock.tryLock();
        try {
            System.out.println(uuidValue+"\t"+"---come in biz multiLock");
            try {
                TimeUnit.SECONDS.sleep(30);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("multiLock exception:{}",e.getCause()+"\t"+e.getMessage());
        }finally {
            redLock.unlock();
            log.info("释放分布式锁：{}",CACHE_KEY_REDLOCK);
        }
        return "multiLock task is voer"+uuidValue;

    }
}
