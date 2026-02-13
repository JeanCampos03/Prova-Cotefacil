# API 1 - Gateway/Autenticação

## Exemplos de Requisição

### Registrar usuário

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"usuario","password":"senha123"}'
```

**Resposta:**
```json
{
  "status": 201,
  "mensagem": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "date": "12-02-2026 10:30:00"
}
```

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"usuario","password":"senha123"}'
```

### Acessar pedidos (com token)

```bash
curl -X GET "http://localhost:8080/api/orders?page=0&size=10" \
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

## Arquitetura

```
api1/
├── config/          # Configurações (Security, RestTemplate)
├── controller/      # AuthController, OrdersGatewayController
├── dto/             # DTOs de request/response
├── entity/          # Entidades JPA
├── exception/       # Tratamento global de exceções
├── repository/      # Repositórios JPA
├── security/        # JWT, Filtro de autenticação
└── service/         # Lógica de negócio
```
