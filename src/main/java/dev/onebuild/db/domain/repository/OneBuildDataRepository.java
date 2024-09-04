package dev.onebuild.db.domain.repository;

import dev.onebuild.db.domain.model.config.DatabaseInfo;
import dev.onebuild.db.domain.model.types.OneBuildRecord;

import java.util.List;

public interface OneBuildDataRepository {
  void save(DatabaseInfo database, OneBuildRecord record);
  OneBuildRecord findById(DatabaseInfo database, Object id);
  List<OneBuildRecord> findAll(DatabaseInfo database);
  void deleteById(DatabaseInfo database, Object id);
}