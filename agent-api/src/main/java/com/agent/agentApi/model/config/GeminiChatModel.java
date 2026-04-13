package com.agent.agentApi.model.config;

import com.agent.agentApi.model.schema.OutputChatSchema;
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
            .temperature(0.7)
            .includeThoughts(false)
            .outputSchema(OutputChatSchema.toJSONSchema())
            .build()
          )
          .build()
    );
	}

  @Bean
  @Qualifier("paramProvider")
  public ChatClient paramProvider(GoogleGenAiChatModel.Builder builder) {
    return ChatClient.create(
      builder
      .defaultOptions(
        GoogleGenAiChatOptions.builder()
        .model(ChatModel.GEMINI_2_5_FLASH)
        .temperature(0.1)
        .includeThoughts(false)
        .build()
      )
      .build());
  }
    

}
