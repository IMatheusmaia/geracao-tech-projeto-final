package com.agent.searcher.model.config;

import com.agent.searcher.model.schema.OutputSchema;
import com.google.genai.Client;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatModel.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
  @Primary
	public ChatClient chatClient(GoogleGenAiChatModel.Builder chatModelBuilder) {
		return ChatClient.create(
      chatModelBuilder
        .defaultOptions(
            GoogleGenAiChatOptions.builder()
            .model(ChatModel.GEMINI_2_5_FLASH)
            .temperature(0.5)
            .includeThoughts(false)
            .build()
          )
          .build()
    );
	}

  @Bean
  @Qualifier("resumerAgent")
  public ChatClient resumerAgent(GoogleGenAiChatModel.Builder chatModelBuilder) {
    return ChatClient.create(
      chatModelBuilder
        .defaultOptions(
            GoogleGenAiChatOptions.builder()
            .model(ChatModel.GEMINI_2_5_FLASH)
            .temperature(0.8)
            .includeThoughts(false)
            .outputSchema(OutputSchema.SearchedData.toJSONSchema())
            .build()
          )
          .build()
    );
  }
    
}
