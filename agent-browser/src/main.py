from browser_use import Agent, Browser, ChatGoogle
import asyncio

async def main():
    await asyncio.sleep(1)
    print("Hello, World!")

if __name__ == "__main__":
    asyncio.run(main())