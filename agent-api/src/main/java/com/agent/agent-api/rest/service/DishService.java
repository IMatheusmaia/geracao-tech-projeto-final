package com.agent.searcher.rest.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.agent.searcher.rest.entity.DishEntity;
import com.agent.searcher.rest.exception.ResourceNotFoundException;
import com.agent.searcher.rest.repository.DishRepository;

@Service
public class DishService {

  private final DishRepository repository;

  public DishService(DishRepository repository) {
    this.repository = repository;
  }

  public List<DishEntity> findAllDishs() {
      return repository.findAll();
  }

  public DishEntity findDishById(String id) {
     return repository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado com id: " + id));
  }

  public DishEntity saveDish(DishEntity dish) {
    return repository.save(dish);
  }

  public void deleteDish(String id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Conteúdo não encontrado com id: " + id);
    }
    repository.deleteById(id);
  }
}
