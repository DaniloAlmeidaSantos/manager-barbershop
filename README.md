# Manager Barbershop API

API REST para gerenciamento de agendamentos de barbearia. Permite que clientes agendem, remarquem e cancelem serviços, enquanto barbeiros visualizam e gerenciam todos os agendamentos.

## Tecnologias

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Security** (JWT stateless)
- **Spring Data JPA** + **MySQL**
- **Argon2** (hashing de senhas via BouncyCastle)
- **Auth0 java-jwt 4.4.0**
- **Resilience4j** (rate limiting via AOP)
- **Lombok**
- **Maven**

---

## Pré-requisitos

- JDK 17+
- MySQL 8+
- Maven 3.8+

---

## Configuração

### Variáveis de ambiente

| Variável | Descrição | Default (dev only) |
|---|---|---|
| `DATABASE_URL` | JDBC URL do banco MySQL | `jdbc:mysql://localhost:3306/barber` |
| `DATABASE_USER` | Usuário do banco | `root` |
| `DATABASE_PASSWORD` | Senha do banco | *(vazio)* |
| `JWT_SECRET` | Segredo HMAC256 para assinar tokens JWT | *(hardcoded — **não usar em produção**)* |
| `JWT_EXPIRATION_TIME` | Tempo de expiração do token em minutos | `100` |

> **Produção:** Sempre defina `JWT_SECRET` com um valor forte via variável de ambiente. Nunca utilize o valor padrão.

### Banco de dados

O Hibernate gerencia o schema automaticamente (`ddl-auto: update`). Basta criar o banco e configurar as variáveis:

```sql
CREATE DATABASE barber CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> **Importante:** As senhas dos usuários de sistema (`TB_SYSTEM_USERS`) devem ser armazenadas como **hashes Argon2**. O encoder padrão é `Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()`.

---

## Executando o projeto

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080/manager-barbershop`.

---

## Autenticação

A API usa **JWT Bearer Token** em todas as rotas protegidas.

### Obter token

```
POST /manager-barbershop/auth/login
```

```json
{
  "username": "seu_usuario",
  "password": "sua_senha"
}
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expirationTime": 100
}
```

Use o token no header de todas as requisições protegidas:
```
Authorization: Bearer <token>
```

---

## Endpoints

### Autenticação

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| `POST` | `/auth/login` | Não | Gera JWT para usuário de sistema |

### Clientes

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| `POST` | `/v1/customer` | Não | Cadastra novo cliente |
| `POST` | `/v1/customer/authenticate` | Não | Autentica cliente (retorna dados do perfil) |
| `POST` | `/v1/customer/schedule/service` | Sim | Agenda um serviço |
| `PUT` | `/v1/customer/schedule/service/{id}` | Sim | Remarca um agendamento |
| `DELETE` | `/v1/customer/schedule/service/{id}` | Sim | Cancela um agendamento |
| `GET` | `/v1/customer/available-hours/{barberId}` | Sim | Consulta horários disponíveis do barbeiro |

### Barbeiros

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| `POST` | `/v1/barber/find` | Sim | Busca barbeiros por nome, localização ou serviço (paginado) |
| `POST` | `/v1/barber/config` | Sim | Cria configuração da barbearia |

### Gestão pelo Barbeiro

| Método | Rota | Auth | Role | Descrição |
|---|---|---|---|---|
| `POST` | `/v1/barber/management/barbers?requestingBarberId={adminId}` | Sim | ADMIN | Cadastra novo barbeiro (EMPLOYEE). Na primeira chamada com tabela vazia, `requestingBarberId` deve ser omitido — o barbeiro é promovido a ADMIN automaticamente. |
| `GET` | `/v1/barber/management/appointments?barberId={id}` | Sim | Qualquer | Lista agendamentos ativos/pendentes do barbeiro |
| `GET` | `/v1/barber/management/appointments/history?barberId={id}` | Sim | Qualquer | Lista histórico (cancelados e finalizados) |
| `POST` | `/v1/barber/management/appointments` | Sim | Qualquer | Agenda serviço para qualquer cliente |
| `PUT` | `/v1/barber/management/appointments/{id}` | Sim | Qualquer | Remarca qualquer agendamento |
| `DELETE` | `/v1/barber/management/appointments/{id}` | Sim | Qualquer | Cancela qualquer agendamento |

