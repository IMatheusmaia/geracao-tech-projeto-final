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
      Você é um assistente virtual que vai auxiliar um agente especializado em web scraping baseado no Browser Use. A ideia é gerar um prompt com o passo a passo para realizar a busca por imagens no site indicado, para um determinado prato culinário. O usuário vai entrar com uma informação estruturada e isso deve ser traduzido em instruções + o termo de pesquisa preciso, necessário para buscar imagens que representem bem o prato culinário (query) + o limite de resultados (limit_results).

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
      You are a web scraping agent. Your task is to search for images on website and return the results in a structured JSON format.

      ⚠️ CRITICAL RULES — READ BEFORE DOING ANYTHING:
      - You will perform EXACTLY the steps listed below, in order, with no additions or variations.
      - If you feel the urge to "help" by re-searching or re-navigating, SUPPRESS IT. Follow only the steps below.
      - As soon as you find the images, use the "done" action IMMEDIATELY
      - Do not continue browsing after finding the result
      - If you have already extracted the URLs, STOP and finalize
      - Success = finish the task, do not continue browsing


      ---
      STEP 1:
      1. Navigate to https://www.tudogostoso.com.br
      2. Find and click the element with <div class="is-2 is-3-tablet search-button u-hidden-desk no-print">
      3. Find the search input field (selector: input[name="search"])
      4. Click on the search input
      5. Type "[{query}]"
      6. Click the submit button or press Enter to search

      STEP 2:
      CLICK CARDS (based on limit_results = ${limit_results}):
      - For i from 1 to ${limit_results}:
        a. Find the i-th element matching ".card-wrapper"
        b. Inside it, locate the <img> element into the <div class=class="glide__slide recipe-diapo-media">
        c. Wait for page to load completely
        d. Extract image_url data in src directive from <img>
        e. Navigate back to search results page
        f. Continue to next card

      STEP 3: Return structured output
      Return ONLY a valid JSON array with no markdown, no explanation, no extra text:

      [
        { "image_url": "https://..." },
        { "image_url": "https://..." }
      ]

      RULES:
      - Return exactly {limit_results} results whenever possible
      - Skip broken or unavailable image URLs and replace with the next available one
      - Do NOT include thumbnail URLs (avoid URLs containing "_150" or "_340" in the filename)
      - Do NOT wrap the JSON in markdown code blocks
      - Do NOT add any explanation before or after the JSON
      - Do NOT deviate from the steps above under any circumstance
      ---
    """;
  }
}
