package org.example.redislock.mylock;

import cn.hutool.core.util.IdUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

@Component
public class DistributedLockFactory {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String lockName;

    private String uuidValue;

    public DistributedLockFactory() {
        this.uuidValue = IdUtil.simpleUUID();
    }

    public Lock getDistributedLock(String lockType){
        if(StringUtil.isNullOrEmpty(lockType)) return null;
        if (lockType.equalsIgnoreCase("REDIS")){
            lockName = "lzxRedisLock";
            return new RedisDistributedLock(stringRedisTemplate,lockName,uuidValue);
        } else if (lockType.equalsIgnoreCase("ZOOKEEPER")) {
            //TODO zookeeper版本的分布式锁实现
            return new ZookeeperDistributedLock();
        } else if (lockType.equalsIgnoreCase("MYSQL")) {
            //TODO mysql版本的分布式锁实现
            return null;
        }
        return null;

    }
}
