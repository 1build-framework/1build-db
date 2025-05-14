package dev.onebuild.db.config;

import dev.onebuild.db.domain.service.OneBuildDataJdbcService;
import dev.onebuild.db.persistence.OneBuildDataJdbcRepository;
import dev.onebuild.db.utils.OneBuildDataRepositoryHelper;
import dev.onebuild.commons.domain.model.ui.OneBuildEndpoint;
import dev.onebuild.commons.domain.repository.OneBuildDataRepository;
import dev.onebuild.commons.domain.service.OneBuildDataService;
import dev.onebuild.commons.errors.OneBuildExceptionFactory;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class OneBuildDatabaseConfiguration {
  private static final String DEFAULT_TEMPLATE_PATH = "/internal/db/templates";
  @Bean("dbTemplateLoaders")
  public List<TemplateLoader> dbTemplateLoaders() {
    var templateLoaders = new ArrayList<TemplateLoader>();

    //default template classpath
    log.info("Default Database Template Source Path: {}", DEFAULT_TEMPLATE_PATH);
    templateLoaders.add(new ClassTemplateLoader(this.getClass(), DEFAULT_TEMPLATE_PATH));
    //app template classpath

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

  @Bean
  public OneBuildDataRepositoryHelper oneBuildDataRepositoryHelper(@Qualifier("dbFreemarkerConfiguration") freemarker.template.Configuration dbFreemarkerConfiguration) {
    return new OneBuildDataRepositoryHelper(dbFreemarkerConfiguration);
  }

  @Bean("oneBuildDataJdbcRepository")
  public OneBuildDataRepository oneBuildDataJdbcRepository(
      OneBuildDataRepositoryHelper oneBuildDataRepositoryHelper,
      Map<String, NamedParameterJdbcTemplate> testJdbcTemplates,
      OneBuildExceptionFactory exceptionFactory) {
    return new OneBuildDataJdbcRepository(oneBuildDataRepositoryHelper, testJdbcTemplates, exceptionFactory);
  }

  @Bean("oneBuildDataService")
  public OneBuildDataService oneBuildDataService(List<OneBuildEndpoint> endpoints,
                                                 OneBuildDataRepository oneBuildDataRepository,
                                                 OneBuildExceptionFactory exceptionFactory) {
    return new OneBuildDataJdbcService(endpoints, oneBuildDataRepository, exceptionFactory);
  }
}