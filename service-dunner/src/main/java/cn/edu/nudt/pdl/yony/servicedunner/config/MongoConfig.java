package cn.edu.nudt.pdl.yony.servicedunner.config;

import cn.edu.nudt.pdl.yony.servicedunner.utils.MongoDataSource;
import cn.edu.nudt.pdl.yony.servicedunner.utils.MongoJdbcTemplate;
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
