# AI Ticket Assistant

An enterprise-style AI support assistant that combines **Multi-Agent AI, Tool Calling, RAG, order management, ticket management, and real-time streaming** in one application.

The system can answer support questions, search trusted knowledge, query orders, create support tickets, suggest products, and prepare orders for confirmation before any business action is completed.

## Features

- Multi-Agent workflow with Intent, Order, and Knowledge Agents
- Controlled Tool Calling through `ToolDispatcher` and `ToolRegistry`
- RAG-based knowledge and product search
- Safe order confirmation before order creation
- Order and support-ticket management
- Real-time workflow streaming with Server-Sent Events (SSE)
- Single-request and workflow-based assistance
- AI provider abstraction with Qwen, Claude, and local fallback support
- MySQL persistence with JPA and Flyway
- React + TypeScript frontend
- Docker Compose setup and automated tests

## Tech Stack

**Backend**
- Java 17
- Spring Boot
- Spring WebFlux
- Spring Data JPA
- MySQL
- Flyway

**AI / RAG**
- Qwen
- Anthropic Claude
- Sentence / knowledge retrieval workflow
- BM25
- ChromaDB adapter

**Frontend**
- React 18
- TypeScript
- Vite
- Tailwind CSS
- Zustand
- React Query

**Testing & Deployment**
- JUnit
- Vitest
- Testcontainers
- Docker
- Docker Compose
- Nginx

## How It Works

```text
User
  │
  ▼
React Frontend
  │
  ├── REST
  └── SSE
  │
  ▼
Spring Boot Backend
  │
  ▼
Intent Agent
  │
  ├── Tool Calling ──► Orders / Tickets / Products
  │
  └── RAG ───────────► Knowledge Base
  │
  ▼
Business Services
  │
  ▼
MySQL
```

The main design principle is simple: **AI understands the request, while the backend controls business actions.**

## Safe Order Flow

Order creation is intentionally split into two steps.

```text
User asks to buy a product
          ↓
AI identifies purchase intent
          ↓
Create pending confirmation
          ↓
User reviews product / price / address / payment method
          ↓
User confirms
          ↓
Backend creates the real order
```

The AI never writes directly to the database and does not create an order without confirmation.

## Project Structure

```text
ai-ticket-assistant/
├── backend/
│   ├── src/main/java/com/example/aiticketassistant/
│   │   ├── application/
│   │   ├── domain/
│   │   ├── infrastructure/
│   │   └── interfaces/
│   └── src/main/resources/db/migration/
│
├── frontend/
│   └── src/
│       ├── app/
│       └── features/assistant/
│
└── docker-compose.yml
```

## API

```text
GET  /assistant/stream
GET  /products
GET  /knowledge/search
GET  /orders
GET  /orders/{orderNo}
GET  /tickets
GET  /tickets/{id}
POST /assistant/order-confirmations/{id}/confirm
POST /assistant/order-confirmations/{id}/cancel
GET  /actuator/health
```

Swagger UI is also available in the backend application.

## Getting Started

### Prerequisites

- Java 17
- Node.js and npm
- MySQL
- Docker and Docker Compose
- An AI API key for Qwen or Anthropic (optional; local fallback is available)

### Clone

```bash
git clone <your-repository-url>
cd ai-ticket-assistant
```

### Run with Docker

```bash
docker compose up -d --build
```

### Run Backend Locally

```bash
cd backend
./mvnw -s settings.xml test
./mvnw -s settings.xml spring-boot:run
```

### Run Frontend Locally

```bash
cd frontend
npm install
npm run dev
```

## Testing

Backend:

```bash
cd backend
./mvnw -s settings.xml test
```

Frontend:

```bash
cd frontend
npm test
```

## Security & Reliability

- AI actions go through a controlled tool layer.
- Business rules stay in application/domain services.
- Environment variables are used for external API keys and configuration.
- Database changes are managed with Flyway migrations.
- Order creation requires explicit user confirmation.

## Project Goal

The goal of AI Ticket Assistant is to build a practical support system that is **conversational, tool-aware, data-aware, and safe for real business workflows**.

## License

This project currently does not include an open-source license.
