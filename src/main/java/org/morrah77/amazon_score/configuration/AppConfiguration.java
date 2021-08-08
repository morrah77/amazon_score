package org.morrah77.amazon_score.configuration;

import org.morrah77.amazon_score.AmazonScoreApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    // TODO improve it providing a logger factory producing loggers per each class
    @Bean
    public Logger log() {
        return LoggerFactory.getLogger(AmazonScoreApplication.class);
    }
}
