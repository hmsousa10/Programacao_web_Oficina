# Sistema de Gestão de Oficina (SGO)

Sistema completo de gestão de oficina automóvel com backend Java (Spring Boot) e frontend web (HTML5 + CSS3 + JavaScript puro).

---

## Índice

1. [Descrição do Projeto](#descrição-do-projeto)
2. [Tecnologias Utilizadas](#tecnologias-utilizadas)
3. [Estrutura do Projeto](#estrutura-do-projeto)
4. [Pré-requisitos](#pré-requisitos)
5. [Configuração da Base de Dados](#configuração-da-base-de-dados)
6. [Configuração no Eclipse (Windows)](#configuração-no-eclipse-windows)
7. [Correr o Backend](#correr-o-backend)
8. [Correr o Frontend](#correr-o-frontend)
9. [Perfis de Utilizador](#perfis-de-utilizador)
10. [Funcionalidades Principais](#funcionalidades-principais)
11. [API REST](#api-rest)
12. [Segurança e Autenticação](#segurança-e-autenticação)
13. [Regras de Negócio](#regras-de-negócio)
14. [Roadmap](#roadmap)

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
├── database/
│   └── init.sql                    # Script SQL de inicialização da BD
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

- **Java 17** ou superior ([download](https://adoptium.net/))
- **Maven 3.6+** (incluído no Eclipse IDE for Enterprise Java; ou [download](https://maven.apache.org/download.cgi))
- **MySQL 8.0+** ([MySQL Community Server](https://dev.mysql.com/downloads/mysql/))
- **MySQL Workbench 8.0** (opcional, mas recomendado — [download](https://dev.mysql.com/downloads/workbench/))
- Um browser moderno (Chrome, Firefox, Edge) para o frontend

> ⚠️ **Nota sobre o MySQL Connector:** Este projeto usa **Maven** para gerir todas as dependências, incluindo o `mysql-connector-j`. **Não** é necessário adicionar manualmente nenhum ficheiro `.jar` ao projeto — o Maven descarrega o conector automaticamente ao compilar.

---

## Configuração da Base de Dados

### 1. Inicializar a base de dados com o MySQL Workbench

1. Abre o **MySQL Workbench** e liga-te ao teu servidor local (geralmente `localhost:3306`).
2. Abre o ficheiro `database/init.sql` (incluído no repositório) via **File → Open SQL Script…** e executa-o (⚡ ou `Ctrl+Shift+Enter`).
   - Isto cria a base de dados `sgo_db` com o charset correto.
   - As tabelas são criadas automaticamente pelo Hibernate quando o backend arranca pela primeira vez.
3. Alternativamente, cola e executa o seguinte SQL no editor:
   ```sql
   CREATE DATABASE IF NOT EXISTS sgo_db
       CHARACTER SET utf8mb4
       COLLATE utf8mb4_unicode_ci;
   ```

### 2. Configurar as credenciais da base de dados

Edita `backend/src/main/resources/application.properties` com o utilizador e password que definiste ao instalar o MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sgo_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=A_TUA_PASSWORD_AQUI
```

> Por omissão, o utilizador é `root` e a password é a que definiste durante a instalação do MySQL. Se criaste um utilizador dedicado, usa essas credenciais.

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

## Configuração no Eclipse (Windows)

Esta secção guia-te passo a passo para abrir e correr o projeto no **Eclipse IDE for Enterprise Java and Web Developers** em Windows.

### 1. Instalar o Eclipse

1. Descarrega o **Eclipse IDE for Enterprise Java and Web Developers** em [https://www.eclipse.org/downloads/](https://www.eclipse.org/downloads/).
2. Instala e abre o Eclipse.

> O Eclipse para Enterprise Java já inclui suporte Maven (plugin **m2e**) integrado. **Não precisas de instalar Maven separadamente** nem de adicionar o `mysql-connector-j.jar` manualmente — o Maven descarrega tudo automaticamente.

### 2. Importar o projeto como projeto Maven

1. No Eclipse, vai a **File → Import…**
2. Expande **Maven** e seleciona **Existing Maven Projects** → **Next**
3. Em **Root Directory**, clica em **Browse…** e seleciona a pasta `backend` dentro do repositório clonado (ex.: `C:\Users\hugom\Programacao_web_Oficina\backend`)
4. Confirma que `pom.xml` aparece na lista → clica **Finish**
5. O Eclipse vai descarregar automaticamente todas as dependências (incluindo o MySQL Connector) do Maven Central. Aguarda até o progresso na barra inferior terminar.

### 3. Configurar as credenciais da base de dados

1. No **Project Explorer**, abre `src/main/resources/application.properties`
2. Altera a linha da password para a password do teu MySQL:
   ```properties
   spring.datasource.password=A_TUA_PASSWORD_AQUI
   ```
   Se o teu utilizador MySQL não for `root`, altera também `spring.datasource.username`.

### 4. Correr o backend a partir do Eclipse

**Opção A – Run as Spring Boot App (mais simples):**

1. No **Project Explorer**, clica com o botão direito em `SgoApplication.java` (em `src/main/java/com/oficina/sgo/`)
2. Seleciona **Run As → Spring Boot App**
3. Verifica na consola do Eclipse que aparece: `Started SgoApplication` e `Tomcat started on port 8080`

**Opção B – Run as Java Application:**

1. Clica com o botão direito em `SgoApplication.java`
2. Seleciona **Run As → Java Application**

**Opção C – Via Maven no Eclipse:**

1. Clica com o botão direito na raiz do projeto no **Project Explorer**
2. Seleciona **Run As → Maven build…**
3. Em **Goals**, escreve: `spring-boot:run`
4. Clica **Run**

### 5. Verificar que está a funcionar

Abre o browser e vai a [http://localhost:8080](http://localhost:8080). Se o frontend estiver na pasta `static`, deverá aparecer a página de login.

### Resolução de Problemas Comuns no Eclipse

| Problema | Solução |
|----------|---------|
| Erros vermelhos após importar | Clica com o botão direito no projeto → **Maven → Update Project** (Alt+F5) |
| `Access denied for user 'root'@'localhost'` | Verifica a password em `application.properties` |
| `Unknown database 'sgo_db'` | Executa o script `database/init.sql` no MySQL Workbench |
| `Communications link failure` | Confirma que o serviço MySQL está a correr (Windows Services → MySQL80 → Start) |
| Porta 8080 já em uso | Altera `server.port=8081` em `application.properties` |
| Java 17 não encontrado | Vai a **Window → Preferences → Java → Installed JREs** e adiciona o JDK 17 |

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

