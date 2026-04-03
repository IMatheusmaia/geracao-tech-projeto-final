package com.agent.searcher.model.config;

import com.google.genai.Client;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class GeminiChatModel {

  @Bean
  public Client clientConfig() {
    return Client.builder()
      .vertexAI(false)
      .build();
  }

  @Bean
  public GoogleGenAiChatModel.Builder chatModelBuilder(Client clientConfig) {
    return GoogleGenAiChatModel.builder().genAiClient(clientConfig);
  }

  @Bean
	public ChatClient chatClient(GoogleGenAiChatModel.Builder chatModelBuilder) {
		return ChatClient.create(
      chatModelBuilder
        .defaultOptions(
            GoogleGenAiChatOptions.builder()
            .model("gemini-2.5-flash")
            .temperature(0.6)
            .includeThoughts(false)
            .build()
          )
          .build()
    );
	}
    
}
