package dev.onebuild.db.config;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class DbTemplateConfiguration {

  @Bean("dbTemplateLoaders")
  public List<TemplateLoader> dbTemplateLoaders(OneBuildDbConfigs oneBuildDbConfigs) {
    var templateLoaders = new ArrayList<TemplateLoader>();

    //Database Classpath
    templateLoaders.add(new ClassTemplateLoader(this.getClass(), oneBuildDbConfigs.getSourcePath()));
    log.info("Database Template Source Path: {}", oneBuildDbConfigs.getSourcePath());

    return templateLoaders;
  }

  @Bean("dbFreemarkerConfiguration")
  public freemarker.template.Configuration dbFreemarkerConfiguration(@Qualifier("dbTemplateLoaders") List<TemplateLoader> dbTemplateLoaders) {
    var configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
    configuration.setTemplateLoader(new MultiTemplateLoader(dbTemplateLoaders.toArray(new TemplateLoader[0])));
    configuration.setDefaultEncoding("UTF-8");
    configuration.setTemplateExceptionHandler(freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER);
    configuration.setLogTemplateExceptions(true);
    configuration.setWrapUncheckedExceptions(true);
    configuration.setFallbackOnNullLoopVariable(false);
    return configuration;
  }
}