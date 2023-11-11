package com.example.redisstudy.bio.accept;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.SocketHandler;

/**
 * @author liuzhengxing
 * @version v1.0
 * @package com.example.redisstudy.bio
 * @data 2023/11/9 7:37
 */
public class RedisServer {
    public static void main(String[] args) throws IOException {
        Byte[] bytes = new Byte[1024];
        ServerSocket serverSocket = new ServerSocket(6379);
        while (true) {
            try {
                System.out.println("等待客户端连接");
                Socket accept = serverSocket.accept();
                System.out.println("-----222 成功连接");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
