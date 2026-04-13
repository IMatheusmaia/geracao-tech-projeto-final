import logging
import traceback
from browser_use import Agent, Browser, ChatGoogle, Controller
from dotenv import load_dotenv
from os import getenv

logger = logging.getLogger("browser_agent")


class BrowserAgent:

    def __init__(self, task: str, output:object):
        load_dotenv()
        self.api_key = getenv("GOOGLE_API_KEY")

        controller = Controller(output_model=output)

        self.task = task
        self.output = output
        self.controller = controller
        self.browser = Browser(
            headless=True,
            window_size={'width': 1024, 'height': 768},
            no_viewport=True,
            executable_path=getenv("CHROMIUM_BIN_PATH"),
            keep_alive=True,
            wait_for_network_idle_page_load_time=1.5,
            disable_security=True
        )
        model_name = getenv("BROWSER_MODEL", "gemini-2.5-flash")
        self.model = ChatGoogle(
                model=model_name,
                temperature=0.6,
                supports_structured_output=True,
                api_key=self.api_key
            )

        logger.info("[INIT] Browser configurado - headless=True, chromium_path_bin=%s", getenv("CHROMIUM_BIN_PATH"))

        self.agent = Agent(
            max_steps=25,
            llm=self.model,
            task=self.task,
            browser=self.browser,
            chat=self.model,
            output_model_schema=self.output
        )

        logger.info("[INIT] Agente criado - modelo=gemini-2.5-pro, output_schema=ListImages")

    async def result(self) -> str:
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

            return raw_result

        except Exception as e:
            logger.error("[ERROR] Falha na execucao do agente: %s", str(e))
            logger.error("[ERROR] Traceback completo:\n%s", traceback.format_exc())
            raise
