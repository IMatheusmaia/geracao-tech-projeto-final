package com.agent.searcher.model.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.agent.searcher.model.prompts.ResumerPrompt;
import com.agent.searcher.model.schema.OutputSchema;

@Service
public class ResumerClientService {
  private final ChatClient resumerClient;

  public ResumerClientService(
      @Qualifier("resumerAgent")
      ChatClient resumerClient
  ) {
    this.resumerClient = resumerClient;
  }

  public OutputSchema.SearchedData getResume(String input) {
      return resumerClient
        .prompt()
          .system(new ResumerPrompt().prompt())
          .user(input)
          .call().responseEntity(OutputSchema.SearchedData.class)
          .getEntity();
  }
  
}
