package com.example.redisstudy.jedisDemo;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class JedisDemo {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.233.128", 6379);
        jedis.auth("111111");

        log.info("redis conn status:{}","连接成功");
        log.info("redis ping retvalue:{}",jedis.ping());

        jedis.set("k1","jedis");
        log.info("k1 value:{}",jedis.get("k1"));


    }
}
