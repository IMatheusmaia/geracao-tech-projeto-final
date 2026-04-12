package com.agent.agentApi.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.agent.agentApi.rest.entity.DishEntity;

public interface DishRepository extends CrudRepository<DishEntity, String> {

  List<DishEntity> findAll();

  List<DishEntity> findByCategory(String category);

  Optional<DishEntity> findById(String id);

  void deleteById(String id);

}
