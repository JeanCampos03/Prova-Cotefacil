# API 2 - CRUD de Pedidos

## Exemplos de Requisição

### Registrar usuário

```bash
curl -X POST http://localhost:8082/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario",
    "password": "senha123",
    "role": "USER"
  }'
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
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario",
    "password": "senha123"
  }'
```

### Criar pedido

```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Maria Silva",
    "customerEmail": "maria.silva@email.com",
    "items": [
      {
        "productName": "Dipirona",
        "quantity": 4,
        "unitPrice": 4.89
      },
      {
        "productName": "Paracetamol",
        "quantity": 2,
        "unitPrice": 3.50
      }
    ]
  }'
```

### Listar pedidos (com paginação)

```bash
curl -X GET "http://localhost:8082/api/orders?page=0&size=10" \
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

### Buscar pedido por ID

```bash
curl -X GET http://localhost:8082/api/orders/1 \
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

### Atualizar pedido

```bash
curl -X PUT http://localhost:8082/api/orders/1 \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "PROCESSING",
    "items": [
      {
        "itemId": 1,
        "productName": "Dipirona",
        "quantity": 5,
        "unitPrice": 4.89
      }
    ]
  }'
```

### Excluir pedido (exclusão lógica)

```bash
curl -X DELETE http://localhost:8082/api/orders/1 \
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

### Listar itens de um pedido

```bash
curl -X GET http://localhost:8082/api/orders/1/items \
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

### Adicionar itens a um pedido

```bash
curl -X POST http://localhost:8082/api/orders/1/items \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "productName": "Ibuprofeno",
      "quantity": 3,
      "unitPrice": 5.99
    }
  ]'
```

Os status possíveis são:

- `PENDING` - Pendente
- `PROCESSING` - Em processamento
- `SHIPPED` - Enviado
- `DELIVERED` - Entregue
- `CANCELLED` - Cancelado

**Regras de negócio:**
- Pedidos com status `DELIVERED` ou `CANCELLED` não podem ser alterados
- Pedidos com status `DELIVERED` não podem ser excluídos
- Status não pode retroceder (ex: de `SHIPPED` para `PROCESSING`)

## Arquitetura

```
api2/
├── config/          # Configurações (Security, Swagger, Seeder)
├── controllers/     # Controllers REST (AuthController, OrderController, OrderItemController)
├── dtos/            # DTOs de request/response
├── entitys/         # Entidades JPA (Order, OrderItem, Usuario)
├── exceptions/      # Tratamento global de exceções
├── repositorys/    # Repositórios JPA
├── security/        # JWT, Filtro de autenticação
└── services/       # Lógica de negócio
```

## Validações

- **OrderDTO**: 
  - `customerName`: obrigatório, não vazio
  - `customerEmail`: obrigatório, formato de email válido
  - `items`: obrigatório, lista não vazia

- **OrderItemDTO**:
  - `productName`: obrigatório, entre 3 e 100 caracteres
  - `quantity`: obrigatório, maior que zero
  - `unitPrice`: obrigatório, maior que zero, até 2 casas decimais

- **AuthDTO**:
  - `username`: obrigatório, entre 3 e 50 caracteres
  - `password`: obrigatório, mínimo 6 caracteres

## Tratamento de Erros

A API retorna erros padronizados no formato:

```json
{
  "status": 400,
  "mensagem": "Mensagem de erro",
  "date": "12-02-2026 10:30:00"
}
```

Códigos HTTP:
- `200` - Sucesso
- `201` - Criado com sucesso
- `400` - Requisição inválida
- `401` - Não autenticado
- `404` - Recurso não encontrado
- `409` - Conflito (regra de negócio violada)

## Testes
Para executar os testes:

```bash
./mvnw test
```

## Desenvolvimento

### Estrutura de Pacotes

- `br.com.prova.cotefacil.api2.controllers` - Controllers REST
- `br.com.prova.cotefacil.api2.services` - Lógica de negócio
- `br.com.prova.cotefacil.api2.repositorys` - Acesso a dados
- `br.com.prova.cotefacil.api2.entitys` - Entidades JPA
- `br.com.prova.cotefacil.api2.dtos` - Data Transfer Objects
- `br.com.prova.cotefacil.api2.config` - Configurações
- `br.com.prova.cotefacil.api2.exceptions` - Exceções customizadas
- `br.com.prova.cotefacil.api2.security` - Segurança e JWT

## Licença

Este projeto foi desenvolvido para a Prova Técnica CoteFácil 2026.
