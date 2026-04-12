package com.agent.searcher.rest.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.agent.searcher.model.client.ChatClientService;
import com.agent.searcher.rest.entity.DishEntity;
import com.agent.searcher.rest.service.DishService;

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

  @DeleteMapping("/dish/{id}")
  public ResponseEntity<Void> deleteContent(@PathVariable String id) {
    dishService.deleteDish(id);
    return ResponseEntity.noContent().build();
  }
}
