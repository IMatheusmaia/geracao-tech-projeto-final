import os
import uvicorn
from tools import mcp

AUTH_TOKEN = os.environ.get("MCP_AUTH_TOKEN", "")


class BearerAuthMiddleware:
    """ASGI middleware that validates Bearer token on every HTTP request."""

    def __init__(self, app):
        self.app = app

    async def __call__(self, scope, receive, send):
        if scope["type"] == "http":
            headers = dict(scope.get("headers", []))
            auth = headers.get(b"authorization", b"").decode()
            if not auth.startswith("Bearer ") or auth[7:] != AUTH_TOKEN:
                await send(
                    {
                        "type": "http.response.start",
                        "status": 401,
                        "headers": [[b"content-type", b"application/json"]],
                    }
                )
                await send(
                    {
                        "type": "http.response.body",
                        "body": b'{"error":"Unauthorized"}',
                    }
                )
                return
        await self.app(scope, receive, send)


def main():
    app = mcp.sse_app()
    app = BearerAuthMiddleware(app)
    uvicorn.run(app, host="0.0.0.0", port=8000)


if __name__ == "__main__":
    main()
