package com.agent.searcher.rest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agent.searcher.rest.dto.request.SourceRequest;
import com.agent.searcher.rest.dto.response.SourceResponse;
import com.agent.searcher.rest.entity.SourceEntity;
import com.agent.searcher.rest.exception.ResourceNotFoundException;
import com.agent.searcher.rest.repository.SourceRepository;

@Service
public class SourceService {

  private final SourceRepository repository;

  public SourceService(SourceRepository repository) {
    this.repository = repository;
  }

  public SourceResponse save(SourceRequest request) {
    SourceEntity entity = new SourceEntity(request.name(), request.baseUrl());
    SourceEntity saved = repository.save(entity);
    return toResponse(saved);
  }

  public List<SourceResponse> findAll() {
    return repository.findAll().stream()
      .map(this::toResponse)
      .toList();
  }

  public void deleteById(String id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Fonte não encontrada com id: " + id);
    }
    repository.deleteById(id);
  }

  private SourceResponse toResponse(SourceEntity e) {
    return new SourceResponse(e.getId(), e.getName(), e.getBaseUrl());
  }
}
