package cn.edu.nudt.pdl.yony.servicewaiter;

import cn.edu.nudt.pdl.yony.servicewaiter.config.MongoConfig;
import cn.edu.nudt.pdl.yony.servicewaiter.server.DunnerServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
@Import(MongoConfig.class)
public class ServiceWaiterApplication {

        public static void main(String[] args) {
                SpringApplication.run(ServiceWaiterApplication.class, args);
        }
}
