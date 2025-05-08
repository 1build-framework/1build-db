package dev.onebuild.db.tests.domain.repository;

import dev.onebuild.config.CommonConfiguration;
import dev.onebuild.db.config.OneBuildDatabaseConfiguration;
import dev.onebuild.domain.model.db.DatabaseAction;
import dev.onebuild.domain.model.db.DatabaseInfo;
import dev.onebuild.domain.model.db.DefaultOneBuildRecord;
import dev.onebuild.domain.model.db.OneBuildRecord;
import dev.onebuild.domain.repository.OneBuildDataRepository;
import dev.onebuild.db.tests.config.DbTestConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    DatabaseInfo findDatabaseInfo = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.FIND.getValue()).build();
    List<OneBuildRecord> r = repository.find(findDatabaseInfo, Map.of("id", record.getId()));
    assertNotNull(r);
    assertEquals(1, r.size());
    assertEquals(record.getId(), r.get(0).getId());
    assertEquals("APP002", r.get(0).get("application_code"));
    assertEquals("Test Application", r.get(0).get("application_name"));
  }

  @Disabled
  @Test
  @DisplayName("Test bulk insert method of Repository")
  public void testBulkInsertData() {
    // Given
    OneBuildRecord applicationRecord1 = new DefaultOneBuildRecord()
        .add("application_code", "APP001")
        .add("application_name", "Test Application 1");

    OneBuildRecord applicationRecord2 = new DefaultOneBuildRecord()
        .add("application_code", "APP002")
        .add("application_name", "Test Application 2");

    OneBuildRecord tenantRecord1 = new DefaultOneBuildRecord()
        .add("tenant_code", "TEN001")
        .add("tenant_name", "Test Tenant 1");

    OneBuildRecord tenantRecord2 = new DefaultOneBuildRecord()
        .add("tenant_code", "TEN002")
        .add("tenant_name", "Test Tenant 2");

    DatabaseInfo applicationDatabaseInfo = DatabaseInfo.builder()
        .dataSource("test")
        .schema("auth")
        .table("applications")
        .id("id")
        .statement(DatabaseAction.INSERT_ONE.getValue()).build();

    DatabaseInfo tenantDatabaseInfo = DatabaseInfo.builder()
        .dataSource("test")
        .schema("auth")
        .table("tenants")
        .id("id")
        .statement(DatabaseAction.INSERT_ONE.getValue()).build();

    DatabaseInfo tenantApplicationDatabaseInfo = DatabaseInfo.builder()
        .dataSource("test")
        .schema("auth")
        .table("tenant_application_mapping")
        .id("id")
        .statement(DatabaseAction.INSERT_ALL.getValue()).build();

    // When
    repository.save(applicationDatabaseInfo, applicationRecord1);
    repository.save(applicationDatabaseInfo, applicationRecord2);
    repository.save(tenantDatabaseInfo, tenantRecord1);
    repository.save(tenantDatabaseInfo, tenantRecord2);

    assertNotNull(applicationRecord1.getId());
    assertNotNull(applicationRecord2.getId());
    assertNotNull(tenantRecord1.getId());
    assertNotNull(tenantRecord2.getId());

    applicationDatabaseInfo.setStatement(DatabaseAction.FIND.getValue());
    List<OneBuildRecord> applications = repository.find(applicationDatabaseInfo, Map.of());
    assertNotNull(applications);
    assertEquals(2, applications.size());

    tenantDatabaseInfo.setStatement(DatabaseAction.FIND.getValue());
    List<OneBuildRecord> tenants = repository.find(tenantDatabaseInfo, Map.of());
    assertNotNull(tenants);
    assertEquals(2, tenants.size());

    List<OneBuildRecord> mappings = new ArrayList<>();
    for(int i = 0; i < 2; i++) {
      mappings.add(new DefaultOneBuildRecord()
          .add("tenant_id", tenants.get(i).get("id"))
          .add("application_id", applications.get(i).get("id")));
    }
    repository.saveAll(tenantApplicationDatabaseInfo, mappings, List.of("tenant_id", "application_id"));

    tenantApplicationDatabaseInfo.setStatement(DatabaseAction.FIND.getValue());
    List<OneBuildRecord> tenantApplications = repository.find(tenantApplicationDatabaseInfo, Map.of());
    assertNotNull(tenantApplications);
    assertEquals(2, tenantApplications.size());
    tenantApplications.forEach(ta -> {
      assertNotNull(ta.get("tenant_id"));
      assertNotNull(ta.get("application_id"));
    });
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

    DatabaseInfo findById = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.FIND.getValue()).build();
    List<OneBuildRecord> r = repository.find(findById, Map.of("id", record.getId()));
    assertNotNull(r);
    assertEquals(1, r.size());
    assertEquals(record.getId(), r.get(0).getId());
    assertEquals("APP002", r.get(0).get("application_code"));
    assertEquals("Test Application", r.get(0).get("application_name"));

    record.add("application_name", "Updated Name");
    DatabaseInfo updateInfo = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.UPDATE_BY_ID.getValue()).build();
    repository.save(updateInfo, record);

    //Test the updated data
    r = repository.find(findById, Map.of("id", record.getId()));
    assertNotNull(r);
    assertEquals(1, r.size());
    assertEquals(record.getId(), r.get(0).getId());
    assertEquals("APP002", r.get(0).get("application_code"));
    assertEquals("Updated Name", r.get(0).get("application_name"));
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

    DatabaseInfo findById = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.FIND.getValue()).build();
    List<OneBuildRecord> r = repository.find(findById, Map.of("id", record.getId()));
    assertNotNull(r);
    assertEquals(1, r.size());
    assertEquals(record.getId(), r.get(0).getId());
    assertEquals("APP002", r.get(0).get("application_code"));
    assertEquals("Test Application", r.get(0).get("application_name"));

    DatabaseInfo deleteById = DatabaseInfo.builder().dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.DELETE_BY_ID.getValue()).build();
    repository.deleteById(deleteById, (Long) record.getId());

    List<OneBuildRecord> r2 = repository.find(findById, Map.of("id", record.getId()));
    assertTrue(r2.isEmpty());
  }

  @Test
  @DisplayName("Test findAll method of Repository")
  public void testFindAllData() {
    List<OneBuildRecord> records = new ArrayList<>();

    DatabaseInfo databaseInfo = DatabaseInfo.builder()
        .dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.INSERT_ONE.getValue())
        .build();

    OneBuildRecord record = new DefaultOneBuildRecord()
        .add("application_code", "APP002")
        .add("application_name", "Test Application 2");
    records.add(record);
    repository.save(databaseInfo, record);
    assertNotNull(record.getId());

    record = new DefaultOneBuildRecord()
        .add("application_code", "APP003")
        .add("application_name", "Test Application 3");
    records.add(record);
    repository.save(databaseInfo, record);
    assertNotNull(record.getId());

    record = new DefaultOneBuildRecord()
        .add("application_code", "APP004")
        .add("application_name", "Test Application 4");
    records.add(record);
    repository.save(databaseInfo, record);
    assertNotNull(record.getId());

    DatabaseInfo findAllInfo = DatabaseInfo.builder()
        .dataSource("test").schema("auth").table("applications").id("id").statement(DatabaseAction.FIND.getValue())
        .build();
    List<OneBuildRecord> newRecords = repository.find(findAllInfo, Map.of());
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