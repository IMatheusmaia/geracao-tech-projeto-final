package com.agent.agentApi.rest.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.agent.agentApi.rest.dto.request.DishRequest;
import com.agent.agentApi.rest.entity.DishEntity;
import com.agent.agentApi.rest.exception.ResourceNotFoundException;
import com.agent.agentApi.rest.repository.DishRepository;

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

  public DishEntity createDish(DishRequest request) {
    DishEntity dish = new DishEntity(
        null, request.title(), request.description(), request.category(),
        request.price(), request.imageUrl(), request.ingredients()
    );
    return repository.save(dish);
  }

  public DishEntity updateDish(String id, DishRequest request) {
    DishEntity existing = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado com id: " + id));

    existing.setTitle(request.title());
    existing.setDescription(request.description());
    existing.setCategory(request.category());
    existing.setPrice(request.price());
    existing.setImageUrl(request.imageUrl());
    existing.setIngredients(request.ingredients());

    return repository.save(existing);
  }

  public void deleteDish(String id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Conteúdo não encontrado com id: " + id);
    }
    repository.deleteById(id);
  }
}
