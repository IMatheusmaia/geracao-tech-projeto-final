package com.agent.searcher.model.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class ChatModelTools {

  @Tool(description = "Redirect to MCP Client")
  protected void redirectToMCPClient(
    @ToolParam() String message
  ) {

  }
  
}
