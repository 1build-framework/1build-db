package dev.onebuild.db.domain.service;

import dev.onebuild.domain.model.db.OneBuildRecord;
import dev.onebuild.domain.model.ui.OneBuildEndpoint;
import dev.onebuild.domain.model.ui.ResourceType;
import dev.onebuild.domain.repository.OneBuildDataRepository;
import dev.onebuild.domain.service.OneBuildDataService;
import dev.onebuild.utils.OneBuildExceptionFactory;

import java.util.List;

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
  public OneBuildRecord findById(String path, Long id) {
    OneBuildEndpoint endpoint = endpoints.stream()
        .filter(e -> e.getResourceType() == ResourceType.ENDPOINT)
        .filter(e -> e.getPath().equals(path))
        .findFirst()
        .orElseThrow(() -> exceptionFactory.createEndpointNotFoundException(path));
    return repository.findById(endpoint, id);
  }

  @Override
  public List<OneBuildRecord> findAll(String path) {
    OneBuildEndpoint endpoint = endpoints.stream()
        .filter(e -> e.getResourceType() == ResourceType.ENDPOINT)
        .filter(e -> e.getPath().equals(path))
        .findFirst()
        .orElseThrow(() -> exceptionFactory.createEndpointNotFoundException(path));
    return repository.findAll(endpoint);
  }

  @Override
  public void save(String path, OneBuildRecord record) {
    OneBuildEndpoint endpoint = endpoints.stream()
        .filter(e -> e.getResourceType() == ResourceType.ENDPOINT)
        .filter(e -> e.getPath().equals(path))
        .findFirst()
        .orElseThrow(() -> exceptionFactory.createEndpointNotFoundException(path));
    repository.save(endpoint, record);
  }

  @Override
  public void deleteById(String path, Long id) {
    OneBuildEndpoint endpoint = endpoints.stream()
        .filter(e -> e.getResourceType() == ResourceType.ENDPOINT)
        .filter(e -> e.getPath().equals(path))
        .findFirst()
        .orElseThrow(() -> exceptionFactory.createEndpointNotFoundException(path));
    repository.deleteById(endpoint, id);
  }
}