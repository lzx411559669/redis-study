package com.example.redisstudy.service;

import com.example.redisstudy.entity.User;
import com.example.redisstudy.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {
    public static final String CACHE_KEY_USER = "user:";
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserMapper userMapper;

    /**
     * 业务逻辑没有写错，对于小厂中厂(QPS《=1000)可以使用，但是大厂不行
     * @param id
     * @return
     */
    public User findUserById(Integer id)
    {
        User user = null;
        String key = CACHE_KEY_USER+id;

        //1 先从redis里面查询，如果有直接返回结果，如果没有再去查询mysql
        user = (User) redisTemplate.opsForValue().get(key);

        if(user == null)
        {
            //2 redis里面无，继续查询mysql
            user = userMapper.selectOneById(id);
            log.info("user:{}",user.toString());
            if(user == null)
            {
                //3.1 redis+mysql 都无数据
                //你具体细化，防止多次穿透，我们业务规定，记录下导致穿透的这个key回写redis
                return user;
            }else{
                //3.2 mysql有，需要将数据写回redis，保证下一次的缓存命中率
                redisTemplate.opsForValue().set(key,user);
            }
        }
        return user;
    }

    /**
     * 加强补充，避免突然key失效了，打爆mysql，做一下预防，尽量不出现击穿的情况。
     * @param id
     * @return User
     */
    public User findUserById2(Integer id){
        User user = null;
        String key = CACHE_KEY_USER+id;
        //1 先从redis里面查询，如果有直接返回结果，如果没有再去查询mysql，
        // 第1次查询redis，加锁前
        user = (User)redisTemplate.opsForValue().get(key);
        if (user == null){
            //2 大厂用，对于高QPS的优化，进来就先加锁，保证一个请求操作，让外面的redis等待一下，避免击穿mysql
            synchronized (UserService.class){
                //第2次查询redis，加锁后
                user = (User)redisTemplate.opsForValue().get(key);
                //3 二次查redis还是null，可以去查mysql了(mysql默认有数据)
                if (user==null){
                    //4 查询mysql拿数据(mysql默认有数据)
                    user = userMapper.selectOneById(id);
                    if (user==null){
                        return null;
                    }else{
                        //5 mysql里面有数据的，需要回写redis，完成数据一致性的同步工作
                        redisTemplate.opsForValue().setIfAbsent(key,user,7L, TimeUnit.DAYS);
                    }
                }
            }
        }
        return user;
    }


}
