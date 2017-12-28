package cn.edu.nudt.pdl.yony.servicedunner;

import cn.edu.nudt.pdl.yony.servicedunner.utils.CusRedisPersisContainer;
import cn.yony.automaton.simple.SimpAutomaton;
import cn.yony.automaton.simple.factory.SimpAutomatonFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@EnableEurekaClient
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
	public CusRedisPersisContainer cusRedisPersisContainer() {
//                CusRedisPersisContainer crpc = new CusRedisPersisContainer(399, "", 123, 256);
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
}
