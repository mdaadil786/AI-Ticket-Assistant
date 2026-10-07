# AI Ticket Assistant

An enterprise-style AI support assistant that combines **Multi-Agent AI, Tool Calling, RAG, order management, ticket management, and real-time streaming** into one application.

The system is designed to handle common customer-support workflows such as product questions, order queries, support tickets, and safe order creation while keeping business actions controlled by the backend.

## What This Project Does

AI Ticket Assistant allows a user to interact with an AI support assistant using a chat-style interface.

Depending on the user's request, the system can:

- Answer general support questions
- Search product and knowledge data
- Query order information
- Create and update support tickets
- Suggest products from the catalog
- Create an order confirmation before placing an order
- Create the final order only after user confirmation
- Stream the AI workflow and final response in real time

## Why I Built It

The goal of this project is to go beyond a simple chatbot.

A real support assistant often needs to understand user intent, access business data, call backend tools, and perform actions safely.

This project demonstrates how AI can be connected to real business workflows while keeping important business rules and database operations inside the backend.

## Key Features

### Multi-Agent Workflow

The assistant uses different agents for different responsibilities:

- **Intent Agent** – identifies what the user is trying to do
- **Order Agent** – handles order-related context
- **Knowledge Agent** – works with retrieved knowledge and product information

The workflow is orchestrated by the backend instead of allowing the AI model to directly control business logic.

### Tool Calling

The AI can request controlled tools such as:

- `CREATE_TICKET`
- `QUERY_ORDER`
- `UPDATE_TICKET`
- `SEARCH_KNOWLEDGE`
- `SEARCH_PRODUCTS`
- `CREATE_ORDER`

The backend validates the requested tool through a centralized `ToolDispatcher` and `ToolRegistry` before execution.

### Safe Order Confirmation

Order creation is intentionally separated into two steps.

```text
User Request
     ↓
AI understands the request
     ↓
Create Order Confirmation
     ↓
User reviews the order
     ↓
User confirms
     ↓
Real Order is created
```

The AI cannot directly create an order or modify the database.

This keeps high-impact actions under backend control.

### RAG / Knowledge Search

The project includes a knowledge retrieval layer for answering questions using stored information.

It supports:

- Knowledge document search
- Knowledge chunks
- Product catalog search
- BM25-based search
- Hybrid search architecture
- ChromaDB adapter

This allows the assistant to answer questions using business data instead of relying only on model-generated answers.

### Real-Time Streaming

The backend uses **Server-Sent Events (SSE)** to stream workflow progress to the frontend.

The UI can show:

- Workflow started
- Agent activity
- Tool calls
- Tool results
- Knowledge sources
- Order confirmation
- Final answer
- Workflow trace

This gives the user visibility into what the assistant is doing.

## Example User Requests

### Product / Knowledge Questions

```text
What products are priced at 699?
Which clothes are cheaper than 699?
How much is the black hoodie?
How many clothes can I buy with 1000?
```

### Order Related Requests

```text
I want to buy a black hoodie.
Check my order status.
Show me my order details.
```

### Support Requests

```text
My payment was successful but the order was not created.
Create a support ticket for this issue.
Update my ticket.
```

## How It Works

A typical request flows through the system like this:

```text
User
  ↓
React Frontend
  ↓
SSE / REST API
  ↓
Assistant Workflow
  ↓
Intent Agent
  ↓
Agent / Tool / RAG
  ↓
Business Logic
  ↓
Database / Knowledge Store
  ↓
SSE Events
  ↓
Frontend
```

The main design principle is:

```text
AI understands the request.
Backend controls the action.
Domain services apply business rules.
Database stores the result.
Frontend shows the workflow.
```

## Architecture

```text
                    ┌─────────────────────┐
                    │      React UI       │
                    │    TypeScript       │
                    └─────────┬───────────┘
                              │
                        REST + SSE
                              │
                              ▼
              ┌────────────────────────────┐
              │     Spring Boot Backend    │
              │                            │
              │  Assistant Workflow        │
              │  Agent Routing             │
              │  Tool Dispatcher           │
              │  Business Services         │
              └─────────────┬──────────────┘
                            │
          ┌─────────────────┼─────────────────┐
          ▼                 ▼                 ▼
     AI Providers       Knowledge/RAG      MySQL
          │                 │                 │
     Qwen / Claude      BM25 / Chroma     Orders/Tickets
          │
          ▼
      Fallback AI
```

## Tech Stack

### Backend

