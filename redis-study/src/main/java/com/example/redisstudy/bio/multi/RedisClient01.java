package com.example.redisstudy.bio.multi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author liuzhengxing
 * @version v1.0
 * @package com.example.redisstudy.nio
 * @data 2023/11/9 7:44
 */
public class RedisClient01 {
    public static void main(String[] args) throws IOException {
        // 创建一个Socket对象，并连接到指定的IP地址和端口
        Socket socket = new Socket("127.0.01", 6379);
        // 获取Socket对象的输出流
        OutputStream outputStream = socket.getOutputStream();
        // 无限循环，接受用户输入的字符串
        while (true) {
            // 创建一个Scanner对象，用于从控制台读取输入
            Scanner scanner = new Scanner(System.in);
            // 读取下一个字符串
            String string = scanner.next();
            // 判断字符串是否为"quit"，如果是则向输出流写入"quit"并跳出循环
            if (string.equalsIgnoreCase("quit")) {
                outputStream.write(("quit".getBytes()));
                break;
            }
            // 否则向输出流写入输入的字符串
            outputStream.write(string.getBytes());
        }
        // 关闭输出流
        outputStream.close();
        // 关闭Socket连接
        socket.close();


    }
}
