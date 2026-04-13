package com.agent.agentApi.model.prompts;

import java.util.Map;
import java.util.function.Consumer;
import org.springframework.ai.chat.client.ChatClient.PromptSystemSpec;
import com.agent.agentApi.model.prompts.utils.DateTimeBR;

public record DynamicPrompts(
) {
  
  public static Consumer<PromptSystemSpec> chatSysPrompt() {
    return prompt -> prompt.param(
      """
      # Contexto: #
      Agora é {time}, da região {region}, e você é um assistente virtual especializado em informar o usuário dos pratos disponíveis e dar informações acerca dos ingredientes, preço e outras informações nutricionais.
      """
      , Map.of(
          "time", DateTimeBR.getTime(),
          "region", "São Paulo - Brasil"
        ));
  }

  public static String imagesByDishPrompt() {
    return 
      """
        ## Contexto ##
        Você é um assistente virtual que vai auxiliar um agente especializado em web scraping baseado no Browser Use. A ideia é gerar um prompt com o passo a passo para realizar a busca por imagens no Google para um determinado prato culinário. O usuário vai entrar com uma informação estruturada e isso deve ser traduzido em instruções + o termo de pesquisa preciso, necessário para buscar imagens que representem bem o prato culinário (query) + o limite de resultados (limit_results).

        ## Tarefa ##
        Gerar um prompt com as instruções necessárias para que o agente consiga buscar imagens de um prato específico.

        ### Entrada esperada do usuário ###

        {
          limit_results: 3,
          dish_data: {
            title: "Feijoada"
            description: "Prato típico brasileiro, feito com feijão preto e carnes variadas."
            category: "Tradicional Brasileira"
            ingredients: ["Feijão preto", "Carne de porco", "Linguiça", "Costela de porco"]
          }
        }

        ### Saída esperada ###
        You are a web scraping agent. Your task is to search for images on Google Images and return the results in a structured JSON format.
        ---
        Follow the steps below carefully:

        STEP 1: Navigate to the URL
        Open the browser and navigate to:
        https://www.google.com/search?q={query}&tbm=isch&tbs=itp:photo

        STEP 2: Wait for page load
        Wait until the image grid is fully loaded on the page.

        STEP 3: Scroll to load more images
        Scroll down slowly until at least {limit_results} images are visible on the page.

        STEP 4: Extract image URLs
        For each image thumbnail visible on the page:
        - Click on the image to open the side panel
        - Wait for the full-resolution image to load in the side panel
        - Extract the full-resolution image URL (not the thumbnail)
        - Close the side panel or go back to the grid
        - Repeat until you have collected {limit_results} image URLs

        STEP 5: Return structured output
        Return ONLY a valid JSON array with no extra text, no markdown, no explanation.
        The output must follow exactly this structure:

        [
          { "image_url": "https://..." },
          { "image_url": "https://..." }
           ...
        ]

        RULES:
        - Return exactly {limit_results} results whenever possible
        - Skip broken or unavailable image URLs
        - Do NOT include thumbnail URLs (avoid URLs containing "encrypted-tbn" or "gstatic")
        - Do NOT wrap the JSON in markdown code blocks
        - Do NOT add any explanation before or after the JSON
        ---
      """;
  }
}
