package cn.edu.nudt.pdl.yony.servicesealifevisitor.listerner;

import cn.edu.nudt.pdl.yony.servicesealifevisitor.server.VisitorServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * email: yony228@163.com
 * Created by yony on 17-12-26.
 */
@Component
public class MessageListener implements ApplicationListener {

        @Autowired
        private VisitorServer visitorServer;

//        @Autowired
//        private NioVisitorServer nioVisitorServer;

        public enum ServerType {
                NIO_NETTY4_TCP("nio_netty4_tcp"),
                BIO_TCP("bio_tcp");

                private String name;
                ServerType(String name) {
                        this.name = name;
                }
        }

        @Value("#{T(cn.edu.nudt.pdl.yony.servicesealifevisitor.listerner.MessageListener.ServerType).valueOf('${self.server.type}'.toUpperCase())}")
        private ServerType serverType;

        @Async
        @Override
        public void onApplicationEvent(ApplicationEvent applicationEvent) {
                if (applicationEvent instanceof ApplicationReadyEvent) {
                        switch (serverType) {
                                case NIO_NETTY4_TCP:
//                                        nioVisitorServer.start(null);
                                        break;
                                case BIO_TCP:
                                        try {
                                                visitorServer.start();
                                        } catch (Exception e) {
                                                e.printStackTrace();
                                        }
                                        break;
                        }
                }
        }
}
