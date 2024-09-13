package dev.onebuild.db.tests.domain.repository;

import dev.onebuild.db.config.DbAutoConfiguration;
import dev.onebuild.db.config.DbTemplateConfiguration;
import dev.onebuild.db.config.OneBuildDbConfigs;
import dev.onebuild.db.domain.model.config.DatabaseInfo;
import dev.onebuild.db.domain.model.sql.DefaultOneBuildRecord;
import dev.onebuild.db.domain.model.types.OneBuildRecord;
import dev.onebuild.db.domain.repository.OneBuildDataRepository;
import dev.onebuild.db.tests.config.DatabaseConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = {
    DbAutoConfiguration.class,
    DatabaseConfiguration.class,
    DbTemplateConfiguration.class})
@ActiveProfiles("test")
@Transactional
public class OneBuildDataJdbcRepositoryIntegrationTest {

  @Autowired
  @Qualifier("oneBuildDataJdbcRepository")
  private OneBuildDataRepository repository;

  @Autowired
  private OneBuildDbConfigs dbInfo;

  @Test
  @DisplayName("Test insert method of Repository")
  public void testInsertData() {
    // Given
    OneBuildRecord record = new DefaultOneBuildRecord();
    record.add("application_code", "APP002");
    record.add("application_name", "Test Application");

    DatabaseInfo databaseInfo = this.dbInfo.getDomains().get("applications").findAction("INSERT_ONE").getDbInfo();
    // When
    repository.save(databaseInfo, record);
    assertNotNull(record.getId());

    DatabaseInfo findById = this.dbInfo.getDomains().get("applications").findAction("FIND_BY_ID").getDbInfo();
    OneBuildRecord r = repository.findById(findById, record.getId());
    assertNotNull(r);
    assertEquals(record.getId(), r.getId());
    assertEquals("APP002", r.get("application_code"));
    assertEquals("Test Application", r.get("application_name"));
  }

  @Test
  @DisplayName("Test update method of Repository")
  public void testUpdateData() {
    // Given
    OneBuildRecord record = new DefaultOneBuildRecord();
    record.add("application_code", "APP002");
    record.add("application_name", "Test Application");

    DatabaseInfo databaseInfo = this.dbInfo.getDomains().get("applications").findAction("INSERT_ONE").getDbInfo();
    // When
    repository.save(databaseInfo, record);
    assertNotNull(record.getId());

    DatabaseInfo findById = this.dbInfo.getDomains().get("applications").findAction("FIND_BY_ID").getDbInfo();
    OneBuildRecord r = repository.findById(findById, record.getId());
    assertNotNull(r);
    assertEquals(record.getId(), r.getId());
    assertEquals("APP002", r.get("application_code"));
    assertEquals("Test Application", r.get("application_name"));

    record.add("application_name", "Updated Name");
    DatabaseInfo updateInfo = this.dbInfo.getDomains().get("applications").findAction("UPDATE_BY_ID").getDbInfo();
    repository.save(updateInfo, record);

    //Test the updated data
    r = repository.findById(findById, record.getId());
    assertNotNull(r);
    assertEquals(record.getId(), r.getId());
    assertEquals("APP002", r.get("application_code"));
    assertEquals("Test Application", r.get("application_name"));
  }

  @Test
  @DisplayName("Test deleteById method of Repository")
  public void testDeleteData() {
    // Given
    OneBuildRecord record = new DefaultOneBuildRecord();
    record.add("application_code", "APP002");
    record.add("application_name", "Test Application");

    DatabaseInfo databaseInfo = this.dbInfo.getDomains().get("applications").findAction("INSERT_ONE").getDbInfo();
    // When
    repository.save(databaseInfo, record);
    assertNotNull(record.getId());

    DatabaseInfo findById = this.dbInfo.getDomains().get("applications").findAction("FIND_BY_ID").getDbInfo();
    OneBuildRecord r = repository.findById(findById, record.getId());
    assertNotNull(r);
    assertEquals(record.getId(), r.getId());
    assertEquals("APP002", r.get("application_code"));
    assertEquals("Test Application", r.get("application_name"));

    DatabaseInfo deleteById = this.dbInfo.getDomains().get("applications").findAction("DELETE_BY_ID").getDbInfo();
    repository.deleteById(deleteById, record.getId());

    OneBuildRecord r2 = repository.findById(findById, record.getId());
    assertNull(r2);
  }

  @Test
  @DisplayName("Test findAll method of Repository")
  public void testFindAllData() {
    List<OneBuildRecord> records = new ArrayList<>();

    DatabaseInfo databaseInfo = this.dbInfo.getDomains().get("applications").findAction("INSERT_ONE").getDbInfo();

    OneBuildRecord record = new DefaultOneBuildRecord()
        .add("application_code", "APP002")
        .add("application_name", "Test Application 2");
    records.add(record);
    repository.save(databaseInfo, record);
    assertNotNull(record.getId());

    record = new DefaultOneBuildRecord()
        .add("application_code", "APP003")
        .add("application_name", "Test Application 2");
    records.add(record);
    repository.save(databaseInfo, record);
    assertNotNull(record.getId());

    record = new DefaultOneBuildRecord()
        .add("application_code", "APP004")
        .add("application_name", "Test Application 4");
    records.add(record);
    repository.save(databaseInfo, record);
    assertNotNull(record.getId());

    DatabaseInfo findAllInfo = this.dbInfo.getDomains().get("applications").findAction("FIND_ALL").getDbInfo();
    List<OneBuildRecord> newRecords = repository.findAll(findAllInfo);
    assertNotNull(records);
    assertEquals(3, records.size());

    records.forEach(r -> {
      OneBuildRecord newRecord = newRecords.stream()
          .filter(nr -> nr.getId().equals(r.getId()))
          .findFirst()
          .orElse(null);
      assertNotNull(newRecord);
      assertEquals(r.get("application_code"), newRecord.get("application_code"));
      assertEquals(r.get("application_name"), newRecord.get("application_name"));
    });
  }
}