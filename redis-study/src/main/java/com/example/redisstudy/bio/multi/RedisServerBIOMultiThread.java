package com.example.redisstudy.bio.multi;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author liuzhengxing
 * @version v1.0
 * @package com.example.redisstudy.nio
 * @data 2023/11/9 7:44
 */
public class RedisServerBIOMultiThread {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6379);
        while (true) {
            Socket socket = serverSocket.accept();
            // 创建一个新的线程
            new Thread(() -> {
                try {
                    // 获取socket的输入流
                    InputStream inputStream = socket.getInputStream();
                    int length = -1;
                    byte[] bytes = new byte[1024];
                    System.out.println("-----333 等待读取");

                    // 循环读取输入流直到结束
                    while ((length = inputStream.read(bytes)) != -1) {
                        System.out.println("-----444 成功读取\n" + new String(bytes, 0, length));
                    }

                    // 关闭输入流和socket
                    inputStream.close();
                    socket.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, Thread.currentThread().getName()).start();

            System.out.println(Thread.currentThread().getName());
        }
    }
}
