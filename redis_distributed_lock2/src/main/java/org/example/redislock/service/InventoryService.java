package org.example.redislock.service;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.redislock.mylock.DistributedLockFactory;
import org.example.redislock.mylock.RedisDistributedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
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

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private RedissonClient redissonClient;


    //v9.1版本，使用redisson
    public String saleByRedisson(){
        String key = "lzxRedisLock";
        RLock redissonLock = redissonClient.getLock(key);
        String retMessage = "";
        redissonLock.lock();

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
            if(redissonLock.isLocked() && redissonLock.isHeldByCurrentThread())
            {
                redissonLock.unlock();
            }
        }
        return retMessage;
    }

  /*  //v9.0版本，使用redisson
    public String saleByRedisson(){
        String key = "lzxRedisLock";
        RLock redissonLock = redissonClient.getLock(key);
        String retMessage = "";
        redissonLock.lock();

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
            redissonLock.unlock();
        }
        return retMessage;
    }*/
    // V8.0版本，自动续期时间
    public String sale(){

        Lock lock =distributedLockFactory.getDistributedLock("REDIS");

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
                //暂停几秒钟线程,为了测试自动续期
                try {
                    TimeUnit.SECONDS.sleep(120);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }else{
                retMessage = "商品卖完了，o(╥﹏╥)o";
            }
        }finally {
            lock.unlock();
        }
        return retMessage;

    }

 /* V7.x版本

 public String sale(){

        Lock lock =distributedLockFactory.getDistributedLock("REDIS");

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
                this.testReEnter();
            }else{
                retMessage = "商品卖完了，o(╥﹏╥)o";
            }
        }finally {
            lock.unlock();
        }

        return retMessage;

    }*/

    private void testReEnter()
    {
        Lock redisLock = distributedLockFactory.getDistributedLock("redis");
        redisLock.lock();
        try
        {
            System.out.println("################测试可重入锁#######");
        }finally {
            redisLock.unlock();
        }
    }

    /*  v6.0
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
            //V6.0 将判断+删除自己的合并为lua脚本保证原子性
            String script = "if (redis.call('get',KEYS[1])==ARGV[1]) then " +
                                "return redis.call('del',KEYS[1])" +
                            " else " +
                                "return 0 " +
                            "end";
           stringRedisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(key), uuidValue);
        }
        return retMessage+"\t"+"服务端口号："+port;
    }
*/


/*  v5.0版本，finally，不是原子性，需要使用lua脚本保证原子性
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
    }*/



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
