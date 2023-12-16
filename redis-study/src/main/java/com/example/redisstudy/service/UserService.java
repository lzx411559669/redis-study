package com.example.redisstudy.service;

import cn.hutool.core.collection.CollectionUtil;
import com.example.redisstudy.entity.User;
import com.example.redisstudy.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
     *
     * @param id
     * @return
     */
    public User findUserById(Integer id) {
        User user = null;
        String key = CACHE_KEY_USER + id;

        //1 先从redis里面查询，如果有直接返回结果，如果没有再去查询mysql
        user = (User) redisTemplate.opsForValue().get(key);

        if (user == null) {
            //2 redis里面无，继续查询mysql
            user = userMapper.selectOneById(id);
            log.info("user:{}", user.toString());
            if (user == null) {
                //3.1 redis+mysql 都无数据
                //你具体细化，防止多次穿透，我们业务规定，记录下导致穿透的这个key回写redis
                return user;
            } else {
                //3.2 mysql有，需要将数据写回redis，保证下一次的缓存命中率
                redisTemplate.opsForValue().set(key, user);
            }
        }
        return user;
    }

    /**
     * 加强补充，避免突然key失效了，打爆mysql，做一下预防，尽量不出现击穿的情况。
     *
     * @param id
     * @return User
     */
    public User findUserById2(Integer id) {
        User user = null;
        String key = CACHE_KEY_USER + id;
        //1 先从redis里面查询，如果有直接返回结果，如果没有再去查询mysql，
        // 第1次查询redis，加锁前
        user = (User) redisTemplate.opsForValue().get(key);
        if (user == null) {
            //2 大厂用，对于高QPS的优化，进来就先加锁，保证一个请求操作，让外面的redis等待一下，避免击穿mysql
            synchronized (UserService.class) {
                //第2次查询redis，加锁后
                user = (User) redisTemplate.opsForValue().get(key);
                //3 二次查redis还是null，可以去查mysql了(mysql默认有数据)
                if (user == null) {
                    //4 查询mysql拿数据(mysql默认有数据)
                    user = userMapper.selectOneById(id);
                    if (user == null) {
                        return null;
                    } else {
                        //5 mysql里面有数据的，需要回写redis，完成数据一致性的同步工作
                        redisTemplate.opsForValue().setIfAbsent(key, user, 7L, TimeUnit.DAYS);
                    }
                }
            }
        }
        return user;
    }

    private Lock lock = new ReentrantLock();

    public synchronized void test1() {
//        lock.lock();
//        try {
        System.out.println(System.currentTimeMillis());
        System.out.println("我是test1");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        } finally {
//            lock.unlock();
//        }


    }

    private Lock lock2 = new ReentrantLock();

    public synchronized void test2() {
//        lock2.lock();
//        try {
        System.out.println(System.currentTimeMillis());
        System.out.println("我是test2");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        } finally {
//            lock2.unlock();
//        }

    }

    private List<User> users = null;

    private Lock lock3 = new ReentrantLock();

//    public List<User> setUserAge() {
//        lock3.lock();
//        try {
//            if (users == null || CollectionUtil.isEmpty(users)) {
//                users = userMapper.selectAll();
//            }
//            User user = users.get(0);
//            // 使用AtomicInteger确保递增操作是原子的
//            user.setAge(user.getAge() + 1);
//            System.out.println("-----" + user.getAge() + "-----" + user.isSex());
//            return users;
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//            return users;
//        } finally {
//            lock3.unlock();
//        }
//    }

    public synchronized   List<User> setUserAge() {
        if (users == null || CollectionUtil.isEmpty(users)) {

            users = userMapper.selectAll();
        }
        User user = users.get(0);
        // 使用AtomicInteger确保递增操作是原子的
        user.setAge(user.getAge() + 1);

        System.out.println(Thread.currentThread().getName() + "-----" + user.getAge() + "-----" + user.isSex());
        return users;
    }

//    public List<User> setUserAge() {
//        if (users == null || CollectionUtil.isEmpty(users)) {
//            users = userMapper.selectAll();
//        }
//        User user;
//        synchronized (users) {
//            user = users.get(0);
//            // 在synchronized块中递增年龄
//            user.setAge(user.getAge() + 1);
//        }
//        System.out.println("-----" + user.getAge() + "-----" + user.isSex());
//        return users;
//    }

    public List<User> queryUser() {
        return users;
    }
}
