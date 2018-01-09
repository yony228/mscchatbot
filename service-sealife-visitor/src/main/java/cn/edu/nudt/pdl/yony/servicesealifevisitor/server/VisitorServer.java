package cn.edu.nudt.pdl.yony.servicesealifevisitor.server;

import org.springframework.beans.BeansException;
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
public class VisitorServer implements ApplicationContextAware {

        private ApplicationContext applicationContext;

        public VisitorServer() {
        }

        public void start(String... args) throws Exception {
                run(args);
        }

        public void run(String... args) throws Exception {
                ServerSocket ss = null;
                try{
                        ss = new ServerSocket(14801);
                        System.out.println("Server start on port: 14801");
                        while (true) {
                                Socket socket = ss.accept();
                                VisitorHandler dh = applicationContext.getBean(VisitorHandler.class, new Object[]{socket/*, outerDunnerService*/});
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