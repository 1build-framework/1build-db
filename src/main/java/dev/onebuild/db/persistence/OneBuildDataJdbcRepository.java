package dev.onebuild.db.persistence;

import dev.onebuild.db.domain.model.config.DatabaseInfo;
import dev.onebuild.db.domain.model.sql.DefaultOneBuildRecord;
import dev.onebuild.db.domain.model.types.OneBuildRecord;
import dev.onebuild.db.domain.repository.OneBuildDataRepository;
import dev.onebuild.db.utils.OneBuildDataRepositoryHelper;
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

  public OneBuildDataJdbcRepository(
      OneBuildDataRepositoryHelper oneBuildDataRepositoryHelper,
      Map<String, NamedParameterJdbcTemplate> jdbcTemplates) {
    this.oneBuildDataRepositoryHelper = oneBuildDataRepositoryHelper;
    this.jdbcTemplates = jdbcTemplates;
  }

  @Override
  public void save(DatabaseInfo database, OneBuildRecord record) {
    var jdbcTemplate = jdbcTemplates.get(database.getDataSource() + "JdbcTemplate");

    if(jdbcTemplate != null) {
      String sql = oneBuildDataRepositoryHelper.getSql(database, record);
      SqlParameterSource parameters = record.getSqlParameterSource();

      KeyHolder keyHolder = new GeneratedKeyHolder();
      jdbcTemplate.update(sql, parameters, keyHolder, new String[] { database.getId() });

      if(keyHolder.getKey() != null) {
        record.setId(keyHolder.getKey().longValue());
      }
    }
  }

  @Override
  public OneBuildRecord findById(DatabaseInfo database, Object id) {
    var jdbcTemplate = jdbcTemplates.get(database.getDataSource() + "JdbcTemplate");

    if(jdbcTemplate != null) {
      String sql = oneBuildDataRepositoryHelper.getSql(database);
      SqlParameterSource parameter = new MapSqlParameterSource().addValue(database.getId(), id);
      try {
        return jdbcTemplate.queryForObject(
            sql,
            parameter,
            (rs, rowNum) -> DefaultOneBuildRecord.from(database, rs)
        );
      } catch(EmptyResultDataAccessException e) {
        return null;
      } catch(Exception e) {
        log.error("Error while fetching record", e);
      }
    }
    return null;
  }

  @Override
  public List<OneBuildRecord> findAll(DatabaseInfo database) {
    var jdbcTemplate = jdbcTemplates.get(database.getDataSource() + "JdbcTemplate");

    if(jdbcTemplate != null) {
      String sql = oneBuildDataRepositoryHelper.getSql(database);
      try {
        return jdbcTemplate.query(
            sql,
            Collections.emptyMap(),
            (rs, rowNum) -> DefaultOneBuildRecord.from(database, rs)
        );
      } catch(EmptyResultDataAccessException e) {
        return null;
      } catch(Exception e) {
        log.error("Error while fetching record", e);
      }
    }
    return null;
  }

  @Override
  public void deleteById(DatabaseInfo database, Object id) {
    var jdbcTemplate = jdbcTemplates.get(database.getDataSource() + "JdbcTemplate");

    if(jdbcTemplate != null) {
      String sql = oneBuildDataRepositoryHelper.getSql(database);
      SqlParameterSource parameter = new MapSqlParameterSource().addValue(database.getId(), id);
      jdbcTemplate.update(sql, parameter);
    }
  }
}