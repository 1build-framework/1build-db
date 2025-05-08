package dev.onebuild.db.domain.service;

import dev.onebuild.domain.model.db.DatabaseInfo;
import dev.onebuild.domain.model.db.OneBuildRecord;
import dev.onebuild.domain.model.ui.OneBuildEndpoint;
import dev.onebuild.domain.model.ui.ResourceType;
import dev.onebuild.domain.repository.OneBuildDataRepository;
import dev.onebuild.domain.service.OneBuildDataService;
import dev.onebuild.errors.OneBuildExceptionFactory;

import java.util.List;
import java.util.Map;

public class OneBuildDataJdbcService implements OneBuildDataService {
  private final List<OneBuildEndpoint> endpoints;
  private final OneBuildDataRepository repository;
  private final OneBuildExceptionFactory exceptionFactory;

  public OneBuildDataJdbcService(List<OneBuildEndpoint> endpoints,
                                 OneBuildDataRepository repository,
                                 OneBuildExceptionFactory exceptionFactory) {
    this.endpoints = endpoints;
    this.repository = repository;
    this.exceptionFactory = exceptionFactory;
  }

  @Override
  public List<OneBuildRecord> find(String path, Map<String, Object> params) {
    OneBuildEndpoint endpoint = endpoints.stream()
        .filter(e -> e.getResourceType() == ResourceType.ENDPOINT)
        .filter(e -> e.getWebPath().equals(path))
        .findFirst()
        .orElseThrow(() -> exceptionFactory.createEndpointNotFoundException(path)).copy();
    return repository.find(endpoint, params);
  }

  @Override
  public void save(String path, String url, OneBuildRecord record) {
    OneBuildEndpoint endpoint = endpoints.stream()
        .filter(e -> e.getResourceType() == ResourceType.ENDPOINT)
        .filter(e -> e.getWebPath().equals(path))
        .findFirst()
        .orElseThrow(() -> exceptionFactory.createEndpointNotFoundException(url)).copy();
    repository.save(endpoint, record);
  }

  @Override
  public void save(DatabaseInfo databaseInfo, OneBuildRecord record) {
    repository.save(databaseInfo, record);
  }

  @Override
  public void deleteById(String path, String url, Long id) {
    OneBuildEndpoint endpoint = endpoints.stream()
        .filter(e -> e.getResourceType() == ResourceType.ENDPOINT)
        .filter(e -> e.getWebPath().equals(path))
        .findFirst()
        .orElseThrow(() -> exceptionFactory.createEndpointNotFoundException(url)).copy();
    repository.deleteById(endpoint, id);
  }

  @Override
  public void delete(String path, List<Long> ids) {
    OneBuildEndpoint endpoint = endpoints.stream()
        .filter(e -> e.getResourceType() == ResourceType.ENDPOINT)
        .filter(e -> e.getWebPath().equals(path))
        .findFirst()
        .orElseThrow(() -> exceptionFactory.createEndpointNotFoundException(path)).copy();
    repository.delete(endpoint, ids);
  }
}