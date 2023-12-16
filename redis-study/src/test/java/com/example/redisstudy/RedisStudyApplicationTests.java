package com.example.redisstudy;

import com.example.redisstudy.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
class RedisStudyApplicationTests {

    @Resource
    private UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    void testThread() throws InterruptedException {
        int threadCount = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);

//        userService.setUserAge();
        for (int i = 1; i <= 1000; i++) {
            new Thread(() -> {
                userService.setUserAge();
            }, "thread--"+String.valueOf(i)).start();
        }
        // 等待所有线程完成
        latch.await();

    }

}
