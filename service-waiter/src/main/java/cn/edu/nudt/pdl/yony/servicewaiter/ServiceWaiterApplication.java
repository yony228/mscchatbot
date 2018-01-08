package cn.edu.nudt.pdl.yony.servicewaiter;

import cn.edu.nudt.pdl.yony.servicewaiter.config.MongoConfig;
import cn.edu.nudt.pdl.yony.servicewaiter.config.SwaggerConfig;
import cn.edu.nudt.pdl.yony.servicewaiter.server.DunnerServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableAsync
@EnableFeignClients
//@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
@Import({MongoConfig.class, SwaggerConfig.class})
public class ServiceWaiterApplication {

        public static void main(String[] args) {
                SpringApplication.run(ServiceWaiterApplication.class, args);
        }

        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
                return builder.build();
        }
}
