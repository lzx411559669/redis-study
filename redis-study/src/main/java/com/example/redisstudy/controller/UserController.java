package com.example.redisstudy.controller;

import com.example.redisstudy.entity.User;
import com.example.redisstudy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/{id}")
    private User findById(@PathVariable("id")Integer id){
        return userService.findUserById(id);
    }

    @GetMapping("/v2/{id}")
    private User findById2(@PathVariable("id") Integer id){
        return userService.findUserById2(id);
    }
}
