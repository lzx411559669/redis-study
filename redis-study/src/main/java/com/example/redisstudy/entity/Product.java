package com.example.redisstudy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product
{
    //产品ID
    private Long id;
    //产品名称
    private String name;
    //产品价格
    private Integer price;
    //产品详情
    private String detail;
}
