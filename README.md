# 💰 Financial Control — Controle Financeiro Pessoal

Sistema de controle financeiro pessoal com monorepo, backend Spring Boot + Kotlin e frontend Next.js.

## Pré-requisitos

- Java 21
- Docker e Docker Compose
- Node.js 18+
- npm

## 🚀 Como rodar

### 1. Subir o banco de dados

```bash
docker compose up -d
```

O PostgreSQL ficará disponível em `localhost:5432`.  
As migrations do Flyway criam automaticamente as tabelas e inserem dados demo ao subir o backend.

### 2. Rodar o backend

**Linux/macOS:**
```bash
cd backend
./gradlew bootRun
```

**Windows:**
```bash
cd backend
gradlew.bat bootRun
```

O backend estará disponível em: http://localhost:8080

### 3. Rodar o frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend estará disponível em: http://localhost:3000

---

## 🔗 URLs

| Serviço   | URL                                  |
|-----------|--------------------------------------|
| Frontend  | http://localhost:3000                |
| Backend   | http://localhost:8080                |
| Dashboard API | http://localhost:8080/api/users/{userId}/dashboard?yearMonth=2026-04 |
| Demo user | http://localhost:8080/api/users/demo |

---

## 🗂️ Estrutura do projeto

```
/
├── docker-compose.yml
├── README.md
├── backend/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── src/main/kotlin/br/com/financialcontrol/
│       ├── FinancialControlApplication.kt
│       ├── domain/
│       │   ├── entities/
│       │   └── enums/
│       ├── application/
│       │   ├── dto/
│       │   ├── ports/out/
│       │   └── usecases/
│       ├── infrastructure/
│       │   ├── adapters/
│       │   ├── config/
│       │   └── persistence/
│       └── interfaces/web/controllers/
└── frontend/
    ├── package.json
    ├── next.config.js
    └── src/
        ├── app/          (páginas Next.js)
        ├── components/   (componentes reutilizáveis)
        ├── lib/          (api, utils)
        └── types/        (TypeScript types)
```

---

## 🌱 Dados de Demo

O banco é populado automaticamente com:

- **Usuário:** Marcos Carmona (marcos.demo@email.com)
- **Bancos:** C6 Bank, Nubank
- **Cartões:** C6 Carbon, Nubank Ultravioleta
- **Categorias padrão:** Alimentação, Mercado, Transporte, Saúde, Assinaturas, Compras, Lazer, Educação, Moradia, Outros
- **30+ transações** de Abril/2026 prontas para o dashboard
- **Orçamentos** por categoria para comparação

---

## 📥 Formato do CSV para importação

```csv
date,bank,description,category,amount,paymentMethod
2026-04-15,Nubank,Supermercado,Mercado,-350.00,DEBIT_CARD
2026-04-16,C6 Bank,Uber,Transporte,-45.00,CREDIT_CARD
2026-04-20,Nubank,Salário,Salário,8000.00,BANK_TRANSFER
```

---

## 🛠 Tecnologias

**Backend:**
- Kotlin + Java 21
- Spring Boot 3.2
- Spring Data JPA + Hibernate
- Flyway
- PostgreSQL

**Frontend:**
- Next.js 14
- TypeScript
- Tailwind CSS
- Recharts

**Infraestrutura:**
- Docker Compose
- PostgreSQL 16
