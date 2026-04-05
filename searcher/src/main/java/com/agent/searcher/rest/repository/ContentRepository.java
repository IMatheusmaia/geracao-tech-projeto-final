package com.agent.searcher.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.agent.searcher.rest.entity.ContentEntity;

public interface ContentRepository extends CrudRepository<ContentEntity, String> {

  List<ContentEntity> findAll();

  Optional<ContentEntity> findById(String id);

  void deleteById(String id);

}
