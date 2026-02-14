# Prova Técnica

API de autenticação e gateway que roteia requisições de pedidos para a API Pedidos.

## Requisitos

- Java 17+
- Maven 3.8+
- MySQL 8+ (ou use Docker)
- Docker (opcional, para containerização)

## Como Executar

### Pré-requisitos

- **Java 17+** - Verificar com `java -version`
- **Maven 3.8+** - Já incluído no projeto (mvnw/mvnw.cmd)
- **Docker** - Para executar MySQL via docker-compose

### Opção 1: Execução Local (Recomendado para Desenvolvimento)

#### Passo 1: Iniciar o Banco de Dados MySQL

Na raiz do projeto, execute o docker-compose para iniciar o MySQL:

```bash
docker-compose up -d
```

> O banco `cotefacil` será criado automaticamente com as tabelas via Flyway.
> Credenciais: **root / 0831** | **Porta: 3306**

#### Passo 2: Executar as APIs (em dois terminais diferentes)

**Terminal 1 - API Gateway (porta 8080):**
```bash
cd apigateway
mvnw spring-boot:run
```

**Terminal 2 - API Pedidos (porta 8082):**
```bash
cd apipedidos
mvnw spring-boot:run
```

> **Nota para Windows**: Use `mvnw.cmd` ao invés de `./mvnw` se preferir, ou simplesmente `mvnw` que o terminal detectará automaticamente.

#### Passo 3: Verificar se as aplicações iniciaram

- **API Gateway**: http://localhost:8080/swagger-ui.html
- **API Pedidos**: http://localhost:8082/swagger-ui.html

#### Parar os Serviços

Para parar o MySQL:
```bash
docker-compose down
```

Para parar todos os containers e a rede Docker:
```bash
docker stop apigateway apipedidos
docker rm apigateway apipedidos
docker network rm cotefacil-network
```

### Opção 2: Execução com Docker (Completo)

Se preferir executar tudo em containers Docker:

#### Passo 1: Criar a rede Docker

```bash
docker network create cotefacil-network
```

#### Passo 2: Iniciar MySQL

```bash
docker-compose up -d
```

#### Passo 3: Build e execução da API Gateway

```bash
cd apigateway
docker build -t apigateway:latest .
docker run -d --name apigateway --network cotefacil-network -p 8080:8080 -e spring.datasource.url=jdbc:mysql://host.docker.internal:3306/cotefacil -e spring.datasource.username=root -e spring.datasource.password=0831 apigateway:latest
```

#### Passo 4: Build e execução da API Pedidos

```bash
cd ../apipedidos
docker build -t apipedidos:latest .
docker run -d --name apipedidos --network cotefacil-network -p 8082:8082 -e spring.datasource.url=jdbc:mysql://host.docker.internal:3306/cotefacil -e spring.datasource.username=root -e spring.datasource.password=0831 apipedidos:latest
```

> **Nota**: Use `host.docker.internal` para referenciar a máquina host do Docker.

## Endpoints

### Autenticação (públicos)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/login` | Autenticar e obter token JWT |
| POST | `/auth/register` | Registrar novo usuário e obter token |

### Gateway de Pedidos (requerem token JWT)

| Método | Endpoint | Descrição |
|--------|----------|----------|
| GET | `/api/orders` | Listar pedidos (com paginação) |
| GET | `/api/orders/{id}` | Buscar pedido por ID |
| POST | `/api/orders` | Criar pedido |
| PUT | `/api/orders/{id}` | Atualizar pedido |
| DELETE | `/api/orders/{id}` | Excluir pedido |

**Nota:** Todas as requisições para `/api/orders/**` são encaminhadas para a API Pedidos (porta 8082) com o token JWT incluído automaticamente no header `Authorization`.


## Credenciais de Teste

Após subir a aplicação, você pode registrar um novo usuário usando o endpoint `/auth/register`:

- **Username:** `usuario` (mínimo 3 caracteres)
- **Password:** `senha123` (mínimo 6 caracteres)

Alternativamente, use o endpoint `/auth/login` se já houver um usuário criado.

## Documentação Swagger

### API Gateway

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

### API Pedidos

- **Swagger UI:** http://localhost:8082/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8082/v3/api-docs

## Configurações

As seguintes variáveis de ambiente podem ser configuradas:

| Variável                | Descrição                         | Default                               |
|-------------------------|-----------------------------------|---------------------------------------|
| `JWT_SECRET`            | Chave secreta para assinatura JWT | my-secret-key                         |
| `DB_PASSWORD`           | Senha do MySQL                    | 0831                                  |
| `spring.datasource.url` | URL de conexão com MySQL          | jdbc:mysql://localhost:3306/cotefacil |

**Exemplo de configuração via variáveis de ambiente (Linux/Mac):**
```bash
export JWT_SECRET=sua-chave-secreta
export DB_PASSWORD=sua-senha
```

**Exemplo de configuração via variáveis de ambiente (Windows):**
```powershell
$env:JWT_SECRET = "sua-chave-secreta"
$env:DB_PASSWORD = "sua-senha"
```

## Token JWT

- **Validade:** 1 hora
- **Header:** `Authorization: Bearer <token>`

