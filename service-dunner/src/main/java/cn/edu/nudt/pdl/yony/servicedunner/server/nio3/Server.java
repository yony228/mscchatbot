package cn.edu.nudt.pdl.yony.servicedunner.server.nio3;

import java.net.InetSocketAddress;

/**
 * email: yony228@163.com
 * Created by yony on 18-1-15.
 */
public interface Server {

        public interface TransmissionProtocol{

        }

        // 服务器使用的协议
        public enum TRANSMISSION_PROTOCOL implements TransmissionProtocol {
                TCP,
                UDP
        }

        TransmissionProtocol getTransmissionProtocol();

        void startServer() throws Exception;

        void startServer(int port) throws Exception;

        void startServer(InetSocketAddress socketAddress) throws Exception;

        // 关闭服务器
        void stopServer() throws Exception;

        InetSocketAddress getSocketAddress();
}
