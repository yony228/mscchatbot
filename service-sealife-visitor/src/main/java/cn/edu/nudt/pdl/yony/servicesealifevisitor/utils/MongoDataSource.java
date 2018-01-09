package cn.edu.nudt.pdl.yony.servicesealifevisitor.utils;

import com.mongodb.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * email: yony228@163.com
 * Created by yony on 18-1-4.
 */
@Slf4j
@Component
public enum MongoDataSource {

        instance;

        @Value("${self.mongodb.template.addresses}")
        private String addresses;

        @Value("${self.mongodb.template.database}")
        public String database;

        @Value("${self.mongodb.template.username}")
        private String username;

        @Value("${self.mongodb.template.password}")
        private String password;

        @Value("${self.mongodb.template.socketTimeout}")
        private int socketTimeout;

        @Value("${self.mongodb.template.connectionsPerHost}")
        private int connectionsPerHost;

        @Value("${self.mongodb.template.connectTimeout}")
        private int connectTimeout;

        @Value("${self.mongodb.template.threadsAllowedToBlockForConnectionMultiplier}")
        private int threadsAllowedToBlockForConnectionMultiplier;

        private volatile MongoClient mongoClient;

        static {
        }

        MongoDataSource() {
//                log.debug("===============MongoDataSource初始化========================");
                //绑定地址
        }

        public MongoClient getClient() {
                if (instance.mongoClient == null) {
                        synchronized (MongoDataSource.class) {
                                if (instance.mongoClient == null) {
                                        List<ServerAddress> addrs = new ArrayList<>();
                                        Arrays.asList(addresses.split(",")).stream()
                                                .forEach(address ->
                                                        addrs.add(new ServerAddress(address.split(":")[0], Integer.valueOf(address.split(":")[1]))));

                                        MongoCredential credential = MongoCredential.createScramSha1Credential(
                                                this.username,
                                                this.database,
                                                this.password.toCharArray());

                                        // 通过连接认证获取MongoDB连接
                                        mongoClient = new MongoClient(
                                                addrs,
                                                credential,
                                                new MongoClientOptions.Builder()
                                                        .socketTimeout(this.socketTimeout)// 套接字超时时间，0无限制
                                                        .connectionsPerHost(this.connectionsPerHost)// 连接池设置为300个连接,默认为100
                                                        .connectTimeout(this.connectTimeout)
                                                        .threadsAllowedToBlockForConnectionMultiplier(this.threadsAllowedToBlockForConnectionMultiplier)// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
                                                        .writeConcern(WriteConcern.ACKNOWLEDGED)
                                                        .build());
                                }
                        }

                }
                return instance.mongoClient;
        }


}
