package org.example.redislock.service;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class InventoryService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${server.port}")
    private String port;

    public String sale(){
        String retMessage = "";

        String key = "lzxRedisLock";

        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue);

        //如果没抢到锁，递归重试
        if (!flag){
            //暂停20毫秒后递归调用
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            }catch (InterruptedException  e){
                e.printStackTrace();
            }
            sale();
        }else{
            try {
                //1.查询库存信息
                String reuslt = stringRedisTemplate.opsForValue().get("inventory001");
                //2.判断库存是否足够
                Integer inventoryNumber = reuslt == null?0:Integer.valueOf(reuslt);
                //3.扣减库存
                if (inventoryNumber>0){
                    stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                    retMessage = "成功卖出一个商品，库存剩余: "+inventoryNumber;
                    System.out.println(retMessage);
                }else{
                    retMessage = "商品卖完了，o(╥﹏╥)o";
                }
            }finally {
                //释放锁
                stringRedisTemplate.delete(key);
            }
        }
        return retMessage;
    }

  /*  private Lock lock = new ReentrantLock();

    public String sale(){
        String retMessage = "";
        lock.lock();

        try {
            //1.查询库存信息
            String reuslt = stringRedisTemplate.opsForValue().get("inventory001");
            //2.判断库存是否足够
            Integer inventoryNumber = reuslt == null?0:Integer.valueOf(reuslt);
            //3.扣减库存
            if (inventoryNumber>0){
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品，库存剩余: "+inventoryNumber;
                System.out.println(retMessage);
            }else{
                retMessage = "商品卖完了，o(╥﹏╥)o";
            }
        }finally {
            lock.unlock();
        }

        return retMessage;
    }*/
}
