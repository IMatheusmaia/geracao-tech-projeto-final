package com.agent.agentApi.mcp.config;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;;

@Configuration(proxyBeanMethods = false)
public class MCPClientSettings {

  @Value("${spring.mcp.server.url}")
  private String serverUrl;


  @Bean
  public HttpClientSseClientTransport mcpSseTransport() {
    return HttpClientSseClientTransport.builder(serverUrl)
        .build();
  }

  @Bean
  @Primary
  public McpSyncClient mcpSyncClient(HttpClientSseClientTransport transport) {
    return McpClient.sync(transport)
    .requestTimeout(Duration.ZERO)
    .build();
  }

}
