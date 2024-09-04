package dev.onebuild.db.tests.config;

import dev.onebuild.db.domain.repository.OneBuildDataRepository;
import dev.onebuild.db.persistence.OneBuildDataJdbcRepository;
import dev.onebuild.db.utils.OneBuildDataRepositoryHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DatabaseConfiguration {

  @Bean("testDataSource")
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");//;INIT=CREATE SCHEMA IF NOT EXISTS auth
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    return dataSource;
  }

  @Bean("testTransactionManager")
  public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
    return initializer;
  }

  @Bean("testJdbcTemplate")
  public NamedParameterJdbcTemplate testJdbcTemplate(DataSource dataSource) {
    return new NamedParameterJdbcTemplate(dataSource);
  }

  @Bean
  public OneBuildDataRepositoryHelper oneBuildDataRepositoryHelper(@Qualifier("dbFreemarkerConfiguration") freemarker.template.Configuration dbFreemarkerConfiguration) {
    return new OneBuildDataRepositoryHelper(dbFreemarkerConfiguration);
  }

  @Bean("oneBuildDataJdbcRepository")
  public OneBuildDataRepository oneBuildDataJdbcRepository(
      OneBuildDataRepositoryHelper oneBuildDataRepositoryHelper,
      Map<String, NamedParameterJdbcTemplate> testJdbcTemplates) {
    return new OneBuildDataJdbcRepository(oneBuildDataRepositoryHelper, testJdbcTemplates);
  }


}
