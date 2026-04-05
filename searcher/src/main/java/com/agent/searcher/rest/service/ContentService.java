package com.agent.searcher.rest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agent.searcher.rest.dto.response.ContentResponse;
import com.agent.searcher.rest.entity.ContentEntity;
import com.agent.searcher.rest.exception.ResourceNotFoundException;
import com.agent.searcher.rest.repository.ContentRepository;

@Service
public class ContentService {

  private final ContentRepository repository;

  public ContentService(ContentRepository repository) {
    this.repository = repository;
  }

  public List<ContentResponse> findAll() {
    return repository.findAll().stream()
      .map(this::toResponse)
      .toList();
  }

  public ContentResponse findById(String id) {
    ContentEntity entity = repository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado com id: " + id));
    return toResponse(entity);
  }

  public void deleteById(String id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Conteúdo não encontrado com id: " + id);
    }
    repository.deleteById(id);
  }

  private ContentResponse toResponse(ContentEntity e) {
    return new ContentResponse(e.getId(), e.getTitle(), e.getContent(), e.getSavedAt(), e.getUpdatedAt());
  }
}
