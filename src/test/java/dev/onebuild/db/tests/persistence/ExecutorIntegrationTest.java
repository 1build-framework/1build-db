package dev.onebuild.db.tests.persistence;

import dev.onebuild.db.domain.model.DbWorkflowStep;
import dev.onebuild.db.domain.model.WorkflowStepType;
import dev.onebuild.db.domain.service.WorkflowExecutor;
import dev.onebuild.db.domain.service.WorkflowExecutorImpl;
import dev.onebuild.db.domain.service.WorkflowResult;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.sql.init.platform=h2",
    "spring.sql.init.mode=always",
    "spring.sql.init.schema-locations=classpath:/sql/schema-pg.sql",
    "spring.sql.init.data-locations=classpath:/sql/data-pg.sql"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExecutorIntegrationTest {

  @Autowired
  private DataSource dataSource;

  private NamedParameterJdbcTemplate jdbcTemplate;
  private WorkflowExecutor executor;

  @BeforeEach
  void setUp() {
    jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    executor = new WorkflowExecutorImpl(getDbFreemarkerConfiguration(), Map.of("default-jdbc-template", jdbcTemplate), Executors.newFixedThreadPool(4));
  }

  @Test
  void shouldExecuteMultiStepWorkflow() {
    WorkflowResult result = new WorkflowResult();

    Map<String, Object> params = new HashMap<>();
    params.put("payment_id", 100);
    params.put("order_id", 200);
    params.put("customer_id", 1);
    params.put("product_id", 10);
    params.put("amount", 55.50);
    params.put("quantity", 5);
    params.put("status", "PAID");

    // Step 1: update customer
    DbWorkflowStep updateCustomer = new DbWorkflowStep();
    updateCustomer.setName("update-customer");
    updateCustomer.setType(WorkflowStepType.DB_UPDATE);
    updateCustomer.setSourceDb("default");
    updateCustomer.setStatement("/templates/update_customer.ftl");
    updateCustomer.setStopOnFailure(true);

    // Step 2: create order
    DbWorkflowStep createOrder = new DbWorkflowStep();
    createOrder.setName("create-order");
    createOrder.setType(WorkflowStepType.DB_INSERT);
    createOrder.setSourceDb("default");
    createOrder.setStatement("/templates/create_order.ftl");
    createOrder.setStopOnFailure(true);

    // Step 3: update product
    DbWorkflowStep updateProduct = new DbWorkflowStep();
    updateProduct.setName("update-product");
    updateProduct.setType(WorkflowStepType.DB_UPDATE);
    updateProduct.setSourceDb("default");
    updateProduct.setStatement("/templates/update_product.ftl");
    updateProduct.setStopOnFailure(true);

    // Step 4: insert payment, depends on the other 3
    DbWorkflowStep insertPayment = new DbWorkflowStep();
    insertPayment.setName("insert-payment");
    insertPayment.setType(WorkflowStepType.DB_INSERT);
    insertPayment.setSourceDb("default");
    insertPayment.setStatement("/templates/insert_payment.ftl");
    insertPayment.setStopOnFailure(true);
    insertPayment.setDependencies(List.of(updateCustomer, createOrder, updateProduct));

    // Execute the full workflow
    executor.execute(params, insertPayment, result);

    assertEquals(1, result.getStepResult(updateCustomer.getName()));
    assertEquals(1, result.getStepResult(createOrder.getName()));
    assertEquals(1, result.getStepResult(updateProduct.getName()));
    assertEquals(1, result.getStepResult(insertPayment.getName()));

    // Verify
    Map<String, Object> customer = jdbcTemplate.queryForMap("SELECT * FROM customer WHERE id = 1", Map.of());
    Map<String, Object> payment = jdbcTemplate.queryForMap("SELECT * FROM payment WHERE id = 100", Map.of());
    Map<String, Object> order = jdbcTemplate.queryForMap("SELECT * FROM \"order\" WHERE id = 200", Map.of());
    Map<String, Object> product = jdbcTemplate.queryForMap("SELECT * FROM product WHERE id = 10", Map.of());

    assertEquals("PAID", customer.get("status"));
    assertEquals(new BigDecimal("55.50"), payment.get("amount"));
    assertEquals(5, order.get("quantity"));
    assertEquals(95, product.get("quantity"));
  }

  private freemarker.template.Configuration getDbFreemarkerConfiguration() {
    var templateLoaders = new ArrayList<TemplateLoader>();
    templateLoaders.add(new ClassTemplateLoader(this.getClass(), "/"));

    var configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
    configuration.setTemplateLoader(new MultiTemplateLoader(templateLoaders.toArray(new TemplateLoader[0])));
    configuration.setDefaultEncoding("UTF-8");
    configuration.setTemplateExceptionHandler(freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER);
    configuration.setLogTemplateExceptions(true);
    configuration.setWrapUncheckedExceptions(true);
    configuration.setFallbackOnNullLoopVariable(false);
    return configuration;
  }
}