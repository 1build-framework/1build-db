package dev.onebuild.db.persistence;

import dev.onebuild.commons.domain.model.db.DatabaseAction;
import dev.onebuild.db.utils.OneBuildDataRepositoryHelper;
import dev.onebuild.commons.domain.model.db.DatabaseInfo;
import dev.onebuild.commons.domain.model.db.DefaultOneBuildRecord;
import dev.onebuild.commons.domain.model.db.OneBuildRecord;
import dev.onebuild.commons.domain.repository.OneBuildDataRepository;
import dev.onebuild.commons.errors.OneBuildExceptionFactory;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OneBuildDataJdbcRepository implements OneBuildDataRepository {
  private static final Logger log = LoggerFactory.getLogger(OneBuildDataJdbcRepository.class);
  private final OneBuildDataRepositoryHelper oneBuildDataRepositoryHelper;
  private final Map<String, NamedParameterJdbcTemplate> jdbcTemplates;
  private final OneBuildExceptionFactory exceptionFactory;

  public OneBuildDataJdbcRepository(
      OneBuildDataRepositoryHelper oneBuildDataRepositoryHelper,
      Map<String, NamedParameterJdbcTemplate> jdbcTemplates,
      OneBuildExceptionFactory exceptionFactory) {
    this.oneBuildDataRepositoryHelper = oneBuildDataRepositoryHelper;
    this.jdbcTemplates = jdbcTemplates;
    this.exceptionFactory = exceptionFactory;
  }

  @Override
  public void save(DatabaseInfo database, OneBuildRecord record) {
    var jdbcTemplate = jdbcTemplates.get(database.getDataSource() + "JdbcTemplate");

    if(jdbcTemplate == null) {
      throw exceptionFactory.createDataSourceNotFoundException(database.getDataSource());
    }

    if(StringUtils.isBlank(database.getStatement())) {
      if(record.getId() != null) {
        database.setStatement(DatabaseAction.UPDATE_BY_ID.getValue());
      } else {
        database.setStatement(DatabaseAction.INSERT_ONE.getValue());
      }
    }

    String sql = oneBuildDataRepositoryHelper.getSql(database, record);
    SqlParameterSource parameters = record.getSqlParameterSource();

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(sql, parameters, keyHolder, new String[] { database.getId() });

    if(keyHolder.getKey() != null) {
      record.setId(keyHolder.getKey().longValue());
    }
  }

  @Override
  public List<OneBuildRecord> find(DatabaseInfo database, Map<String, Object> params) {
    var jdbcTemplate = jdbcTemplates.get(database.getDataSource() + "JdbcTemplate");

    if(jdbcTemplate == null) {
      throw exceptionFactory.createDataSourceNotFoundException(database.getDataSource());
    }

    if(StringUtils.isBlank(database.getStatement())) {
      database.setStatement(DatabaseAction.FIND.getValue());
    }

    String sql = oneBuildDataRepositoryHelper.getSql(database, params);
    try {
      return jdbcTemplate.query(
          sql,
          params,
          (rs, rowNum) -> DefaultOneBuildRecord.from(database, rs)
      );
    } catch(EmptyResultDataAccessException e) {
      return Collections.emptyList();
    }
  }

  @Override
  public void deleteById(DatabaseInfo database, Long id) {
    var jdbcTemplate = jdbcTemplates.get(database.getDataSource() + "JdbcTemplate");

    if(jdbcTemplate == null) {
      throw exceptionFactory.createDataSourceNotFoundException(database.getDataSource());
    }

    if(StringUtils.isBlank(database.getStatement())) {
      database.setStatement(DatabaseAction.DELETE_BY_ID.getValue());
    }

    String sql = oneBuildDataRepositoryHelper.getSql(database, Collections.emptyMap());
    SqlParameterSource parameter = new MapSqlParameterSource().addValue(database.getId(), id);
    jdbcTemplate.update(sql, parameter);
  }

  @Override
  public void delete(DatabaseInfo database, List<Long> ids) {
    var jdbcTemplate = jdbcTemplates.get(database.getDataSource() + "JdbcTemplate");

    if(jdbcTemplate == null) {
      throw exceptionFactory.createDataSourceNotFoundException(database.getDataSource());
    }

    if(StringUtils.isBlank(database.getStatement())) {
      database.setStatement(DatabaseAction.DELETE.getValue());
    }

    String sql = oneBuildDataRepositoryHelper.getSql(database, Collections.emptyMap());
    SqlParameterSource parameter = new MapSqlParameterSource().addValue("ids", ids);
    int count = jdbcTemplate.update(sql, parameter);
    log.debug("Delete statement {}. Total deleted {}}", sql, count);
  }

  @Override
  public void saveAll(DatabaseInfo database, List<OneBuildRecord> records, List<String> conflictColumns) {
    if(records.isEmpty()) {
      return;
    }

    var jdbcTemplate = jdbcTemplates.get(database.getDataSource() + "JdbcTemplate");
    if(jdbcTemplate == null) {
      throw exceptionFactory.createDataSourceNotFoundException(database.getDataSource());
    }

    String upsertSql = oneBuildDataRepositoryHelper.getSql(database, records, conflictColumns);
    jdbcTemplate.batchUpdate(upsertSql, records.stream().map(OneBuildRecord::getSqlParameterSource).toArray(SqlParameterSource[]::new));
  }
}