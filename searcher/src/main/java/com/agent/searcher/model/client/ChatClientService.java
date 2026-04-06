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
import com.agent.searcher.model.schema.OutputSchema;
import com.agent.searcher.model.tools.ChatModelTools;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.lettuce.core.RedisException;


@Service
public class ChatClientService {

  private final ChatClient chatClient;
  private final ChatModelTools tools;
  private final MessageWindowChatMemory windowChatMemory;
  private final ObjectMapper objectMapper;

  public ChatClientService(
      ChatClient chatClient,
      ChatModelTools tools,
      MessageWindowChatMemory windowChatMemory,
      ObjectMapper objectMapper
  ) {
    this.chatClient = chatClient;
    this.tools = tools;
    this.windowChatMemory = windowChatMemory;
    this.objectMapper = objectMapper;
  }

  public OutputSchema getResponse(String userMessage, String chatId) throws RedisException, RuntimeException {

    List<Message> history = windowChatMemory.get(chatId);

    String rawResponse = chatClient
      .prompt()
        .system(new DynamicPrompts().system())
        .user(userMessage)
        .messages(history)
        .tools(tools)
        .advisors(
          MessageChatMemoryAdvisor.builder(
            windowChatMemory
          ).build()
        )
        .call().content();

    windowChatMemory.add(chatId, new UserMessage(userMessage));

    try {
      OutputSchema parsed = objectMapper.readValue(rawResponse, OutputSchema.class);
      windowChatMemory.add(chatId, new AssistantMessage(rawResponse));
      return parsed;
    } catch (Exception e) {
      windowChatMemory.add(chatId, new AssistantMessage(rawResponse));
      return new OutputSchema(List.of(new OutputSchema.Message(rawResponse)), null, null);
    }
  }

}
