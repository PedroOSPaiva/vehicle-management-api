# Vehicle Management API

## Descrição do Projeto

API REST robusta para gerenciamento de informações de veículos e clientes, desenvolvida como parte de desafio técnico para Analista Desenvolvimento Java PL. A aplicação implementa operações CRUD completas com autenticação JWT, validações de entrada e documentação interativa.

## Tecnologias Utilizadas

- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.6** - Framework para aplicação web
- **Spring Security** - Autenticação e autorização
- **JWT (JSON Web Token)** - Tokens de autenticação
- **Hibernate** - Mapeamento objeto-relacional
- **MySQL** - Banco de dados relacional
- **H2** - Banco em memória para testes
- **Maven** - Gerenciamento de dependências
- **JUnit 5** - Framework de testes
- **SpringDoc OpenAPI 3** - Documentação da API
- **Log4j2** - Sistema de logging
- **Caffeine** - Implementação de cache

## Requisitos do Sistema

- Java 17 ou superior
- Maven 3.6 ou superior
- MySQL 8.0 ou superior
- Git

## Instalação e Configuração

### 1. Clonar o Repositório

```bash
git clone https://github.com/PedroOSPaiva/vehicle-management-api
cd vehicle-management-api
```

### 2. Configurar Banco de Dados MySQL

```sql
-- Criar database
CREATE DATABASE vehicle_management;

-- Ou executar script completo
-- Arquivo: scripts/database/01_create_database.sql
```

### 3. Configurar Variáveis de Ambiente

Edite o arquivo `src/main/resources/application.properties`:

```properties
# Configurações do MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/vehicle_management
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# JWT Secret Key
jwt.secret=sua_chave_secreta_jwt
```

### 4. Compilar e Executar

```bash
# Compilar o projeto
mvn clean compile

# Executar a aplicação
mvn spring-boot:run
```

A aplicação estará disponível em: **http://localhost:8081**

## Estrutura do Banco de Dados

### Tabelas Principais

- **clients**: Armazena informações dos usuários/clientes
- **vehicles**: Armazena informações dos veículos
- **refresh_tokens**: Armazena tokens de refresh para JWT

### Scripts DDL

Os scripts de criação do banco estão disponíveis em:
`data.sql`

## Autenticação e Segurança

A API utiliza autenticação JWT (JSON Web Token). Para acessar endpoints protegidos:

### 1. Registrar Usuário

```bash
POST /api/auth/signup
{
    "name": "Nome do Usuário",
    "email": "usuario@email.com",
    "password": "senha123",
    "userType": "NORMAL_USER"
}
```

### 2. Fazer Login

```bash
POST /api/auth/signin
{
    "email": "usuario@email.com",
    "password": "senha123"
}
```

### 3. Usar Token JWT

Incluir no header das requisições:

```
Authorization: Bearer <seu_token_jwt>
```

## Endpoints da API

### Autenticação

- `POST /api/auth/signup` - Registrar novo usuário
- `POST /api/auth/signin` - Fazer login

### Clientes

- `GET /api/clients` - Listar todos clientes
- `GET /api/clients/{id}` - Buscar cliente por ID
- `POST /api/clients` - Criar novo cliente
- `PUT /api/clients/{id}` - Atualizar cliente
- `DELETE /api/clients/{id}` - Excluir cliente

### Veículos

- `GET /api/vehicles` - Listar todos veículos
- `GET /api/vehicles/{id}` - Buscar veículo por ID
- `POST /api/vehicles` - Criar novo veículo
- `PUT /api/vehicles/{id}` - Atualizar veículo
- `DELETE /api/vehicles/{id}` - Excluir veículo

## Documentação Interativa

A documentação da API está disponível via Swagger UI:

```
http://localhost:8081/swagger-ui.html
```

A especificação OpenAPI está disponível em:

```
http://localhost:8081/api/api-docs
```

## Execução de Testes

### Executar Todos os Testes

```bash
mvn test
```

### Executar Testes com Relatório

```bash
mvn surefire-report:report
```

### Estrutura de Testes

- 44 testes implementados
- Testes unitários e de integração
- Cobertura de controllers, services e segurança
- Ambiente de teste com H2 database

## Configurações de Desenvolvimento

### Perfil de Teste

O perfil de teste utiliza banco H2 em memória. Configuração em `src/test/resources/application-test.properties`.

### Logging

Configurado com Log4j2 para logs estruturados. Arquivo de configuração: `src/main/resources/log4j2.xml`.

### Cache

Implementado com Caffeine para melhor performance em operações de leitura frequentes.

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/vehicle_management_api/
│   │   ├── config/          # Configurações da aplicação
│   │   ├── controller/      # Endpoints REST
│   │   ├── entity/          # Entidades JPA
│   │   ├── repository/      # Repositórios Spring Data
│   │   ├── security/        # Configurações de segurança
│   │   ├── service/         # Lógica de negócio
│   │   └── validation/      # Validações customizadas
│   └── resources/           # Arquivos de configuração
├── test/                    # Testes automatizados
└── scripts/                 # Scripts de banco de dados
```

## Funcionalidades Implementadas

- CRUD completo para veículos e clientes
- Autenticação e autorização com JWT
- Validações de entrada robustas
- Documentação OpenAPI interativa
- Sistema de logging estruturado
- Cache para melhor performance
- Tratamento global de exceções
- Testes unitários e de integração
- Configuração de ambientes (dev/test/prod)

## Considerações de Produção

- Configure variáveis sensíveis via environment variables
- Utilize HTTPS em produção
- Configure connection pooling apropriado
- Monitore logs e métricas de performance
- Configure backup regular do banco de dados

## Desenvolvido por

**Pedro Henrique** - [GitHub](https://github.com/PedroOSPaiva)

## Licença

Este projeto foi desenvolvido para fins de avaliação técnica.
