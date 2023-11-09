package org.example.redislock.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.redislock.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "redis分布式锁测试")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;
    @ApiOperation("扣减库存，一次卖一个")
    @GetMapping("/inventory/sale")
    public String sale(){
        return inventoryService.sale();
    }

    @GetMapping("/inventory/salebyRedisson")
    public String saveByRedisson(){
        return inventoryService.saleByRedisson();
    }
}
