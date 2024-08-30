package dev.onebuild.db.tests.config;

import dev.onebuild.db.config.OneBuildDbConfigs;
import dev.onebuild.db.domain.model.ActionConfig;
import dev.onebuild.db.domain.model.DbInfoConfig;
import dev.onebuild.db.domain.model.DomainConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static dev.onebuild.db.tests.utils.TestUtils.validateDbInfo;
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
    assertEquals("/override/statements", oneBuildDbConfigs.getSourcePath());
    //Root db info test
    DbInfoConfig rootDbInfo = oneBuildDbConfigs.getDbInfo();
    assertNotNull(rootDbInfo);
    validateDbInfo("common", "auth", null, null, null, rootDbInfo);

    //Domain db info test
    DomainConfig appDomain = oneBuildDbConfigs.getDomains().get("applications");
    validateDbInfo("auth", null, "applications", "id", null, appDomain.getDbInfo());

    //Action db info test
    ActionConfig createAction = appDomain.getActions().get("create");
    validateDbInfo("auth", "auth", "applications", "id", null, createAction.getInfo());

    //Action db info test
    ActionConfig updateAction = appDomain.getActions().get("update");
    validateDbInfo("authorizations", "entitlement", "auth-apps", "id", "update_apps.sql", updateAction.getInfo());

  }

}
