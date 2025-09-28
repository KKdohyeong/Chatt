package project.DevView.cat_service.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "questionExecutor")
    public ThreadPoolTaskExecutor questionExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(10);
        ex.setMaxPoolSize(40);
        ex.setQueueCapacity(200);
        ex.setThreadNamePrefix("questionGen-");
        ex.initialize();
        return ex;
    }
}