- Java 17
- Spring Boot 3.3.6
- Spring WebFlux
- Spring Data JPA
- Reactor
- Spring Validation
- Spring Actuator
- OpenAPI / Swagger UI
- Flyway
- MySQL 8.4

### AI & Agent Layer

- Multi-Agent architecture
- Intent Agent
- Order Agent
- Knowledge Agent
- Tool Calling
- Qwen Adapter
- Anthropic Claude Adapter
- Fallback AI Adapter

### RAG / Knowledge

- BM25 search
- ChromaDB
- Knowledge documents
- Knowledge chunks
- Hybrid search architecture

### Frontend

- React 18
- TypeScript
- Vite
- Tailwind CSS
- Zustand
- React Query
- EventSource / SSE
- lucide-react

### Testing & Deployment

- JUnit
- Reactor Test
- Testcontainers
- H2
- Vitest
- Docker
- Docker Compose
- Nginx

## Project Structure

```text
ai-ticket-assistant/
│
├── backend/
│   ├── src/main/java/com/example/aiticketassistant/
│   │   ├── interfaces/
│   │   │   ├── rest/
│   │   │   └── sse/
│   │   │
│   │   ├── application/
│   │   │   ├── agent/
│   │   │   ├── assistant/
│   │   │   ├── tool/
│   │   │   ├── order/
│   │   │   ├── query/
│   │   │   ├── assembler/
│   │   │   └── workflow/
│   │   │
│   │   ├── domain/
│   │   │   ├── agent/
│   │   │   ├── catalog/
│   │   │   ├── customer/
│   │   │   ├── knowledge/
│   │   │   ├── order/
│   │   │   └── ticket/
│   │   │
│   │   └── infrastructure/
│   │       ├── ai/
│   │       ├── knowledge/
│   │       ├── persistence/
│   │       └── repository/
│   │
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/
│
├── frontend/
│   └── src/
│       ├── app/
│       └── features/assistant/
│           ├── api/
│           ├── components/
│           ├── hooks/
│           └── stores/
│
└── docker-compose.yml
```

## Main Backend Components

### AssistantWorkflowService

The central workflow service that coordinates the assistant request and decides which part of the system should handle it.

### IntentAgent

Identifies the user's intent and can route requests such as:

- General support
- Knowledge questions
- Order queries
- Ticket creation
- Order + knowledge questions

### ToolDispatcher

Acts as a safety boundary between AI-generated tool requests and real backend operations.

It:

1. Validates the requested tool.
2. Finds the correct executor.
3. Executes the business operation.
4. Records tool activity.
5. Adds the result to the workflow context.

### ToolRegistry

Maintains the available tools and their metadata.

### KnowledgeAgent

Uses retrieved knowledge to produce answers based on available business information.

### Order Services

Order-related services handle:

- Order confirmation creation
- Confirmation approval
- Order creation
- Order status
- Order items
- Payment state

## API

### Assistant Streaming

```text
GET /assistant/stream
```

Used to stream the assistant workflow through Server-Sent Events.

Example:

```text
/assistant/stream?message=I want to buy a black hoodie&customerId=CUST-1001
```

### Products

```text
GET /products
```

### Knowledge Search

```text
GET /knowledge/search
```

### Orders

```text
GET /orders
GET /orders/{orderNo}
```

### Tickets

```text
GET /tickets
GET /tickets/{id}
```

### Order Confirmation

```text
POST /assistant/order-confirmations/{id}/confirm
POST /assistant/order-confirmations/{id}/cancel
```

### Health

```text
GET /actuator/health
```

Swagger UI is also available for API exploration.

## Database

The project uses **MySQL** with **JPA** and **Flyway**.

Flyway manages schema creation and demo data through database migrations.

Core business areas include:

- Orders
- Order items
- Products
- Product inventory
- Customer addresses
- Customer payment methods
- Order confirmations
- Tickets
- Knowledge documents
- Knowledge chunks
- Tool invocations
- Workflow traces

## Database Migration

Migration files are stored in:

```text
backend/src/main/resources/db/migration/
```

They cover:

- Core schema
- Demo data
- Safe order creation flow
- Demo order items
- Clothing catalog knowledge

## Frontend

The React application provides a visual workspace for interacting with the AI assistant.

The interface includes:

- Chat composer
- Streaming AI answer
- Agent workflow board
- Tool call cards
- RAG source panel
- Product catalog panel
- Order confirmation card
- Ticket status panel
- Workflow trace timeline

