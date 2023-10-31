package com.example.redisstudy.luttuceDemo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SortArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class luttuceDemo {
    public static void main(String[] args) {
        //使用构建器 RedisURI.builder
        RedisURI uri = RedisURI.builder()
                .redis("192.168.233.128")
                .withPort(6379)
                .withAuthentication("default","111111")
                .build();
        //创建连接客户端
        RedisClient client = RedisClient.create(uri);
        StatefulRedisConnection conn = client.connect();
        //操作命令api
        RedisCommands<String,String> commands = conn.sync();

        //keys
        List<String> list = commands.keys("*");
        for(String s : list) {
            log.info("key:{}",s);
        }
        //String
        commands.set("k1","1111");
        String s1 = commands.get("k1");
        System.out.println("String s ==="+s1);

        //list
        commands.lpush("myList2", "v1","v2","v3");
        List<String> list2 = commands.lrange("myList2", 0, -1);
        for(String s : list2) {
            System.out.println("list ssss==="+s);
        }
        //set
        commands.sadd("mySet2", "v1","v2","v3");
        Set<String> set = commands.smembers("mySet2");
        for(String s : set) {
            System.out.println("set ssss==="+s);
        }
        //hash
        Map<String,String> map = new HashMap<>();
        map.put("k1","138xxxxxxxx");
        map.put("k2","atguigu");
        map.put("k3","zzyybs@126.com");//课后有问题请给我发邮件

        commands.hmset("myHash2", map);
        Map<String,String> retMap = commands.hgetall("myHash2");
        for(String k : retMap.keySet()) {
            System.out.println("hash  k="+k+" , v=="+retMap.get(k));
        }

        //zset
        commands.zadd("myZset2", 100.0,"s1",110.0,"s2",90.0,"s3");
        List<String> list3 = commands.zrange("myZset2",0,10);
        for(String s : list3) {
            System.out.println("zset ssss==="+s);
        }

        //sort
        SortArgs sortArgs = new SortArgs();
        sortArgs.alpha();
        sortArgs.desc();

        List<String> list4 = commands.sort("myList2",sortArgs);
        for(String s : list4) {
            System.out.println("sort ssss==="+s);
        }

        //关闭
        conn.close();
        client.shutdown();
    }

}
