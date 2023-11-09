package com.example.redisstudy.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author liuzhengxing
 * @version v1.0
 * @package com.example.redisstudy.nio
 * @data 2023/11/9 8:00
 */
public class RedisServerNIO {
    static ArrayList<SocketChannel> channelList = new ArrayList<>();
    static ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    public static void main(String[] args) throws IOException {
        System.out.println("---------RedisServerNIO 启动等待中......");
        // 创建一个ServerSocketChannel对象
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定指定的端口号
        serverSocketChannel.bind(new java.net.InetSocketAddress("127.0.0.1",6379));
        // 设置Channel为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        // 无限循环，处理数据接收和客户端连接
        while (true) {
            // 遍历所有已连接的SocketChannel
            for (SocketChannel socketChannel : channelList) {
                // 从SocketChannel中读取数据到byteBuffer
                int read = socketChannel.read(byteBuffer);
                // 如果读取到数据
                if (read > 0) {
                    // 打印读取到的数据长度
                    System.out.println("-----读取数据: " + read);
                    // 将byteBuffer翻转，使其头部指向最近添加的数据
                    byteBuffer.flip();
                    // 创建一个byte数组，用于存储从byteBuffer中读取的数据
                    byte[] bytes = new byte[read];
                    // 将byteBuffer中的数据复制到byte数组
                    byteBuffer.get(bytes);
                    // 将byte数组转换为String并打印
                    System.out.println(new String(bytes));
                    // 清空byteBuffer
                    byteBuffer.clear();
                }
            }

            // 处理新的客户端连接
            SocketChannel socketChannel = serverSocketChannel.accept();

            if (socketChannel!= null) {
                System.out.println("-----成功连接: ");
                // 设置SocketChannel为非阻塞模式
                socketChannel.configureBlocking(false);
                // 将新的SocketChannel添加到channelList中
                channelList.add(socketChannel);
                // 打印客户端连接成功的消息
                System.out.println("-----客户端连接成功");
                // 打印channelList的大小
                System.out.println("channelList size" + channelList.size());
            }
        }

    }
}
