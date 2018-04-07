package cn.edu.nudt.pdl.yony.servicedunner;

import cn.edu.nudt.pdl.yony.servicedunner.utils.CusRedisPersisContainer;
import cn.yony.automaton.simple.SimpAutomaton;
import cn.yony.automaton.simple.factory.SimpAutomatonFactory;
import cn.yony.automaton.simple.persistence.IPersistenceContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@EnableEurekaClient
@EnableAsync
//@EnableDiscoveryClient
@Controller
@SpringBootApplication
public class ServiceDunnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceDunnerApplication.class, args);
	}

	@RequestMapping("/")
	public String def() {
		return "init";
	}

	@RequestMapping(value = "/init")
	public String init() {
		return "init";
	}

	@Bean
	public IPersistenceContainer cusRedisPersisContainer() {
//                CusRedisPersisContainer crpc = new CusRedisPersisContainer(399, "", 123, 256);
		IPersistenceContainer persistenceContainer = new CusRedisPersisContainer();
		return persistenceContainer;
	}

	@Bean
	public SimpAutomaton automaton() throws Exception {
		SimpAutomaton sa = SimpAutomatonFactory.getAutomation();
		IPersistenceContainer persistenceContainer = cusRedisPersisContainer();
		sa.setSessPersis(persistenceContainer);
		return sa;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
