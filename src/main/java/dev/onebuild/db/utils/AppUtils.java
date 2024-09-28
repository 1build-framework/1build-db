package dev.onebuild.db.utils;

import dev.onebuild.domain.model.db.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class AppUtils {
  /*public static void processDatabaseConfigs(OneBuildDbConfigs configs) {
    if(configs.getDomains() == null) {
      return;
    }
    for (Map.Entry<String, DomainInfo> domainEntry : configs.getDomains().entrySet()) {
      DomainInfo domainInfo = domainEntry.getValue();

      for(ActionInfo actionInfo : domainInfo.getActions()) {

        DatabaseInfo domainDbInfo = domainInfo.getDbInfo();
        if(actionInfo.getDbInfo() == null) {
          actionInfo.setDbInfo(new DatabaseInfo());
        }

        DatabaseInfo actionDbInfo = actionInfo.getDbInfo();

        //datasource
        if (actionDbInfo.getDataSource() == null) {
          if(domainDbInfo.getDataSource() == null) {
            actionDbInfo.setDataSource(configs.getDbInfo().getDataSource());
          } else {
            actionDbInfo.setDataSource(domainDbInfo.getDataSource());
          }
        }

        //schema
        if (actionDbInfo.getSchema() == null) {
          if (domainDbInfo.getSchema() == null) {
            actionDbInfo.setSchema(configs.getDbInfo().getSchema());
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
        if (actionDbInfo.getStatement() == null) {
          DatabaseAction statement = null;
          try {
            statement = DatabaseAction.fromName(actionInfo.getName());
          } catch(Exception e) {
            log.error("Error while setting statement for action {}", actionInfo.getName(), e);
          }
          if(statement == null) {
            throw new RuntimeException(String.format("Action %s not valid. Please select from one of these actions %s ", actionInfo.getName(), DatabaseAction.listAllNames()));
          }
          actionDbInfo.setStatement(statement.getValue());
        }
        //action type
        actionDbInfo.setActionType(ActionType.fromValue(actionInfo.getActionType()));
      }
    }
  }*/
}