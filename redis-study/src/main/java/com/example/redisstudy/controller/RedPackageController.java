package com.example.redisstudy.controller;

import com.example.redisstudy.redPackageDemo.RedPackageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuzhengxing
 * @version v1.0
 * @package com.example.redisstudy.controller
 * @data 2023/11/10 10:55
 */
@RestController
@RequestMapping("redPackage")
public class RedPackageController {

    @Resource
    private RedPackageService redPackageService;

    @GetMapping("/send")
    public String sendRedPackage(int totalMoney,int redPackageNum) {
        return redPackageService.sendRedPackage(totalMoney,redPackageNum);
    }

    @GetMapping("/rob")
    public String robRedPackage(String key,String userId) {
        return redPackageService.robRedPackage(key,userId);
    }

}
