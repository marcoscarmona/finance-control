# Financial Control API

API local para controle de despesas, cartões, faturas, parcelas e assinaturas.

## Executar

```bash
docker compose up --build
```

O Compose inicia PostgreSQL, aplica as migrations Flyway e inicia a API.

| Serviço | URL |
| --- | --- |
| Swagger | http://localhost:8080/swagger-ui/index.html |
| OpenAPI | http://localhost:8080/v3/api-docs |
| Health | http://localhost:8080/actuator/health |

Não há autenticação neste MVP. Cadastre um usuário em `POST /api/users` e utilize o UUID retornado nos demais endpoints (`/api/users/{userId}/...`).

O banco é inicializado vazio. Para apagar apenas o volume local de desenvolvimento, execute `docker compose down -v`.
