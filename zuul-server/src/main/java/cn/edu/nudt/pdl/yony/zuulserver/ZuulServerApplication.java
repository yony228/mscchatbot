package cn.edu.nudt.pdl.yony.zuulserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
//@EnableEurekaClient
//@SpringBootApplication
@SpringCloudApplication
public class ZuulServerApplication {

        public static void main(String[] args) {
                SpringApplication.run(ZuulServerApplication.class, args);
        }
}
