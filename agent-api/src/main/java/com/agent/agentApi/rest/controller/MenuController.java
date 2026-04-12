package com.agent.agentApi.rest.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.agent.agentApi.model.client.ChatClientService;
import com.agent.agentApi.rest.dto.request.DishRequest;
import com.agent.agentApi.rest.entity.DishEntity;
import com.agent.agentApi.rest.service.DishService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class MenuController {

  private final ChatClientService chatService;
  private final DishService dishService;

  public MenuController(
    ChatClientService chatService,
    DishService dishService
  ) {
    this.chatService = chatService;
    this.dishService = dishService;
  }

  // === Chat ===

  @PostMapping("/chat")
  public void search(@RequestBody String request) {
    try {
      // chatService.getResponse(request, "default-user");
    } catch(Exception e) {

    }
  }

  // === Menu ===

  @GetMapping("/dish")
  public ResponseEntity<List<DishEntity>> listDishs() {
    return ResponseEntity.ok(dishService.findAllDishs());
  }

  @GetMapping("/dish/{id}")
  public ResponseEntity<DishEntity> getDish(@PathVariable String id) {
    return ResponseEntity.ok(dishService.findDishById(id));
  }

  @PostMapping("/dish")
  public ResponseEntity<DishEntity> createDish(@Valid @RequestBody DishRequest request) {
    DishEntity created = dishService.createDish(request);
    return ResponseEntity.ok(created);
  }

  @PutMapping("/dish/{id}")
  public ResponseEntity<DishEntity> updateDish(@PathVariable String id, @Valid @RequestBody DishRequest request) {
    DishEntity updated = dishService.updateDish(id, request);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/dish/{id}")
  public ResponseEntity<Void> deleteContent(@PathVariable String id) {
    dishService.deleteDish(id);
    return ResponseEntity.noContent().build();
  }
}
