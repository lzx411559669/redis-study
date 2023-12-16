package com.example.redisstudy.controller;

import com.example.redisstudy.entity.User;
import com.example.redisstudy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/test")
    private String test(){
        new Thread(()->{
                userService.test1();
                },"Thread1").start();
        new Thread(()->{
            userService.test2();
        },"Thread2").start();
        return "test";
    }

    @GetMapping("/setuserAge")
    public List<User> setuserAge(){
        return userService.setUserAge();
    }

    @GetMapping("/getUsers")
    public List<User> getUsers(){
        return userService.queryUser();
    }
}
