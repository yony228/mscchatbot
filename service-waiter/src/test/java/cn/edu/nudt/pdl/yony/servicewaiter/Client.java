package cn.edu.nudt.pdl.yony.servicewaiter;

import com.netflix.infix.TimeUtil;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * email: yony228@163.com
 * Created by yony on 17-12-25.
 */
public class Client {
        Socket socket = null;

        @Test
        public void start() {
                try {
                        byte[] buffer = new byte[4800];
                        socket = new Socket("localhost", 14800);
                        OutputStream os = socket.getOutputStream();
                        FileInputStream fis = new FileInputStream("/home/yony/Downloads/hebing.wav");
                        int length = 0;
                        while((length = fis.read(buffer, 0, 4800)) > 0) {
                                os.write(buffer, 0, length);
                                os.flush();
                        }
                        /*try {
                                Thread.sleep(100000);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }*/
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}
