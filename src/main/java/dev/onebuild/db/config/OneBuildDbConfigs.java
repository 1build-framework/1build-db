package dev.onebuild.db.config;

import dev.onebuild.db.domain.model.ActionConfig;
import dev.onebuild.db.domain.model.DomainConfig;
import dev.onebuild.db.domain.model.DbInfoConfig;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "onebuild.db")
public class OneBuildDbConfigs {
  private String sourcePath;
  private DbInfoConfig dbInfo;
  private Map<String, DomainConfig> domains;

  @PostConstruct
  public void postProcess() {
    for (Map.Entry<String, DomainConfig> domainEntry : domains.entrySet()) {
      DomainConfig domainConfig = domainEntry.getValue();

      for(Map.Entry<String, ActionConfig> actionEntry : domainConfig.getActions().entrySet()) {
        DbInfoConfig domainDbInfo = domainConfig.getDbInfo();
        if(actionEntry.getValue().getInfo() == null) {
          actionEntry.getValue().setInfo(new DbInfoConfig());
        }
        DbInfoConfig actionDbInfo = actionEntry.getValue().getInfo();

        //datasource
        if (actionDbInfo.getDataSource() == null) {
          if(domainDbInfo.getDataSource() == null) {
            actionDbInfo.setDataSource(dbInfo.getDataSource());
          } else {
            actionDbInfo.setDataSource(domainDbInfo.getDataSource());
          }
        }
        //schema
        if (actionDbInfo.getSchema() == null) {
          if(domainDbInfo.getSchema() == null) {
            actionDbInfo.setSchema(dbInfo.getSchema());
          } else {
            actionDbInfo.setSchema(domainDbInfo.getSchema());
          }
        }
        //table
        if (actionDbInfo.getTable() == null) {
          actionDbInfo.setTable(domainDbInfo.getTable());
        }
        //id
        if (actionDbInfo.getId() == null) {
          actionDbInfo.setId(domainDbInfo.getId());
        }
      }
    }
  }
}
