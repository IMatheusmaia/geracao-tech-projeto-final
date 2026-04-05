package com.agent.searcher.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.agent.searcher.rest.entity.SourceEntity;

public interface SourceRepository extends CrudRepository<SourceEntity, String> {

  List<SourceEntity> findAll();

  Optional<SourceEntity> findById(String id);

  void deleteById(String id);

}
