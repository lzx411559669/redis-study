package com.example.redisstudy.bio.read;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

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
        OutputStream outputStream = socket.getOutputStream();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String string = scanner.next();
            if (string.equalsIgnoreCase("quit")) {
                break;
            }
            byte[] bytes = string.getBytes();
            outputStream.write(bytes);
            System.out.println("------RedisClient01 send msg:" + string);
        }
        outputStream.close();
        socket.close();
    }
}
