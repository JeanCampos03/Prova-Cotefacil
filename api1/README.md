# API 1 - Gateway/Autenticação (Prova Técnica CoteFácil 2026)

API de autenticação e gateway que roteia requisições de pedidos para a API 2.

## Requisitos

- Java 17+
- Maven 3.8+
- MySQL 8+ (ou use Docker)
- API 2 rodando (para funcionalidade de pedidos)

## Como Executar

### Execução Local

1. **Configure o MySQL** - Crie o banco `cotefacil` ou deixe a aplicação criar automaticamente:
   ```bash
   mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS cotefacil;"
   ```

2. **Configure o `application.properties`** (ou variáveis de ambiente):
   - `spring.datasource.username` - usuário MySQL (default: root)
   - `spring.datasource.password` - senha MySQL
   - `api2.url` - URL da API 2 (default: http://localhost:8082)

3. **Execute a aplicação**:
   ```bash
   ./mvnw spring-boot:run
   # Ou no Windows:
   mvnw.cmd spring-boot:run
   ```

### Execução com Docker

```bash
docker build -t api1-gateway .
docker run -p 8080:8080 \
  -e spring.datasource.url=jdbc:mysql://host.docker.internal:3306/cotefacil \
  -e spring.datasource.username=root \
  -e spring.datasource.password=sua_senha \
  -e api2.url=http://host.docker.internal:8082 \
  api1-gateway
```

## Endpoints

### Autenticação (públicos)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/login` | Autenticar e obter token JWT |
| POST | `/auth/register` | Registrar novo usuário e obter token |

### Gateway de Pedidos (exigem token JWT)

| Método | Endpoint | Descrição |
|--------|----------|----------|
| GET | `/api/orders` | Listar pedidos (com paginação) |
| GET | `/api/orders/{id}` | Buscar pedido por ID |
| POST | `/api/orders` | Criar pedido |
| PUT | `/api/orders/{id}` | Atualizar pedido |
| DELETE | `/api/orders/{id}` | Excluir pedido |
| * | `/api/orders/**` | Encaminha para API 2 |

**Nota:** Requisições para `/api/orders/**` são encaminhadas para a API 2 com o token JWT incluído no header `Authorization`.

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

## Credenciais de Teste

Após subir a aplicação, registre um usuário com:

- **Username:** `usuario` (mínimo 3 caracteres)
- **Password:** `senha123` (mínimo 6 caracteres)

Ou use o endpoint `/auth/register` para criar.

## Documentação Swagger

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

## Configurações

| Variável | Descrição | Default |
|----------|-----------|---------|
| `JWT_SECRET` | Chave secreta para assinatura JWT | my-secret-key |
| `API2_URL` | URL base da API 2 | http://localhost:8082 |
| `spring.datasource.url` | URL do MySQL | jdbc:mysql://localhost:3306/cotefacil |

## Token JWT

- **Validade:** 1 hora
- **Header:** `Authorization: Bearer <token>`

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
