package com.agent.agentApi.rest.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.agent.agentApi.mcp.client.MCPClientService;
import com.agent.agentApi.model.client.ChatClientService;
import com.agent.agentApi.rest.dto.request.DishRequest;
import com.agent.agentApi.rest.dto.request.UpdateImageUrlRequest;
import com.agent.agentApi.rest.dto.response.ListImagesByDish;
import com.agent.agentApi.rest.entity.DishEntity;
import com.agent.agentApi.rest.service.DishService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class MenuController {

  private final ChatClientService chatService;
  private final DishService dishService;
  private final MCPClientService mcpClient;

  public MenuController(
    ChatClientService chatService,
    DishService dishService,
    MCPClientService mcpClient
  ) {
    this.chatService = chatService;
    this.dishService = dishService;
    this.mcpClient = mcpClient;
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

  @GetMapping("/dish/{id}/image-search")
  public ResponseEntity<ListImagesByDish> getImagesByDish(@PathVariable String id, @RequestParam(defaultValue = "3") Integer limitResults) {
    ObjectMapper mapper = new ObjectMapper();

    try {
      var dishData = dishService.findDishById(id);

      var request = Map.of(
        "limit_results", limitResults,
        "dish_data", dishData
      );
      var input = mapper.writeValueAsString(request);

      var mcpResult = mcpClient.searchImagesByDish(input);

      ListImagesByDish listImages = mapper.convertValue(mcpResult.structuredContent(), ListImagesByDish.class);

      return ResponseEntity.ok(listImages);

    } catch (JsonProcessingException e) {
      return ResponseEntity.internalServerError().build();
    }
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

  @PatchMapping("/dish/{id}")
  public ResponseEntity<DishEntity> updateImageUrl(@PathVariable String id, @Valid @RequestBody UpdateImageUrlRequest request) {
    DishEntity updated = dishService.updateImageUrl(id, request);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/dish/{id}")
  public ResponseEntity<Void> deleteContent(@PathVariable String id) {
    dishService.deleteDish(id);
    return ResponseEntity.noContent().build();
  }
}
