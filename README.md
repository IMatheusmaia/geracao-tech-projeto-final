
# Smart Menu — Cardápio Digital com Busca de Imagens e Pratos por IA

Projeto final do curso Geração Tech 3.0. Uma aplicação de cardápio digital onde o administrador pode gerenciar, buscar pratos e buscar imagens automaticamente usando um agente de IA.

## Requisitos

- **Node.js** >= 22
- **Docker** + Docker Compose
- **Google API Key** (Gemini)

## Como rodar

### 1. Configurar variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
GOOGLE_API_KEY=sua_chave_aqui
```

### 2. Subir os serviços com Docker

```bash
docker compose up --build
```

| Serviço       | Porta | Descrição                              |
|---------------|-------|----------------------------------------|
| agent-browser | 8000  | Browser agent com MCP (Python/Chromium)|
| agent-api     | 3030  | API principal (Java/Spring Boot)       |
| redis         | 6379  | Cache                                  |
| neo4j         | 9090  | Banco de grafos                        |

### 3. Subir o front-end

Em outro terminal:

```bash
cd ui
npm install
npm run dev
```

A interface estará disponível em `http://localhost:3000`.

## Usando a aplicação

O front-end possui duas áreas principais:

- **Cardápio (cliente):** visualização dos pratos organizados por categorias, com imagens, preços, descrição e ingredientes.
- **Painel do administrador:** área protegida por login onde é possível criar, editar e excluir pratos do cardápio.

O destaque da aplicação é a **busca de imagens assistida por IA**: ao editar um prato, o administrador pode clicar em "Buscar imagem com IA" e o sistema utiliza um agente com Gemini para encontrar imagens relevantes do prato na web. As imagens encontradas são exibidas em um carrossel para o administrador selecionar a mais adequada.

## Features futuras
- O usuário administrador poderá extrair informações de outros cardápios digitais concorrentes e poderá alterar em massa os preços com base em porcentagem, por exemplo: +10% ou -10%.
- O usuário comum poderá buscar por pratos do menu apenas descrevendo ingredientes ou pedindo sugestão de comidas salgadas, alcoólicas ou doces, em um chat.

## Falhas possíveis
- Durante a busca de imagens o agent-browser pode falhar entrando em loop de busca, consumindo muitos tokens e demorando a retornar uma resposta, resultando em um erro de timeout. Atualmente, a API aguarda 15 minutos pela resposta.

### Possível solução de falhas
- Usar modelos mais especializados e poderosos para buscar por imagens, como o gemini-3.1-pro-preview.

## Licença
Projeto final do curso Geração Tech 3.0.