package com.example.redisstudy.controller;

import com.example.redisstudy.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "OrderController",description = "订单接口")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/order/add")
    @Operation(summary = "添加订单")
    public void  addOrder(){
        orderService.addOrder();
    }

    @GetMapping("/order/{id}")
    @Operation(summary = "根据id获取订单")
    public String getOrder(@PathVariable("id") Integer keyNo){
        return  orderService.getOrderById(keyNo);
    }
}
