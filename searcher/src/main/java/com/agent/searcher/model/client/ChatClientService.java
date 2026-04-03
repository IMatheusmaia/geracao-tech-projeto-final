package com.agent.searcher.model.client;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import com.agent.searcher.model.prompts.DynamicPrompts;
import com.agent.searcher.model.tools.ChatModelTools;


@Service
public class ChatClientService {
  
  private final ChatClient chatClient;
  private final ChatModelTools tools;

  public ChatClientService(
      ChatClient chatClient,
      ChatModelTools tools
  ) {
    this.chatClient = chatClient;
    this.tools = tools;
  }

  public String getResponse(DynamicPrompts prompt, List<Message> history) {
     
      return chatClient
        .prompt()
          .system(prompt.system())
          .user(prompt.userMessage())
          .messages(history)
          .tools(tools)
          .call().content();
  }
  
}
