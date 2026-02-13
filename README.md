# Prova Técnica

API de autenticação e gateway que roteia requisições de pedidos para a pedidos.

## Requisitos

- Java 17+
- Maven 3.8+
- MySQL 8+ (ou use Docker)
- API Pedidos rodando (para funcionalidade de pedidos)

## Como Executar

### Execução Local

1. **Configure o MySQL** - Crie o banco `cotefacil`
   ```bash
   mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS cotefacil;"
   ```

2. **Configure o `application.properties`** com credenciais locais do seu banco:
   - `spring.datasource.username` - root
   - `spring.datasource.password` - 1234
   - `api2.url` - URL da API Pedidos (default: http://localhost:8082)

3. **Execute a aplicação** (Em dois terminais diferentes, dentro do projeto):
   ```bash
   Rodar API-Gateway
    A partir da pasta base:
   cd api1
    ./mvnw spring-boot:run


   Rodar API-Pedidos
   A partir da pasta base:
    cd api2
    ./mvnw spring-boot:run
   ```

### Execução com Docker

```bash
A partir da pasta base:
cd api1

docker build -t api1-gateway .

docker network create cotefacil-network

docker run -d --name api1-gateway --network cotefacil-network -p 8080:8080 -e spring.datasource.url=jdbc:mysql://host.docker.internal:3306/cotefacil -e spring.datasource.username=root -e spring.datasource.password=1234 api1-gateway

A partir da pasta base:
cd api2

docker build -t api2-pedidos .

docker run -d --name api2-pedidos --network cotefacil-network -p 8082:8082 -e spring.datasource.url=jdbc:mysql://host.docker.internal:3306/cotefacil -e spring.datasource.username=root -e spring.datasource.password=1234 api2-pedidos
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
| * | `/api/orders/**` | Encaminha para API Pedidos |

**Nota:** Requisições para `/api/orders/**` são encaminhadas para a API Pedidos com o token JWT incluído no header `Authorization`.


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
| `API2_URL` | URL base da API Pedidos | http://localhost:8082 |
| `spring.datasource.url` | URL do MySQL | jdbc:mysql://localhost:3306/cotefacil |

## Token JWT

- **Validade:** 1 hora
- **Header:** `Authorization: Bearer <token>`

