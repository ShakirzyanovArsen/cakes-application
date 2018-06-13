package com.cakes.cakes.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.cakes.cakes")
@EnableAsync
public class Config {

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Autowired
    private Environment environment;

    private final static String DIALECT_KEY = "spring.jpa.properties.hibernate.dialect";

    private final static String CORE_POOL_SIZE = "async.core-pool-size";
    private final static String MAX_POOL_SIZE = "async.max-pool-size";
    private final static String QUEUE_CAPACITY = "async.queue-capacity";
    private final static String THREAD_NAME_PREFIX = "async.thread-name-prefix";


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Integer.parseInt(environment.getProperty(CORE_POOL_SIZE)));
        executor.setMaxPoolSize(Integer.parseInt(environment.getProperty(MAX_POOL_SIZE)));
        executor.setQueueCapacity(Integer.parseInt(environment.getProperty(QUEUE_CAPACITY)));
        executor.setThreadNamePrefix(environment.getProperty(THREAD_NAME_PREFIX));
        executor.initialize();
        return executor;
    }
}
