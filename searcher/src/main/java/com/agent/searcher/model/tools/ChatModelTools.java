package com.agent.searcher.model.tools;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.execution.DefaultToolCallResultConverter;
import org.springframework.stereotype.Component;
import com.agent.searcher.mcp.dto.McpToolResultDTO;
import com.agent.searcher.model.client.ResumerClientService;
import com.agent.searcher.model.schema.OutputSchema;
import com.agent.searcher.model.tools.params.DBToolParam;
import com.agent.searcher.model.tools.params.ToolMCPParam;
import com.agent.searcher.rest.entity.ContentEntity;
import com.agent.searcher.rest.entity.SourceEntity;
import com.agent.searcher.rest.repository.ContentRepository;
import com.agent.searcher.rest.repository.SourceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisException;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;

@Component
public class ChatModelTools {
  private final McpSyncClient mcpSyncClient;
  private final ContentRepository contentRepository;
  private final SourceRepository sourceRepository;
  private final ResumerClientService resumerService;

  public ChatModelTools(
    McpSyncClient mcpSyncClient,
    ContentRepository contentRepository,
    SourceRepository sourceRepository,
    ResumerClientService resumerService
  ) {
    this.mcpSyncClient = mcpSyncClient;
    this.contentRepository = contentRepository;
    this.sourceRepository = sourceRepository;
    this.resumerService = resumerService;
  }

  @Tool(description = "Tool que é disparada quando o conteúdo que o usuário está buscando está no banco de dados vetorial (contexto RAG e intepretando o histórico de conversas o usuário manifestou claramente querer resgatar a informação)", name = "redirectToDB", returnDirect = false, resultConverter = DefaultToolCallResultConverter.class)
  protected OutputSchema.RetriveledData redirectToDB(@ToolParam(required = true) DBToolParam param) throws RedisException {
    return OutputSchema.RetriveledData.fromEntity(
      contentRepository.findById(param.resourceId()).orElseThrow(
        () -> new RedisException("Recurso %s não encontrado no banco de dados".formatted(param.resourceId()))
      )
    );
  }

  @Tool(description = "Tool que é disparada quando o conteúdo não está no contexto RAG, ou insuficinte para o usuário", name = "redirectToMCPClient", returnDirect = false, resultConverter = DefaultToolCallResultConverter.class)
  protected OutputSchema.SearchedData redirectToMCPClient(
    @ToolParam(required = true) ToolMCPParam param
  ) throws RuntimeException {
      ObjectMapper mapper = new ObjectMapper();
      List<SourceEntity> sources = sourceRepository.findAll();

      if (sources.isEmpty()) {
        throw new RuntimeException("Nenhuma dado pode ser capturado do Browser Agent");
      }
    
      List<McpToolResultDTO> scrapedData = sources.stream().map((item) -> {
          CallToolRequest toolRequest = new CallToolRequest(
        "search_task",
        Map.of(
          "task", param,
          "site", item.getBaseUrl()
        )
      );
      CallToolResult result = mcpSyncClient.callTool(toolRequest);

      if (Boolean.FALSE.equals(result.isError())) {

        Object raw = result.structuredContent();

        if (Objects.nonNull(raw)) {
            try {
                return mapper.convertValue(raw, McpToolResultDTO.class );
            } catch (IllegalArgumentException error) {
                throw new RuntimeException("Erro ao converter resultado da tool MCP", error);
            }
        }
      }
      return null;
      }).toList();

      try {
        OutputSchema.SearchedData resume = resumerService.getResume(
          mapper.writeValueAsString(scrapedData)
        );
        ContentEntity entity = OutputSchema.SearchedData.toEntity(resume);
        contentRepository.save(entity);
        
        return resume;

      } catch (Exception e) {
        System.err.println("Erro ao resumir dados: " + e.getMessage());
        return null;
      }
  }
  
}
