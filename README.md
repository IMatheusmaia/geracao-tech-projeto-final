# Searcher — Agregador de Notícias com IA Multiagente

> **Projeto em construcao**

Sistema de busca e sintese jornalistica de noticias que utiliza multiplos agentes de IA para automatizar web scraping, agregacao de conteudo e geracao de resumos com atribuicao de fontes.

## Visao Geral

O Searcher e uma plataforma composta por tres servicos que se comunicam via protocolo **MCP (Model Context Protocol)** com transporte **SSE (Server-Sent Events)**, integrando um backend Java (Spring AI), um agente de navegador em Python (browser-use) e uma interface React:

## Status do Projeto

### O que funciona

- API REST para comunicacao entre front-end e modelo de IA
- O modelo de IA decide corretamente se chama ou nao uma ferramenta (Tool Calling)
- Comunicacao MCP entre cliente Java (Spring AI) e servidor Python via SSE — retorna dados com baixa carga (1 fonte de pesquisa, pesquisas simples)
- CRUD de fontes de pesquisa persistidas no Redis

### Parcialmente funcional

- **Memoria de chat**: o modelo armazena as mensagens do usuario no Neo4j, mas nao ha feedback visual no front-end para o usuario acompanhar o historico
- **Resumo de noticias**: funciona para o dia atual com baixa carga de trabalho, com tempo limite de 300 segundos

### Gargalos enfrentados

- Alta complexidade arquitetural (multi-linguagem, multi-agente, multi-banco)
- Escopo ambicioso para o tempo disponivel
- **Consumo elevado de tokens**: ~700k tokens consumidos em apenas 3 pesquisas devido ao web scraping

### Meritos do projeto

- Integracao entre sistemas de linguagens diferentes (Java <-> Python) usando protocolo **MCP SSE**
- **Encadeamento multiagentic** com tres agentes especializados: orquestrador, scraper e sintetizador
- Prompts complexos e bem estruturados para contextos jornalisticos
- Experiencia adquirida em padroes de desenvolvimento de nivel enterprise (microsservicos, repositorios, DTOs, gerenciamento de estado)

## Como Executar

### Pre-requisitos

- Docker e Docker Compose
- Google API Key (Gemini)

### Inserir variáveis de ambiente
No arquivo `docker-compose.yml` substitua ${GOOGLE_API_KEY} por sua chave de API do Google Gemini

### Buildar e Subir os Containers

```bash
docker compose up --build
```

Isso inicia Redis, Neo4j e o servico `agent-browser` `ui` e `searcher`.


### Passo a Passo de como ver o resultado positivo gerado

- Crie uma fonte de pesquisa ou no máximo 3
- Digite no input de texto algo como "Quais as notícias de hoje?"
- Você pode optar por escolher um perfil de pesquisa ou não
- Ao final do processamento da barra lateral esqueda vai renderizar o card com um resumo das notícias

**Obs.: é possível repetir o processo quantas vezes desejar, desde que seja uma carga pequena de trabalho, pois o processo é demorado e consome muitos tokens**

---


## Arquitetura

### Stack Tecnologica

| Camada | Tecnologia | Versao |
|--------|-----------|--------|
| Backend | Java + Spring Boot | 17 / 3.5.13 |
| IA (Backend) | Spring AI + Google Gemini | 1.1.4 / gemini-2.5-pro |
| Agente Browser | Python + FastMCP + browser-use | - |
| Frontend | React + TypeScript + Vite | 19 / 5.9 / 8 |
| Estado (Frontend) | Zustand + TanStack Query | 5 / 5.96 |
| Cache / NoSQL | Redis (RedisStack) | - |
| Memoria de Chat | Neo4j | 5.26 |
| UI | Tailwind CSS + Radix UI | 4.2 / 1.4 |
| Protocolo IA | MCP (Model Context Protocol) via SSE | - |

### Servicos e Portas

| Servico | Porta | Descricao |
|---------|-------|-----------|
| `searcher` | 3030 | API REST (Spring Boot) |
| `agent-browser` | 8000 | MCP Server - Web scraping (Python) |
| `ui` | 5173 | Interface grafica (Vite dev server) |
| `redis` | 6379 | Banco NoSQL para fontes e conteudos |
| `redis-insight` | 8001 | Interface web para inspecionar Redis |
| `neo4j` | 9090 (bolt) / 7474 (http) | Banco de grafos para memoria de chat |

