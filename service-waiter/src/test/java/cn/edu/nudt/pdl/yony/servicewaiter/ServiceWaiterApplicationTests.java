package cn.edu.nudt.pdl.yony.servicewaiter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceWaiterApplicationTests {

	@Autowired
	protected RestTemplate restTemplate;

	@Test
	public void contextLoads() {
	}

	@Test
	public void post(){
		String quote = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", String.class);
		System.out.println(quote.toString());
	}



}
