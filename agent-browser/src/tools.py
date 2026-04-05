from browser_agent import BrowserAgent
from mcp.server.fastmcp import FastMCP
from mcp.server.fastmcp import Context

mcp = FastMCP("Browser-Agent")


@mcp.tool()
async def search_task(describe_task: str) -> str:
    """Tool chamada quando o usuario quer buscar por notícias no browser agent utilizando browser use."""
    try:
        agent = BrowserAgent(task=describe_task)
        result = await agent.run()
        return result.model_dump_json()
    except Exception as e:
        return f"Erro ao executar a busca: {e}"


# @mcp.tool()
# async def explore_task(describe_task: str) -> str:
#     """Tool chamada quando o usuario nao tem um assunto especifico, quer apenas explorar
#     noticias de hoje ou nao sabe que assunto explorar."""
#     try:
#         agent = BrowserAgent(task=describe_task)
#         result = await agent.run()
#         return result.model_dump_json()
#     except Exception as e:
#         return f"Erro ao explorar: {e}"
