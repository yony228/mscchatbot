package cn.edu.nudt.pdl.yony.servicesealifevisitor.listerner;

import cn.edu.nudt.pdl.yony.servicesealifevisitor.server.VisitorServer;
import org.springframework.beans.factory.annotation.Autowired;
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

        @Async
        @Override
        public void onApplicationEvent(ApplicationEvent applicationEvent) {
                if(applicationEvent instanceof ApplicationReadyEvent) {
                        try {
                                visitorServer.start();
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
        }
}