The frontend receives SSE events and updates the UI in real time.

## Security & Safety

A key design goal of the project is keeping AI actions controlled.

The AI does not directly:

- Call database repositories
- Execute Java services
- Create orders without confirmation
- Perform unrestricted business actions

Instead:

```text
AI
 ↓
Tool Call
 ↓
ToolDispatcher
 ↓
Tool Executor
 ↓
Application Service
 ↓
Repository
 ↓
Database
```

This creates a clear boundary between AI reasoning and business execution.

## AI Provider Architecture

The backend uses an abstraction called `AiClientPort`.

This allows different AI providers to be used without changing the main application logic.

```text
AiClientPort
    │
    ├── Qwen Adapter
    ├── Anthropic Claude Adapter
    └── Fallback Adapter
```

This makes the AI layer easier to replace or extend.

## Environment Configuration

The project uses environment variables for external configuration.

Example:

```env
AI_PROVIDER=qwen
QWEN_API_KEY=your-qwen-api-key
QWEN_MODEL=qwen-plus

ANTHROPIC_API_KEY=your-anthropic-api-key
ANTHROPIC_MODEL=claude-opus-4-7

VITE_API_BASE_URL=http://localhost:18080
```

Do not commit real API keys or secrets to the repository.

## Getting Started

### Prerequisites

Make sure you have:

- Java 17
- Maven or Maven Wrapper
- Node.js
- npm
- Docker
- Docker Compose

### Clone the Repository

```bash
git clone <your-repository-url>
cd ai-ticket-assistant
```

## Run with Docker

The easiest way to start the complete application is:

```bash
docker compose up -d --build
```

### Services

```text
Frontend   → http://localhost:15173
Backend    → http://localhost:18080
Swagger UI → http://localhost:18080/swagger-ui.html
MySQL      → localhost:13306
ChromaDB   → http://localhost:8000
```

### Check Services

```bash
docker compose ps
```

### View Backend Logs

```bash
docker compose logs -f backend
```

### View Frontend Logs

```bash
docker compose logs -f frontend
```

## Run Backend Locally

```bash
cd backend
./mvnw -s settings.xml test
./mvnw -s settings.xml spring-boot:run
```

Default backend port:

```text
8080
```

## Run Frontend Locally

```bash
cd frontend
npm install
npm run dev
```

Default Vite port:

```text
5173
```

## Testing

### Backend

```bash
cd backend
./mvnw -s settings.xml test
```

The backend includes tests for workflow logic, agent behavior, tools, SSE handling, and search-related functionality.

### Frontend

```bash
cd frontend
npm test
```

### Frontend Build

```bash
npm run build
```

## Example Workflow

For a request such as:

```text
I want to buy a black hoodie.
```

the flow is approximately:

```text
User Request
      ↓
Intent Detection
      ↓
Order Intent
      ↓
CREATE_ORDER Tool Call
      ↓
Tool Validation
      ↓
Create Order Confirmation
      ↓
SSE Event
      ↓
Frontend Shows Confirmation Card
      ↓
User Confirms
      ↓
Confirm Order API
      ↓
Create Order + Order Items
      ↓
Final Result
```

The system does not automatically place the order without user confirmation.

## Key Engineering Highlights

- Multi-Agent AI workflow
- Controlled Tool Calling
- Safe order confirmation flow
- RAG-based knowledge retrieval
- Real-time SSE streaming
- AI provider abstraction
- Domain-driven backend structure
- JPA-based persistence
- Flyway database migrations
- Dockerized development environment
- Backend and frontend automated tests
- Observable workflow and tool traces

## What I Learned

Building this project gave me hands-on experience with:

- Designing AI-powered backend workflows
- Multi-Agent orchestration
- Tool Calling and safety boundaries
- RAG and knowledge retrieval
- Server-Sent Events
- Spring WebFlux
- Domain-driven application structure
- JPA and relational data modeling
- AI provider abstraction
- Docker-based application setup
- Writing tests for AI workflow components

## Project Goal

The goal of AI Ticket Assistant is to build a support system that is not just conversational, but also capable of working with real business data and workflows.

The project focuses on an important principle:

> **AI should understand and assist, while the application remains responsible for business rules, security, and execution.**

## Future Improvements

- User authentication and access control
- More real-world integrations
- Better conversation memory
- Advanced RAG ranking
- More business tools
- Production observability and monitoring
- Human-agent handoff
- More detailed analytics
- Cloud deployment

## License

This project currently does not include an open-source license.
