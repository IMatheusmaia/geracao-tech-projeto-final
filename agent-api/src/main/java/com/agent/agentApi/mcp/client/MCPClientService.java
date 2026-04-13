package com.agent.agentApi.mcp.client;

import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.agent.agentApi.model.prompts.DynamicPrompts;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;

@Service
public class MCPClientService {

  private final McpSyncClient mcpSyncClient;
  
  @Qualifier("paramProvider")
  private final ChatClient model;

  public MCPClientService(McpSyncClient mcpSyncClient, ChatClient model) {
    this.mcpSyncClient = mcpSyncClient;
    this.model = model;
  }

  public CallToolResult searchImagesByDish(String userInput) {
      var modelResponse = model.prompt()
        .system(DynamicPrompts.imagesByDishPrompt())
        .user(userInput)
        .call().content();

        CallToolRequest mcpRequest = new CallToolRequest("search_images_by_dish", Map.of(
          "describe_task", modelResponse
        ));

        return mcpSyncClient.callTool(mcpRequest);
  }
  
}
