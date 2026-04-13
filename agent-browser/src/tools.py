import json
import logging
import traceback
from datetime import datetime
from browser_agent import BrowserAgent
from mcp.server.fastmcp import FastMCP
from pydantic import BaseModel

logger = logging.getLogger("mcp_tools")

mcp = FastMCP("Browser-Agent", host="0.0.0.0")

class DishImage(BaseModel):
    url_source: str

class ListImages(BaseModel):
    images: list[DishImage]


@mcp.tool()
async def search_images_by_dish(describe_task: str) -> str:
    """
    Tool chamada quando o usuario quer buscar por imagens de algum prato específico na barra de pesquisas do navegador.

    Args:
        describe_task: Descricao da tarefa de busca por imagens na barra de pesquisa, para aumentar a eficiência de busca da imagem.
    """
    request_id = datetime.now().strftime("%Y%m%d_%H%M%S_%f")
    logger.info("=" * 60)
    logger.info("[MCP TOOL] search_task chamada - ID: %s", request_id)
    logger.info("[MCP TOOL][INPUT] describe_task: %s", describe_task)

    try:
        agent = BrowserAgent(task=describe_task, output=ListImages)
        result = await agent.result()

        if result is None:
            logger.error("[MCP TOOL][ERROR] Resultado do agente foi None!")
            return json.dumps({
                "error": "Agente retornou resultado vazio",
                "request_id": request_id
            }, ensure_ascii=False)
    

        result_json = json.dumps(result, ensure_ascii=False, indent=2)
        logger.info("[MCP TOOL][OUTPUT] Retornando JSON (%d bytes)", len(result_json))
        logger.debug("[MCP TOOL][OUTPUT] Conteudo:\n%s", result_json)

        return result_json

    except Exception as e:
        error_detail = {
            "error": str(e),
            "traceback": traceback.format_exc(),
            "request_id": request_id,
            "describe_task": describe_task
        }
        logger.error("[MCP TOOL][FATAL] Erro nao tratado: %s", str(e))
        logger.error("[MCP TOOL][FATAL] Traceback:\n%s", traceback.format_exc())
        return json.dumps(error_detail, ensure_ascii=False, indent=2)
