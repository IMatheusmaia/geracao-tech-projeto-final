package com.agent.searcher.model.client;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import com.agent.searcher.model.prompts.DynamicPrompts;
import com.agent.searcher.model.schema.OutputSchema;
import com.agent.searcher.model.tools.ChatModelTools;

import io.lettuce.core.RedisException;


@Service
public class ChatClientService {
  
  private final ChatClient chatClient;
  private final ChatModelTools tools;
  private final MessageWindowChatMemory windowChatMemory;

  public ChatClientService(
      ChatClient chatClient,
      ChatModelTools tools,
      MessageWindowChatMemory windowChatMemory
  ) {
    this.chatClient = chatClient;
    this.tools = tools;
    this.windowChatMemory = windowChatMemory;
  }

  public OutputSchema getResponse(String userMessage, String chatId) throws RedisException, RuntimeException {

    List<Message> history = windowChatMemory.get(chatId);

    var output = chatClient
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
        .call().entity(OutputSchema.class);
    
    windowChatMemory.add(chatId, new UserMessage(userMessage));

    return output;
  }
  
}