### Agentes de IA

O sistema utiliza **tres agentes** com responsabilidades distintas:

1. **Agente Orquestrador** (Java/Gemini) — Recebe a mensagem do usuario e decide o fluxo de chamadas: busca no banco de dados existente (`redirectToDB`) ou aciona o agente de scraping via MCP (`redirectToMCPClient`).

2. **Agente de Web Scraping** (Python/browser-use + Gemini) — Automatiza o navegador Chrome para navegar ate fontes de noticia, extrair manchetes e conteudo bruto das paginas.

3. **Agente de Sintese** (Java/Gemini) — Recebe os dados brutos coletados pelo agente de scraping e gera uma materia jornalistica consolidada com atribuicao de fontes e formatacao Markdown.

### Fluxo de Dados

```
Usuario envia mensagem
        │
        ▼
[Agente Orquestrador] analisa intenção
        │
        ├──► Conteúdo já existe no Redis? ──► Retorna dados cached
        │
        └──► Precisa buscar? ──► [MCP/SSE] ──► [Agente Scraper (Python)]
        │
        ▼
Dados brutos coletados
        │
        ▼
[Agente Síntese] ──► Matéria consolidada
                              │
                              ▼
                    Persiste no Redis
                    Armazena memória no Neo4j
                              │
                              ▼
                      Retorna para o usuário
```

## Estrutura do Projeto

```
geracao-tech-projeto-final/
├── searcher/                    # Backend Java (Spring Boot + Spring AI)
│   ├── pom.xml
│   └── src/main/java/com/agent/searcher/
│       ├── mcp/                 # Configuração MCP Client + DTOs
│       ├── model/
│       │   ├── client/          # ChatClientService, ResumerClientService
│       │   ├── config/          # Configuração do modelo Gemini
│       │   ├── memory/          # Memória de chat (Neo4j)
│       │   ├── prompts/         # Prompts dos agentes
│       │   ├── schema/          # OutputSchema (resposta da API)
│       │   └── tools/           # Ferramentas do modelo (DB, MCP)
│       ├── rest/
│       │   ├── controller/      # Endpoints REST
│       │   ├── entity/          # Entidades Redis (Source, Content)
│       │   ├── repository/      # Repositórios Redis
│       │   ├── service/         # Lógica de negócio
│       │   └── dto/             # Request/Response DTOs
│       └── SearcherApplication.java
│
├── agent-browser/               # MCP Server Python (web scraping)
│   ├── requirements.txt
│   ├── .Dockerfile
│   ├── .env
│   └── src/
│       ├── main.py              # Entry point do servidor MCP
│       ├── browser_agent.py     # Agente de automação browser
│       └── tools.py             # Ferramenta MCP: search_task
│
├── ui/                          # Frontend React + TypeScript
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── api/                 # Cliente HTTP (searcher.ts)
│       ├── components/
│       │   ├── chat/            # ChatInput, MessageBubble, MessageList
│       │   ├── layout/          # AppLayout
│       │   ├── modals/          # ContentModal, SourcesModal
│       │   ├── sidebar/         # ConversationList
│       │   └── ui/              # Componentes base (shadcn/ui)
│       ├── hooks/               # useSearch, useContents, useSources
│       ├── stores/              # Zustand stores (chat, sources)
│       └── types/               # Definições de tipos TypeScript
│
└── docker-compose.yml           # Redis, Neo4j, Agent Browser
```

## API REST

### Endpoints

| Metodo | Rota | Descricao |
|--------|------|-----------|
| `POST` | `/api/v1/search` | Busca principal (interacao com IA) |
| `GET` | `/api/v1/contents` | Lista conteudos salvos |
| `GET` | `/api/v1/contents/{id}` | Busca conteudo por ID |
| `DELETE` | `/api/v1/contents/{id}` | Remove conteudo |
| `POST` | `/api/v1/sources` | Adiciona fonte de pesquisa |
| `GET` | `/api/v1/sources` | Lista fontes cadastradas |
| `DELETE` | `/api/v1/sources/{id}` | Remove fonte |

--- 

## Licenca
Projeto final do curso Geracao Tech 3.0.
