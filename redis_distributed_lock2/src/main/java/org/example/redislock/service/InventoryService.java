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

    public String sale()    {
        String retMessage = "";
        String key = "lzxRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();
        while(!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue,30L,TimeUnit.SECONDS)){
            //暂停20毫秒，类似CAS自旋
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存
            if(inventoryNumber > 0) {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品，库存剩余: "+inventoryNumber;
                System.out.println(retMessage);
            }else{
                retMessage = "商品卖完了，o(╥﹏╥)o";
            }
        }finally {
            // v5.0判断加锁与解锁是不是同一个客户端，同一个才行，自己只能删除自己的锁，不误删他人的
            if(stringRedisTemplate.opsForValue().get(key).equalsIgnoreCase(uuidValue)){
                stringRedisTemplate.delete(key);
            }
        }
        return retMessage+"\t"+"服务端口号："+port;
    }



/*  递归容易导致StackOverflowError
    public String sale(){
        String retMessage = "";
        String key = "lzxRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue);
        if(!flag){
            //暂停20毫秒后递归调用
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
            sale();
        }else{
            try{
                //1 查询库存信息
                String result = stringRedisTemplate.opsForValue().get("inventory001");
                //2 判断库存是否足够
                Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
                //3 扣减库存
                if(inventoryNumber > 0) {
                    stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                    retMessage = "成功卖出一个商品，库存剩余: "+inventoryNumber;
                    System.out.println(retMessage);
                }else{
                    retMessage = "商品卖完了，o(╥﹏╥)o";
                    System.out.println(retMessage);
                }
            }finally {
                stringRedisTemplate.delete(key);
            }
        }
        return retMessage+"\t"+"服务端口号："+port;
    }*/

/*   v2.0版本 单机版 ReentrantLock
    private Lock lock = new ReentrantLock();

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
