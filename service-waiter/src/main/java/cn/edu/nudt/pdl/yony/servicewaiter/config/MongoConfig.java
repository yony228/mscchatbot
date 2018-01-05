package cn.edu.nudt.pdl.yony.servicewaiter.config;

import cn.edu.nudt.pdl.yony.servicewaiter.utils.MongoDataSource;
import cn.edu.nudt.pdl.yony.servicewaiter.utils.MongoJdbcTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * email: yony228@163.com
 * Created by yony on 18-1-4.
 */
@Configuration
public class MongoConfig {

        @Bean
        public MongoDataSource mongoDataSource(){
                return MongoDataSource.instance;
        }

        @Bean
        public MongoJdbcTemplate mongoJdbcTemplate() {
                return new MongoJdbcTemplate(mongoDataSource());
        }
}
