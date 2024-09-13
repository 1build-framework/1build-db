package dev.onebuild.db.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(OneBuildDbConfigs.class)
public class DbAutoConfiguration {
}