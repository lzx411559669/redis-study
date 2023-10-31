package com.example.redisstudy.controller;

import com.example.redisstudy.service.GuavaBloomFilterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "google工具Guava处理布隆过滤器")
@Slf4j
public class GuavaBloomFilterController {

    @Resource
    private GuavaBloomFilterService guavaBloomFilterService;

    @Operation(summary = "guava布隆过滤器插入100万样本数据并额外10W测试是否存在")
    @GetMapping("/guavafilter")
    public void guavaBloomFilter(){
        guavaBloomFilterService.guavaBloomFilter();
    }

}
