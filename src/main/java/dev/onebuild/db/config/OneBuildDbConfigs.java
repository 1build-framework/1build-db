package dev.onebuild.db.config;

import dev.onebuild.db.domain.model.config.ActionInfo;
import dev.onebuild.db.domain.model.config.ActionType;
import dev.onebuild.db.domain.model.config.DatabaseInfo;
import dev.onebuild.db.domain.model.config.DomainInfo;
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
  private Map<String, String> defaultStatements;
  private DatabaseInfo dbInfo;
  private Map<String, DomainInfo> domains;

  @PostConstruct
  public void postProcess() {
    for (Map.Entry<String, DomainInfo> domainEntry : domains.entrySet()) {
      DomainInfo domainInfo = domainEntry.getValue();

      for(Map.Entry<String, ActionInfo> actionEntry : domainInfo.getActions().entrySet()) {

        DatabaseInfo domainDbInfo = domainInfo.getDbInfo();
        if(actionEntry.getValue().getDbInfo() == null) {
          actionEntry.getValue().setDbInfo(new DatabaseInfo());
        }

        DatabaseInfo actionDbInfo = actionEntry.getValue().getDbInfo();

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
          if (domainDbInfo.getSchema() == null) {
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

        //statement
        if (actionDbInfo.getStatement() == null &&
            defaultStatements != null &&
            defaultStatements.containsKey(actionEntry.getKey())) {
          String statement = defaultStatements.get(actionEntry.getKey());
          actionDbInfo.setStatement(statement);
        }

        //action type
        actionDbInfo.setActionType(ActionType.fromValue(actionEntry.getValue().getActionType()));
      }
    }
  }
}