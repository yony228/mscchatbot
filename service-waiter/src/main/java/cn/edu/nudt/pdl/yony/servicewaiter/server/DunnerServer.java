package cn.edu.nudt.pdl.yony.servicewaiter.server;

import cn.edu.nudt.pdl.yony.servicewaiter.service.OuterDunnerService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * email: yony228@163.com
 * Created by yony on 17-12-25.
 */
@Component
public class DunnerServer implements ApplicationContextAware {

        private ApplicationContext applicationContext;

        @Autowired
        private OuterDunnerService outerDunnerService;


        public DunnerServer() {
        }

        public void start(String... args) throws Exception {
                run(args);
        }

        public void run(String... args) throws Exception {
                ServerSocket ss = null;
                try{
                        ss = new ServerSocket(14800);
                        System.out.println("Server start on port: 14800");
                        while (true) {
                                Socket socket = ss.accept();
                                DunnerHandler dh = applicationContext.getBean(DunnerHandler.class, new Object[]{socket, outerDunnerService});
                                Thread dunner = new Thread(dh);//new DunnerHandler(socket, outerDunnerService)
                                dunner.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                                        @Override
                                        public void uncaughtException(Thread t, Throwable e) {
                                                e.printStackTrace();
                                        }
                                });
                                dunner.start();
                        }
                } finally {
                        if(ss != null) {
                                ss.close();
                                ss = null;
                        }
                }
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
                this.applicationContext = applicationContext;
        }
}