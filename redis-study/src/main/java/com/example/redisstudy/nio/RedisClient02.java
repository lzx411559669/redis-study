package com.example.redisstudy.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author liuzhengxing
 * @version v1.0
 * @package com.example.redisstudy.nio
 * @data 2023/11/9 8:11
 */
public class RedisClient02 {
    public static void main(String[] args) throws IOException {
        System.out.println("------RedisClient02 start");
        // 创建一个Socket对象，连接到指定的主机和端口
        Socket socket = new Socket("127.0.0.1", 6379);
        // 获取输出流，用于向服务器发送数据
        OutputStream outputStream = socket.getOutputStream();
        // 无限循环，用于持续接收用户输入并发送给服务器
        while (true) {
            // 创建一个Scanner对象，用于从控制台读取用户输入
            Scanner scanner = new Scanner(System.in);
            // 读取控制台输入的一行字符串
            String string = scanner.next();
            // 打印发送给服务器的数据
            System.out.println("------RedisClient01 send:" + string);
            // 如果输入的字符串为"quit"，则跳出循环，结束程序执行
            if (string.equalsIgnoreCase("quit")) {
                break;
            }
            // 将输入的字符串转换为字节数组，并写入输出流中发送给服务器
            outputStream.write(string.getBytes());
            // 在控制台打印提示信息，输入"quit"关键字以完成任务
            System.out.println("input quit keyword to finish...");
        }
        // 关闭输出流
        outputStream.close();
        // 关闭Socket连接
        socket.close();

    }
}
