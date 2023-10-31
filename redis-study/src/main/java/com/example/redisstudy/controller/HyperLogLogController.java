package com.example.redisstudy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther zzyy
 * @create 2021-05-02 18:16
 */
@Tag(name="HyperLogLogController",description = "淘宝亿级UV的Redis统计方案")
@RestController
@Slf4j
public class HyperLogLogController
{
    @Resource
    private RedisTemplate redisTemplate;

    @Operation(summary = "获得IP去重后的首页访问量")
    @RequestMapping(value = "/uv",method = RequestMethod.GET)
    public long uv()
    {
        //pfcount
        return redisTemplate.opsForHyperLogLog().size("hll");
    }

}
