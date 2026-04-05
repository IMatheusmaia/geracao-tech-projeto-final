from browser_use import Agent, Browser, ChatGoogle
from dotenv import load_dotenv
from os import getenv
from pydantic import BaseModel

class NewsItem(BaseModel):
    title: str
    content: str
    pub_date: str
    name_source: str | None
    url_source: str

class NewsResult(BaseModel):
    items: list[NewsItem]
    url_source: str

class BrowserAgent:

    def __init__(self, task: str):
        load_dotenv()

        self.api_key = getenv("GOOGLE_GEMINI_API_KEY")
        self.browser = Browser(
            headless=True,
            window_size={'width': 1024, 'height': 768},
            no_viewport=True,
            executable_path=getenv("CHROMIUM_BIN_PATH"),
            keep_alive=True,
            wait_for_network_idle_page_load_time=1.5,
            disable_security=True
        )
        self.agent = Agent(
            task=task,
            browser=self.browser,
            chat=ChatGoogle(
                model="gemini-2.5-flash",
                temperature=0.6,
                supports_structured_output=True,
                api_key=self.api_key
            ),
            output_model_schema=NewsResult
        )

    async def run(self) -> NewsResult:
        result = await self.agent.run()
        return result.get_structured_output(NewsResult)
