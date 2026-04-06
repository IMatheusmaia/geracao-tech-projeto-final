package com.agent.searcher.model.tools;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import io.modelcontextprotocol.spec.McpSchema.TextContent;

@Component
public class ChatModelTools {

  private static final Logger log = LoggerFactory.getLogger(ChatModelTools.class);

  private final McpSyncClient mcpSyncClient;
  private final ContentRepository contentRepository;
  private final SourceRepository sourceRepository;
  private final ResumerClientService resumerService;
  private final ObjectMapper mapper;

  public ChatModelTools(
    McpSyncClient mcpSyncClient,
    ContentRepository contentRepository,
    SourceRepository sourceRepository,
    ResumerClientService resumerService,
    ObjectMapper mapper
  ) {
    this.mcpSyncClient = mcpSyncClient;
    this.contentRepository = contentRepository;
    this.sourceRepository = sourceRepository;
    this.resumerService = resumerService;
    this.mapper = mapper;
  }

  @Tool(description = "Tool que é disparada quando o conteúdo que o usuário está buscando está no banco de dados vetorial (contexto RAG e intepretando o histórico de conversas o usuário manifestou claramente querer resgatar a informação)", name = "redirectToDB", returnDirect = false, resultConverter = DefaultToolCallResultConverter.class)
  protected OutputSchema.RetriveledData redirectToDB(@ToolParam(required = true) DBToolParam param) throws RedisException {
    log.info("[TOOL][redirectToDB] Buscando recurso no banco - id: {}", param.resourceId());
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

    String taskDescription = buildTaskDescription(param);
    log.info("============================================================");
    log.info("[TOOL][redirectToMCPClient] Iniciando busca via MCP");
    log.info("[TOOL][redirectToMCPClient] Tipo de busca: {}", param.type().name());
    log.info("[TOOL][redirectToMCPClient] Task description: {}", taskDescription);

    List<SourceEntity> sources = sourceRepository.findAll();
    if (sources.isEmpty()) {
      throw new RuntimeException("Nenhuma fonte cadastrada para busca via Browser Agent");
    }

    log.info("[TOOL][redirectToMCPClient] Fontes encontradas: {} - URLs: {}",
      sources.size(),
      sources.stream().map(SourceEntity::getBaseUrl).toList()
    );

    List<McpToolResultDTO> scrapedData = sources.stream().map((source) -> {
      String siteUrl = source.getBaseUrl();
      log.info("[TOOL][MCP CALL] Enviando requisicao para search_task - site: {}", siteUrl);

      try {
        CallToolRequest toolRequest = new CallToolRequest(
          "search_task",
          Map.of(
            "describe_task", taskDescription,
            "site", siteUrl
          )
        );

        CallToolResult result = mcpSyncClient.callTool(toolRequest);
        log.info("[TOOL][MCP RESULT] Resposta recebida - isError: {}", result.isError());

        if (Boolean.FALSE.equals(result.isError())) {
          // Ler o conteudo de texto retornado pela tool MCP
          String rawText = result.content().stream()
            .filter(c -> c instanceof TextContent)
            .map(c -> ((TextContent) c).text())
            .collect(Collectors.joining());

          if (rawText != null && !rawText.isBlank()) {
            log.info("[TOOL][MCP RESULT] Raw text recebido ({} bytes): {}",
              rawText.length(),
              rawText.length() > 300 ? rawText.substring(0, 300) + "..." : rawText
            );

            try {
              McpToolResultDTO parsed = mapper.readValue(rawText, McpToolResultDTO.class);
              log.info("[TOOL][MCP RESULT] Parseado com sucesso - {} noticias",
                parsed.news() != null ? parsed.news().size() : 0
              );
              return parsed;
            } catch (Exception parseError) {
              log.error("[TOOL][MCP RESULT] Falha ao parsear JSON do MCP: {}", parseError.getMessage());
              log.error("[TOOL][MCP RESULT] Conteudo que falhou: {}", rawText);
              return null;
            }
          } else {
            log.warn("[TOOL][MCP RESULT] Resultado vazio ou sem TextContent");
            log.debug("[TOOL][MCP RESULT] Content items: {}", result.content());
          }
        } else {
          log.error("[TOOL][MCP RESULT] Tool retornou erro: {}", result.content());
        }
      } catch (Exception e) {
        log.error("[TOOL][MCP CALL] Falha na chamada MCP para site {}: {}", siteUrl, e.getMessage(), e);
      }
      return null;
    }).filter(Objects::nonNull).toList();

    log.info("[TOOL][RESUMER] Dados coletados de {} fontes. Enviando para resumo...", scrapedData.size());

    try {
      String scrapedJson = mapper.writeValueAsString(scrapedData);
      log.debug("[TOOL][RESUMER] Input para resumo ({} bytes): {}", scrapedJson.length(),
        scrapedJson.length() > 500 ? scrapedJson.substring(0, 500) + "..." : scrapedJson
      );

      OutputSchema.SearchedData resume = resumerService.getResume(scrapedJson);
      ContentEntity entity = OutputSchema.SearchedData.toEntity(resume);
      contentRepository.save(entity);

      log.info("[TOOL][RESUMER] Resumo gerado e salvo - title: {}", resume.title());
      log.info("============================================================");
      return resume;

    } catch (Exception e) {
      log.error("[TOOL][RESUMER] Erro ao resumir dados: {}", e.getMessage(), e);
      throw new RuntimeException("Erro ao processar resultado da busca: " + e.getMessage(), e);
    }
  }

  private String buildTaskDescription(ToolMCPParam param) {
    StringBuilder sb = new StringBuilder();
    sb.append("Tipo de busca: ").append(param.type().name()).append("\n");
    sb.append("Descricao: ").append(param.type().description()).append("\n");

    if (param.steps() != null && !param.steps().isEmpty()) {
      sb.append("\nPassos:\n");
      for (ToolMCPParam.Step step : param.steps()) {
        sb.append("- ").append(step.step()).append(": ").append(step.description()).append("\n");
      }
    }

    sb.append("\nFormato de saida esperado: JSON com campos title, content, pub_date, name_source, url_source");
    sb.append("\nLimite: maximo 3 noticias por busca");
    return sb.toString();
  }
}