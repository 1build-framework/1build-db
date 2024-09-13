package dev.onebuild.db.tests;

import dev.onebuild.db.config.OneBuildDbConfigs;
import dev.onebuild.db.domain.model.config.ActionInfo;
import dev.onebuild.db.domain.model.config.DatabaseInfo;
import dev.onebuild.db.domain.model.config.DomainInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static dev.onebuild.db.tests.utils.TestUtils.validateDbInfo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ActiveProfiles("merge")
public class OneBuildConfigMergeTest {

  @Autowired
  private OneBuildDbConfigs oneBuildDbConfigs;

  @Test
  public void whenConfigsAreNotOverriden_itPopulatesFromParent() {
    assertNotNull(oneBuildDbConfigs);
    assertEquals("/internal/db/templates", oneBuildDbConfigs.getDefaultSourcePath());
    assertEquals("/app/db/templates", oneBuildDbConfigs.getSourcePath());
    //Root db info test
    DatabaseInfo rootDbInfo = oneBuildDbConfigs.getDbInfo();
    assertNotNull(rootDbInfo);
    validateDbInfo("common", "auth", null, null, null, null, rootDbInfo);

    //Domain db info test
    DomainInfo appDomain = oneBuildDbConfigs.getDomains().get("applications");
    validateDbInfo(null, null, "applications", "id", null, null, appDomain.getDbInfo());

    //Action db info test
    ActionInfo createAction = appDomain.findAction("INSERT_ONE");
    validateDbInfo("common", "auth", "applications", "id", "insert-one.sql.ftl", "default", createAction.getDbInfo());

    //Action db info test
    ActionInfo updateAction = appDomain.findAction("UPDATE_BY_ID");
    validateDbInfo("common", "auth", "applications", "id", "update-apps.sql", "custom", updateAction.getDbInfo());
  }
}