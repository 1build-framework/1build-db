package dev.onebuild.db.config;

import dev.onebuild.db.domain.model.config.DatabaseInfo;
import dev.onebuild.db.domain.model.config.DomainInfo;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static dev.onebuild.db.utils.AppUtils.processDatabaseConfigs;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "onebuild.db")
public class OneBuildDbConfigs {
  private String defaultSourcePath;
  private String sourcePath;
  private DatabaseInfo dbInfo;
  private Map<String, DomainInfo> domains;

  @PostConstruct
  public void postProcess() {
    processDatabaseConfigs(this);
  }
}