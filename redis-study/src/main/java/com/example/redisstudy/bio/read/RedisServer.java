package com.example.redisstudy.bio.read;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author liuzhengxing
 * @version v1.0
 * @package com.example.redisstudy.bio
 * @data 2023/11/9 7:37
 */
public class RedisServer {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(6379);
        while (true) {
            try {
                System.out.println("等待客户端连接");
                //阻塞1 ,等待客户端连接
                Socket socket = serverSocket.accept();
                System.out.println("-----222 成功连接");
                InputStream inputStream = socket.getInputStream();
                int length = -1;
                byte[] bytes = new byte[1024];
                //阻塞2 ,等待客户端发送数据
                while ((length = inputStream.read(bytes))!= -1){
                    System.out.println("-----444 成功读取"+new String(bytes,0,length));
                    System.out.println("====================");
                    System.out.println();
                }
                inputStream.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
