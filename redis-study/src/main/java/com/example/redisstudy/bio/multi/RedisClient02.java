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
public class RedisClient02 {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.01", 6379);
        OutputStream outputStream = socket.getOutputStream();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String string = scanner.next();
            if (string.equalsIgnoreCase("quit")) {
                outputStream.write(("quit".getBytes()));
                break;
            }
            outputStream.write(string.getBytes());
        }
        outputStream.close();
        socket.close();

    }
}
