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
import static dev.onebuild.db.tests.utils.TestUtils.validateDefaultStatements;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ActiveProfiles("override")
public class OneBuildConfigOverrideTest {

  @Autowired
  private OneBuildDbConfigs oneBuildDbConfigs;

  @Test
  public void whenConfigsAreNotOverriden_itPopulatesFromParent() {
    assertNotNull(oneBuildDbConfigs);

    validateDefaultStatements(oneBuildDbConfigs.getDefaultStatements());

    assertEquals("/internal/db/templates", oneBuildDbConfigs.getSourcePath());

    //Root Level db info test
    DatabaseInfo rootDbInfo = oneBuildDbConfigs.getDbInfo();
    assertNotNull(rootDbInfo);
    validateDbInfo("common", "auth", null, null, null, null, rootDbInfo);

    //Domain Level db info test
    DomainInfo appDomain = oneBuildDbConfigs.getDomains().get("applications");
    validateDbInfo("auth", null, "applications", "id", null, null, appDomain.getDbInfo());

    //Action Level db info test
    ActionInfo createAction = appDomain.getActions().get("insert");
    validateDbInfo("auth", "auth", "applications", "id", "insert.sql", "default", createAction.getDbInfo());
    assertNotNull(createAction.getDbInfo().getStatement());

    //Action db info test
    ActionInfo updateAction = appDomain.getActions().get("update");
    validateDbInfo("authorizations", "entitlement", "auth-apps", "id", "update_apps.sql", "custom", updateAction.getDbInfo());
    assertNotNull(updateAction.getDbInfo().getStatement());
  }
}
