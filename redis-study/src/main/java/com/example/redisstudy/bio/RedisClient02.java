package com.example.redisstudy.bio;

import java.io.IOException;
import java.net.Socket;

/**
 * @author liuzhengxing
 * @version v1.0
 * @package com.example.redisstudy.bio
 * @data 2023/11/9 7:38
 */
public class RedisClient02 {
    public static void main(String[] args) throws IOException {
        System.out.println("------RedisClient02 start");
        Socket socket = new Socket("127.0.0.1", 6379);
    }
}
