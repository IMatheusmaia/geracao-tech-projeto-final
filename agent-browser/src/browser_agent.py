import logging
import json
import traceback
from datetime import datetime
from browser_use import Agent, Browser, ChatGoogle
from dotenv import load_dotenv
from os import getenv
from pydantic import BaseModel

logger = logging.getLogger("browser_agent")


class DishImage(BaseModel):
    url_source: str

class ListImages(BaseModel):
    images: list[DishImage]

class BrowserAgent:

    def __init__(self, task: str, dish: str | None = None):
        load_dotenv()

        self.task = task
        self.site = dish
        self.api_key = getenv("GOOGLE_API_KEY")


        full_task = task
        if dish:
            full_task = (
                f"{task}\n\n"
                f"Prato alvo para busca: {dish}\n"
            )
            logger.info("[INPUT] Descrição da task completa: %s", full_task)

        self.browser = Browser(
            headless=True,
            window_size={'width': 1024, 'height': 768},
            no_viewport=True,
            executable_path=getenv("CHROMIUM_BIN_PATH"),
            keep_alive=True,
            wait_for_network_idle_page_load_time=1.5,
            disable_security=True
        )
        self.model = ChatGoogle(
                model="gemini-2.5-pro",
                temperature=0.6,
                supports_structured_output=True,
                api_key=self.api_key
            )

        logger.info("[INIT] Browser configurado - headless=True, chromium=%s", getenv("CHROMIUM_BIN_PATH"))

        self.agent = Agent(
            llm=self.model,
            task=full_task,
            browser=self.browser,
            chat=self.model,
            output_model_schema=ListImages
        )

        logger.info("[INIT] Agente criado - modelo=gemini-2.5-pro, output_schema=NewsResult")

    async def run(self) -> dict:
        """
        Executa o agente de browser e retorna o resultado estruturado.
        Retorna um dict (never None) para compatibilidade com MCP tool return.
        """
        logger.info("[RUN] Iniciando execucao do agente...")

        try:
            result = await self.agent.run()
            logger.info("[RUN] Agente finalizado com sucesso")

            # Log do resultado bruto para debug
            raw_result = str(result)
            logger.debug("[RUN] Resultado bruto (primeiros 500 chars): %s", raw_result[:500])

            structured = result.get_structured_output(ListImages)
            logger.info("[RUN] Structured output obtido com sucesso")

            # Log detalhado de cada noticia capturada
            if structured and structured.news:
                logger.info("[OUTPUT] Total de noticias capturadas: %d", len(structured.news))
                for i, item in enumerate(structured.news):
                    logger.info("[OUTPUT] Noticia %d:", i + 1)
                    logger.info("  title: %s", item.title[:80] if item.title else "N/A")
                    logger.info("  content: %s", item.content[:100] if item.content else "N/A")
                    logger.info("  pub_date: %s", item.pub_date or "N/A")
                    logger.info("  name_source: %s", item.name_source or "N/A")
                    logger.info("  url_source: %s", item.url_source or "N/A")
            else:
                logger.warning("[OUTPUT] structured output retornou None ou lista vazia!")
                logger.warning("[OUTPUT] Tentando extrair resultado alternativo...")

            output = structured.model_dump()
            output_json = json.dumps(output, ensure_ascii=False, indent=2)
            logger.info("[OUTPUT] JSON final enviado (%d bytes):\n%s", len(output_json), output_json)

            return output

        except Exception as e:
            logger.error("[ERROR] Falha na execucao do agente: %s", str(e))
            logger.error("[ERROR] Traceback completo:\n%s", traceback.format_exc())
            raise
