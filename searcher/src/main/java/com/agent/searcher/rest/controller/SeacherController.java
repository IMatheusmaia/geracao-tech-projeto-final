package com.agent.searcher.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agent.searcher.model.client.ChatClientService;
import com.agent.searcher.model.schema.OutputSchema;
import com.agent.searcher.rest.dto.request.SourceRequest;
import com.agent.searcher.rest.dto.response.ContentResponse;
import com.agent.searcher.rest.dto.response.SourceResponse;
import com.agent.searcher.rest.service.ContentService;
import com.agent.searcher.rest.service.SourceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SeacherController {

  private final ChatClientService chatService;
  private final ContentService contentService;
  private final SourceService sourceService;

  public SeacherController(
    ChatClientService chatService,
    ContentService contentService,
    SourceService sourceService
  ) {
    this.chatService = chatService;
    this.contentService = contentService;
    this.sourceService = sourceService;
  }

  // === Search ===

  @PostMapping("/search")
  public ResponseEntity<OutputSchema> search(@RequestBody String request) {
    Optional<OutputSchema> response = Optional.ofNullable(
      chatService.getResponse(request, "default-user")
    );

    if (response.isPresent()) {
      return ResponseEntity.ok(response.get());
    }

    return ResponseEntity.noContent().build();
  }

  // === Content ===

  @GetMapping("/contents")
  public ResponseEntity<List<ContentResponse>> listContents() {
    List<ContentResponse> contents = contentService.findAll();
    if (contents.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(contents);
  }

  @GetMapping("/contents/{id}")
  public ResponseEntity<ContentResponse> getContent(@PathVariable String id) {
    return ResponseEntity.ok(contentService.findById(id));
  }

  @DeleteMapping("/contents/{id}")
  public ResponseEntity<Void> deleteContent(@PathVariable String id) {
    contentService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  // === Source ===

  @PostMapping("/sources")
  public ResponseEntity<SourceResponse> createSource(@Valid @RequestBody SourceRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(sourceService.save(request));
  }

  @GetMapping("/sources")
  public ResponseEntity<List<SourceResponse>> listSources() {
    List<SourceResponse> sources = sourceService.findAll();
    if (sources.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(sources);
  }

  @DeleteMapping("/sources/{id}")
  public ResponseEntity<Void> deleteSource(@PathVariable String id) {
    sourceService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