---

## Exemplos de Payload

### Cadastrar cliente
```json
POST /v1/customer
{
  "name": "João Silva",
  "email": "joao@email.com",
  "phone": "11999999999",
  "secret": "senha123"
}
```

### Agendar serviço
```json
POST /v1/customer/schedule/service
{
  "customer": 1,
  "barber": 2,
  "startTime": "2026-03-20T09:00:00",
  "finishTime": "2026-03-20T10:00:00",
  "services": [1, 2]
}
```

### Buscar barbeiros
```json
POST /v1/barber/find
{
  "barberName": "Carlos",
  "locationName": null,
  "serviceName": null,
  "page": 0,
  "size": 10
}
```

---

## Arquitetura

```
controller          → Recebe requisições HTTP, delega para services
service (interface) → Contrato de negócio (DIP)
service/impl        → Lógica de negócio
repository          → Acesso ao banco via Spring Data JPA
domain              → Entities, DTOs, Enums
infra/security      → JWT filter + helper
infra/exceptions    → Exceções customizadas + handlers globais
app/aspect          → Rate limiting via AOP (@RateLimitProtection)
```

### Princípios SOLID aplicados

- **SRP** — Cada service tem uma única responsabilidade (ex: `BarbershopConfigService` separado de `BarberService`).
- **OCP** — Novos tipos de configuração e serviços são adicionados via novos registros no banco, sem alterar código.
- **LSP** — Implementações substituem suas interfaces sem quebrar contratos.
- **ISP** — `BarberService` (consulta) e `BarbershopConfigService` (configuração) são interfaces independentes.
- **DIP** — Controllers e services dependem de abstrações (interfaces), não de implementações concretas.

---

## Rate Limiting

Endpoints sensíveis são protegidos por rate limit via AOP (`@RateLimitProtection`):

- `POST /auth/login`
- `POST /v1/barber/find`
- `GET /v1/customer/available-hours/{id}`

Configuração padrão: **20 requisições a cada 10 segundos** (ajustável em `application.yml`).

---

## Roles de Barbeiro

| Role | Descrição |
|---|---|
| `ADMIN` | Pode cadastrar novos barbeiros e executar todas as operações de gestão |
| `EMPLOYEE` | Pode gerenciar agendamentos, mas não pode cadastrar novos barbeiros |

### Regra de bootstrap (primeiro barbeiro)

Quando a tabela `TB_BARBERS` está vazia, o **primeiro cadastro** é automaticamente promovido a `ADMIN`. Para isso, **duas condições devem ser atendidas**:

1. `requestingBarberId` deve ser omitido na requisição.
2. O IP da máquina que envia a requisição deve ser igual ao valor configurado em `TB_CONFIG` com `CONFIG_NAME = 'SERVER-PRINCIPLE-IP'`, `BARBER_ID = NULL` e `LOCATION_ID = NULL`.

**Esse registro deve ser inserido diretamente no banco pelo DBA antes do primeiro cadastro:**

```sql
INSERT INTO TB_CONFIG (CONFIG_NAME, CONFIG_VALUE, BARBER_ID, LOCATION_ID)
VALUES ('SERVER-PRINCIPLE-IP', '192.168.1.100', NULL, NULL);
```

Se o IP não estiver configurado ou não coincidir com o da requisição, a resposta será `403 Forbidden` — sem expor o motivo, por segurança.

Após o bootstrap, qualquer novo barbeiro exige autorização de um `ADMIN` existente via `requestingBarberId` e é criado como `EMPLOYEE`.

---

## Status de Agendamentos

| Código | Descrição |
|---|---|
| `A` | Ativo |
| `P` | Pendente |
| `C` | Cancelado |
| `F` | Finalizado |

---

## Segurança

- Senhas hasheadas com **Argon2** (parâmetros Spring Security v5.8).
- Tokens JWT assinados com **HMAC256**, expiração configurável.
- API **stateless** — sem sessão HTTP.
- CSRF desabilitado (API REST pura).
- Rotas públicas restritas ao mínimo necessário: `/auth/login` e `POST /v1/customer`.
