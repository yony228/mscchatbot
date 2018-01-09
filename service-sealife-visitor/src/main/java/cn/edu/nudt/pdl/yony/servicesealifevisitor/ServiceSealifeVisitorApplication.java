package cn.edu.nudt.pdl.yony.servicesealifevisitor;

import cn.edu.nudt.pdl.yony.servicesealifevisitor.config.MongoConfig;
import cn.edu.nudt.pdl.yony.servicesealifevisitor.config.SwaggerConfig;
import cn.edu.nudt.pdl.yony.servicesealifevisitor.utils.CusRedisPersisContainer;
import cn.yony.automaton.simple.SimpAutomaton;
import cn.yony.automaton.simple.factory.SimpAutomatonFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
@Import({MongoConfig.class, SwaggerConfig.class})
public class ServiceSealifeVisitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceSealifeVisitorApplication.class, args);
	}

	@Bean
	public CusRedisPersisContainer cusRedisPersisContainer() {
		CusRedisPersisContainer crpc = new CusRedisPersisContainer();
		return crpc;
	}

	@Bean
	public SimpAutomaton automaton() throws Exception {
		SimpAutomaton sa = SimpAutomatonFactory.getAutomation();
		CusRedisPersisContainer crpc = cusRedisPersisContainer();
		sa.setSessPersis(crpc);
		return sa;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
