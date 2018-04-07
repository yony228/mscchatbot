package cn.edu.nudt.pdl.yony.servicedunner.server;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * email: yony228@163.com
 * Created by yony on 17-12-25.
 */
@Component
public class VisitorServer implements ApplicationContextAware {

        private ApplicationContext applicationContext;

        @Value("${self.server.hostPort}")
        private int serverPort;

        private static ConcurrentHashMap<String, Object> sessions = new ConcurrentHashMap<>();

        public static ISession getSession(String uuid) {
                Object o = sessions.get(uuid);
                if(o instanceof ISession) {
                        return (ISession)o;
                } else {
                        return null;
                }
        }

        public static void rmSession(String uuid) {
                sessions.remove(uuid);
        }

        public VisitorServer() {
        }

        public void start(String... args) throws Exception {
                run(args);
        }

        public void run(String... args) throws Exception {
                ServerSocket ss = null;
                try{
                        ss = new ServerSocket(serverPort);
                        System.out.println("Server start on port: " + serverPort);
                        while (true) {
                                Socket socket = ss.accept();
                                AliVisitorHandler dh = applicationContext.getBean(AliVisitorHandler.class, new Object[]{socket/*, outerDunnerService*/});
                                sessions.put(dh.getUuid(), dh);
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