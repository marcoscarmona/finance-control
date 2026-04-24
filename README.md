# Finance Control — Kotlin + Spring Boot

Projeto base para controle financeiro pessoal com importação de lançamentos, categorização, resumo mensal e dashboard.

## Stack

- Kotlin
- Spring Boot
- PostgreSQL
- Flyway
- Docker Compose
- Gradle Kotlin DSL

## Como rodar

```bash
docker compose up -d
./gradlew bootRun
```

API:

```text
http://localhost:8080
```

## Endpoints principais

```text
POST /api/transactions
GET  /api/transactions?month=2026-04
GET  /api/dashboard?month=2026-04
POST /api/import/csv
GET  /api/categories
POST /api/categories
POST /api/rules
GET  /api/rules
```

## CSV esperado

```csv
date,bank,description,amount,type
2026-04-01,C6,IFOOD RESTAURANTE,89.90,DEBIT
2026-04-02,NUBANK,PAGAMENTO FATURA,1000.00,CREDIT
```

## Próximos passos recomendados

1. Criar frontend Next.js.
2. Adicionar autenticação.
3. Melhorar importação OFX.
4. Criar regras editáveis pela UI.
5. Criar orçamento mensal por categoria.
6. Exportar dashboard para Excel.
