# Sistema de Gestão de Oficina (SGO)

Sistema completo de gestão de oficina automóvel com backend Java (Spring Boot) e frontend web (HTML5 + CSS3 + JavaScript puro).

---

## Índice

1. [Descrição do Projeto](#descrição-do-projeto)
2. [Tecnologias Utilizadas](#tecnologias-utilizadas)
3. [Estrutura do Projeto](#estrutura-do-projeto)
4. [Pré-requisitos](#pré-requisitos)
5. [Configuração da Base de Dados](#configuração-da-base-de-dados)
6. [Correr o Backend](#correr-o-backend)
7. [Correr o Frontend](#correr-o-frontend)
8. [Perfis de Utilizador](#perfis-de-utilizador)
9. [Funcionalidades Principais](#funcionalidades-principais)
10. [API REST](#api-rest)
11. [Segurança e Autenticação](#segurança-e-autenticação)
12. [Regras de Negócio](#regras-de-negócio)
13. [Roadmap](#roadmap)

---

## Descrição do Projeto

O **SGO** é uma aplicação web completa para gestão de uma oficina automóvel. Permite gerir clientes, viaturas, agendamentos, ordens de reparação, stock de peças e visualizar KPIs de desempenho.

O sistema suporta **três perfis de utilizador**:
- **Gerente (MANAGER)** – acesso total: dashboard de KPIs, relatórios, gestão de utilizadores e parametrizações.
- **Receção (RECEPTION)** – gestão de clientes, viaturas, agenda semanal e criação de ordens de reparação.
- **Mecânico (MECHANIC)** – painel de trabalho dedicado: ver reparações atribuídas, cronómetro, checklist de diagnóstico, requisição de peças e conclusão de trabalhos.

---

## Tecnologias Utilizadas

| Camada     | Tecnologia                                          |
|------------|-----------------------------------------------------|
| Backend    | Java 17, Spring Boot 3.2.5                         |
| ORM        | Spring Data JPA + Hibernate                         |
| Segurança  | Spring Security + JWT (JJWT 0.11.5)                |
| Base Dados | MySQL 8                                             |
| Build      | Maven                                               |
| Frontend   | HTML5, CSS3 (Grid & Flexbox), JavaScript ES6+ puro |

---

## Estrutura do Projeto

```
Programacao_web_Oficina/
├── backend/                        # Spring Boot API
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/oficina/sgo/
│       │   ├── SgoApplication.java
│       │   ├── config/             # SecurityConfig, DataSeeder
│       │   ├── security/           # JWT filter, UserDetailsService
│       │   ├── controller/         # REST controllers
│       │   ├── service/            # Business logic
│       │   ├── repository/         # JPA repositories
│       │   ├── model/              # JPA entities
│       │   ├── dto/                # Request/Response DTOs
│       │   └── exception/          # Global exception handler
│       └── resources/
│           ├── application.properties
│           ├── application-dev.properties
│           └── application-prod.properties
└── frontend/                       # Static web app
    ├── index.html                  # Login
    ├── dashboard.html              # Dashboard (Gerente)
    ├── agenda.html                 # Agenda semanal (Receção)
    ├── mecanico.html               # Painel do Mecânico
    ├── clientes.html               # Gestão de Clientes
    ├── viaturas.html               # Gestão de Viaturas
    ├── reparacoes.html             # Ordens de Reparação
    ├── pecas.html                  # Stock de Peças
    ├── css/styles.css
    └── js/
        ├── api.js                  # Fetch wrapper com autenticação
        ├── auth.js                 # Login/logout/token management
        ├── utils.js                # Utilitários partilhados
        ├── dashboard.js
        ├── agenda.js
        ├── mecanico.js
        ├── clientes.js
        ├── viaturas.js
        ├── reparacoes.js
        └── pecas.js
```

---

## Pré-requisitos

- **Java 17** ou superior
- **Maven 3.6+**
- **MySQL 8.0+**
- Um browser moderno (Chrome, Firefox, Edge) para o frontend

---

## Configuração da Base de Dados

1. Instala e inicia o MySQL.
2. Cria a base de dados (o sistema cria automaticamente se usar a connection string com `createDatabaseIfNotExist=true`):
   ```sql
   CREATE DATABASE sgo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Edita `backend/src/main/resources/application.properties` com as tuas credenciais:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/sgo_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=SEU_UTILIZADOR
   spring.datasource.password=SUA_PASSWORD
   ```

### Variáveis de Ambiente (produção)

Para produção, define estas variáveis de ambiente em vez de editar o ficheiro:

| Variável            | Descrição                      |
|---------------------|--------------------------------|
| `DB_URL`            | URL JDBC da base de dados      |
| `DB_USERNAME`       | Utilizador da BD               |
| `DB_PASSWORD`       | Password da BD                 |
| `JWT_SECRET`        | Chave secreta para JWT         |
| `CORS_ORIGINS`      | Origens permitidas (CORS)      |

---

## Correr o Backend

```bash
cd backend

# Compilar e correr com Maven
./mvnw spring-boot:run

# Ou com Maven instalado globalmente
mvn spring-boot:run

# Para um ambiente específico (dev/prod)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

O servidor inicia em **http://localhost:8080**.

### Utilizador Inicial (seed)

Na primeira execução, o `DataSeeder` cria automaticamente um utilizador administrador:

| Campo      | Valor        |
|------------|--------------|
| Username   | `admin`      |
| Password   | `admin123`   |
| Role       | `MANAGER`    |

> ⚠️ **Importante:** Altera a password do utilizador `admin` imediatamente após o primeiro login em produção.

---

## Correr o Frontend

O frontend é composto por ficheiros estáticos que podem ser servidos de duas formas:

### Opção 1: Servir via Spring Boot (recomendado)

Coloca os ficheiros do `frontend/` em `backend/src/main/resources/static/` e o Spring Boot serve-os automaticamente em `http://localhost:8080/`.

### Opção 2: Servidor HTTP simples (desenvolvimento)

```bash
cd frontend

# Com Python
python3 -m http.server 3000

# Com Node.js (npx)
npx serve .

# Com PHP
php -S localhost:3000
```

Abre o browser em **http://localhost:3000**.

> **Nota:** Certifica-te que o backend está a correr em `http://localhost:8080` antes de abrir o frontend.

---

## Perfis de Utilizador

| Role        | Acesso                                                                 |
|-------------|------------------------------------------------------------------------|
| `MANAGER`   | Dashboard KPIs, gestão de utilizadores, relatórios, todas as páginas   |
| `RECEPTION` | Agenda, clientes, viaturas, reparações, peças                          |
| `MECHANIC`  | Painel do mecânico (apenas reparações atribuídas ao próprio)           |

Após login, o utilizador é automaticamente redirecionado para a página correspondente ao seu perfil.

---

## Funcionalidades Principais

### Dashboard (Gerente)
- KPIs: faturação diária/semanal/mensal
- Reparações em curso e concluídas hoje
- Ocupação da oficina: **X/8 viaturas** com barra de progresso visual
- Alertas de stock baixo
- Gestão de utilizadores do sistema

### Agenda Semanal (Receção)
- Visualização tipo calendário (segunda a sábado, 8h–18h)
- Cor dos slots por ocupação:
  - 🟢 Verde: 0/3 reparações
  - 🟡 Amarelo: 1–2/3 reparações
  - 🔴 Vermelho: 3/3 (CHEIO)
- Criar/editar/cancelar marcações
- Navegação entre semanas

### Painel do Mecânico
- Lista de reparações atribuídas (pendentes/em execução)
- **Cronómetro HH:MM:SS** por operação (start/stop/reset)
- Checklist de diagnóstico interativa
- Requisição de peças com aviso de stock baixo
- Conclusão de trabalho com validação

### Gestão de Clientes
- CRUD completo com pesquisa
- Histórico de viaturas e reparações por cliente

### Gestão de Viaturas
- CRUD com pesquisa por matrícula
- Histórico de intervenções

### Gestão de Stock de Peças
- CRUD de peças com referência, stock atual e stock mínimo
- Entradas/saídas de stock
- Alertas de stock abaixo do mínimo
- Histórico de movimentos

---

## API REST

Base URL: `http://localhost:8080/api`

| Recurso          | Endpoint                              | Métodos              |
|------------------|---------------------------------------|----------------------|
| Autenticação     | `/auth/login`                         | POST                 |
| Utilizadores     | `/users`                              | GET, POST, PUT, DELETE |
| Clientes         | `/clientes`                           | GET, POST, PUT, DELETE |
| Viaturas         | `/viaturas`                           | GET, POST, PUT, DELETE |
| Agenda           | `/agenda`, `/agenda/semana/{data}`    | GET, POST, PUT, DELETE |
| Reparações       | `/reparacoes`                         | GET, POST, PUT, DELETE |
| Operações        | `/reparacoes/{id}/operacoes`          | GET, POST, PUT       |
| Peças            | `/pecas`                              | GET, POST, PUT, DELETE |
| Dashboard KPIs   | `/dashboard/kpis`                     | GET                  |

Todos os endpoints (exceto `/auth/**`) requerem o header:
```
Authorization: Bearer <JWT_TOKEN>
```

---

## Segurança e Autenticação

- **JWT Stateless**: tokens com validade de 24 horas.
- **BCrypt**: passwords armazenadas com hash BCrypt.
- **RBAC**: controlo de acesso baseado em roles (MANAGER, RECEPTION, MECHANIC).
- **CORS**: configurável via propriedade `cors.allowed-origins` (por omissão `*` em desenvolvimento).
- **CSRF**: desativado (não aplicável para API REST com JWT Bearer tokens).
- O token JWT é armazenado em `sessionStorage` no browser (limpo ao fechar o separador).

---

## Regras de Negócio

### Capacidade da Agenda
- **Máximo de 3 reparações por slot horário** — validado no `AgendaService`. Retorna HTTP 409 com mensagem clara se excedido.

### Capacidade da Oficina
- **Máximo de 8 viaturas em simultâneo** — validado no `ReparacaoService` ao iniciar uma reparação. Retorna HTTP 409 se excedido.

### Modelo Cliente–Viatura
- Relação 1:N (um cliente, múltiplas viaturas).
- Matrícula única — validada no `ViaturaService`.
- Não é permitido registar uma viatura sem cliente associado.

### Rastreabilidade de Reparações
- Cada reparação regista: mecânico responsável, lista de operações com tempos, peças consumidas, valor total.
- Histórico completo consultável por reparação, viatura ou cliente.

---

## Roadmap

- [ ] Testes automatizados (JUnit, Mockito) para serviços e controladores
- [ ] Migração de esquema com Flyway
- [ ] Notificações por email (confirmação de marcações)
- [ ] Módulo de faturação completo com emissão de PDF
- [ ] Suporte multi-oficina
- [ ] Aplicação móvel nativa (React Native ou Flutter)
- [ ] Dashboard com gráficos interativos (Chart.js ou D3.js)
- [ ] Integração com sistemas de diagnóstico OBD-II
- [ ] Relatórios exportáveis em PDF/Excel
- [ ] Modo offline com sincronização (PWA)

