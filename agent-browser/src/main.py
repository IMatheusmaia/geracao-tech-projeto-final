import os
import logging
import uvicorn
from tools import mcp


def setup_logging():
    log_level = os.getenv("LOG_LEVEL", "INFO").upper()

    logging.basicConfig(
        level=getattr(logging, log_level, logging.INFO),
        format="%(asctime)s | %(levelname)-8s | %(name)-20s | %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
        handlers=[
            logging.StreamHandler(),
        ]
    )

    # Silenciar logs muito verbosos de dependencias
    logging.getLogger("httpx").setLevel(logging.WARNING)
    logging.getLogger("httpcore").setLevel(logging.WARNING)
    logging.getLogger("multipart").setLevel(logging.WARNING)
    logging.getLogger("uvicorn.access").setLevel(logging.WARNING)

    logger = logging.getLogger("main")
    logger.info("=" * 60)
    logger.info("BROWSER AGENT MCP SERVER - Iniciando...")
    logger.info("=" * 60)
    logger.info("Log level: %s", log_level)
    logger.info("Host: 0.0.0.0 | Port: 8000")
    logger.info("MCP Server name: Browser-Agent")
    logger.info("=" * 60)


def main():
    setup_logging()

    logger = logging.getLogger("main")
    app = mcp.sse_app()

    logger.info("SSE app criada, iniciando servidor uvicorn...")
    uvicorn.run(app, host="0.0.0.0", port=8000, log_level="info")


if __name__ == "__main__":
    main()