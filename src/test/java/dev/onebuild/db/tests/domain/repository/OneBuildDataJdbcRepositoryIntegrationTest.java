package dev.onebuild.db.tests.domain.repository;

import dev.onebuild.config.CommonConfiguration;
import dev.onebuild.db.config.OneBuildDatabaseConfiguration;
import dev.onebuild.domain.model.db.DatabaseAction;
import dev.onebuild.domain.model.db.DatabaseInfo;
import dev.onebuild.domain.model.db.DefaultOneBuildRecord;
import dev.onebuild.domain.model.db.OneBuildRecord;
import dev.onebuild.domain.repository.OneBuildDataRepository;
import dev.onebuild.db.tests.config.DbTestConfiguration;
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
    CommonConfiguration.class,
    DbTestConfiguration.class,
    OneBuildDatabaseConfiguration.class})
@ActiveProfiles("test")
@Transactional
public class OneBuildDataJdbcRepositoryIntegrationTest {

  @Autowired
  @Qualifier("oneBuildDataJdbcRepository")
  private OneBuildDataRepository repository;

  @Test
  @DisplayName("Test insert method of Repository")
  public void testInsertData() {
    // Given
    OneBuildRecord record = new DefaultOneBuildRecord();
    record.add("application_code", "APP002");
    record.add("application_name", "Test Application");

    DatabaseInfo saveDatabaseInfo = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.INSERT_ONE.getValue()).build();
    // When
    repository.save(saveDatabaseInfo, record);
    assertNotNull(record.getId());

    DatabaseInfo findDatabaseInfo = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.FIND_BY_ID.getValue()).build();
    OneBuildRecord r = repository.findById(findDatabaseInfo, record.getId());
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

    DatabaseInfo databaseInfo = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.INSERT_ONE.getValue()).build();
    // When
    repository.save(databaseInfo, record);
    assertNotNull(record.getId());

    DatabaseInfo findById = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.FIND_BY_ID.getValue()).build();
    OneBuildRecord r = repository.findById(findById, record.getId());
    assertNotNull(r);
    assertEquals(record.getId(), r.getId());
    assertEquals("APP002", r.get("application_code"));
    assertEquals("Test Application", r.get("application_name"));

    record.add("application_name", "Updated Name");
    DatabaseInfo updateInfo = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.UPDATE_BY_ID.getValue()).build();
    repository.save(updateInfo, record);

    //Test the updated data
    r = repository.findById(findById, record.getId());
    assertNotNull(r);
    assertEquals(record.getId(), r.getId());
    assertEquals("APP002", r.get("application_code"));
    assertEquals("Updated Name", r.get("application_name"));
  }

  @Test
  @DisplayName("Test deleteById method of Repository")
  public void testDeleteData() {
    // Given
    OneBuildRecord record = new DefaultOneBuildRecord();
    record.add("application_code", "APP002");
    record.add("application_name", "Test Application");

    DatabaseInfo databaseInfo = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.INSERT_ONE.getValue()).build();
    // When
    repository.save(databaseInfo, record);
    assertNotNull(record.getId());

    DatabaseInfo findById = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.FIND_BY_ID.getValue()).build();
    OneBuildRecord r = repository.findById(findById, record.getId());
    assertNotNull(r);
    assertEquals(record.getId(), r.getId());
    assertEquals("APP002", r.get("application_code"));
    assertEquals("Test Application", r.get("application_name"));

    DatabaseInfo deleteById = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.DELETE_BY_ID.getValue()).build();
    repository.deleteById(deleteById, record.getId());

    OneBuildRecord r2 = repository.findById(findById, record.getId());
    assertNull(r2);
  }

  @Test
  @DisplayName("Test findAll method of Repository")
  public void testFindAllData() {
    List<OneBuildRecord> records = new ArrayList<>();

    DatabaseInfo databaseInfo = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.INSERT_ONE.getValue()).build();

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

    DatabaseInfo findAllInfo = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.FIND_ALL.getValue()).build();
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