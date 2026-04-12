package com.agent.searcher.model.client;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import com.agent.searcher.model.prompts.DynamicPrompts;
import com.agent.searcher.model.schema.OutputChatSchema;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class ChatClientService {

  private final ChatClient chatClient;
  private final MessageWindowChatMemory windowChatMemory;
  private final ObjectMapper objectMapper;

  public ChatClientService(
      ChatClient chatClient,
      MessageWindowChatMemory windowChatMemory,
      ObjectMapper objectMapper
  ) {
    this.chatClient = chatClient;
    this.windowChatMemory = windowChatMemory;
    this.objectMapper = objectMapper;
  }

  public OutputChatSchema getResponse(String userMessage, String chatId) {

    List<Message> history = windowChatMemory.get(chatId);

    String rawResponse = chatClient
      .prompt()
        .system(new DynamicPrompts().system())
        .user(userMessage)
        .messages(history)
        .advisors(
          MessageChatMemoryAdvisor.builder(
            windowChatMemory
          ).build()
        )
        .call().content();

    windowChatMemory.add(chatId, new UserMessage(userMessage));

    try {
      OutputChatSchema parsed = objectMapper.readValue(rawResponse, OutputChatSchema.class);
      windowChatMemory.add(chatId, new AssistantMessage(rawResponse));
      return parsed;
    } catch (Exception e) {
      windowChatMemory.add(chatId, new AssistantMessage(rawResponse));
      return new OutputChatSchema(List.of());
    }
  }

}
