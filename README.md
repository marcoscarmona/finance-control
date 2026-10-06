# Financial Control API

API local para controle de despesas, cartões, faturas, parcelas e assinaturas.

## Executar

O Docker Compose sobe apenas a infraestrutura (PostgreSQL e pgAdmin). A API roda localmente.

1. Subir o banco e a interface gráfica:

   ```bash
   docker compose up -d
   ```

2. Iniciar a API (aplica as migrations Flyway na inicialização):

   ```bash
   ./gradlew bootRun
   ```

   Por padrão a API conecta em `jdbc:postgresql://localhost:5432/financial_control`. Para usar outro banco, defina `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`.

| Serviço | URL |
| --- | --- |
| Swagger | http://localhost:8080/swagger-ui/index.html |
| OpenAPI | http://localhost:8080/v3/api-docs |
| Health | http://localhost:8080/actuator/health |
| pgAdmin | http://localhost:5050 |

## Banco de dados

| Parâmetro | Valor |
| --- | --- |
| Host | `localhost` |
| Porta | `5432` |
| Banco | `financial_control` |
| Usuário | `financial_user` |
| Senha | `financial_password` |

O pgAdmin abre sem login e já traz o servidor **Financial Control** cadastrado. Na primeira conexão, informe a senha `financial_password` (marque "Save password" para não pedir novamente).

Não há autenticação neste MVP. Cadastre um usuário em `POST /api/users` e utilize o UUID retornado nos demais endpoints (`/api/users/{userId}/...`).

O banco é inicializado vazio. Para apagar os volumes locais de desenvolvimento (banco e pgAdmin), execute `docker compose down -v`.

## Importação única da planilha

Com o PostgreSQL em execução e o usuário já criado, importe a planilha local passando o e-mail do usuário:

```powershell
.\gradlew.bat bootRun --args='--finance.import.file=C:\Users\DELL\Documents\Trash\Meu\Despesas.xlsx --finance.import.email=seu-email@exemplo.com'
```

O processo é idempotente: ele cria somente bancos, conta Nubank, cartões e categorias ausentes. Despesas com a mesma descrição, competência e valor são reportadas como duplicadas e não são inseridas novamente. As abas de investimentos e o resumo de cartões são ignorados.
