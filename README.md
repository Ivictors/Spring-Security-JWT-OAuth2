# Spring-Security-JWT-OAuth2

Este projeto demonstra a implementação de um ecossistema de rede social robusto, focado em segurança e escalabilidade. 
A aplicação utiliza o fluxo OAuth2 (Resource Server) para gerenciar a autenticação e autorização de usuários de forma moderna e segura.
---

## Tecnologias e Dependências

* **Java**
* **Spring Boot 3**
* **Spring Security**: Gerenciamento de autenticação, autorização e filtros de segurança.
* **Spring Data JPA / Hibernate**: Persistência de dados e mapeamento objeto-relacional nas entidades.
* **OAuth2 Resource Server (Nimbus JWT)**: Proteção de endpoints e validação/emissão de tokens.
* **BCrypt**: Hashing seguro para armazenamento de senhas.

---

## Arquitetura de Segurança

A API adota uma arquitetura **Stateless** (sem estado) configurada no `SecurityConfig`. A autenticação é delegada ao token JWT enviado pelo cliente.

O projeto segue o padrão de camadas para garantir a separação de responsabilidades e facilidade de manutenção:

* **Camada de Controller**: Responsável por expor os endpoints e gerenciar as requisições/respostas HTTP.
* **Camada de Service**: Contém a lógica de negócio, como validação de permissões, hashing de senhas e regras de exclusão de dados.
* **Camada de Repository**: Interfaces que utilizam Spring Data JPA para comunicação com o banco de dados.
* **Camada de Entities**: Modelagem dos dados utilizando JPA (User, Role, Tweet).

### Criptografia Assimétrica (RSA)
O sistema utiliza um par de chaves (Pública/Privada) injetadas via propriedades do sistema (`jwt.privatekey` e `jwt.publickey`):
* **Chave Privada**: Utilizada pelo `JwtEncoder` para assinar o token JWT gerado durante o login.
* **Chave Pública**: Utilizada pelo `JwtDecoder` para validar a autenticidade dos tokens recebidos nas requisições protegidas.

---

## Funcionalidades Principais

### 1. Autenticação e Autorização
* **Login**: O endpoint `/login` valida as credenciais (comparando o hash BCrypt) e retorna um JWT contendo o ID do usuário e suas permissões (escopos).
* **Controle de Acesso (RBAC)**: O sistema suporta as roles `ADMIN` e `BASIC`. O acesso a endpoints específicos é controlado via anotações como `@PreAuthorize("hasAuthority('SCOPE_admin')")`.

### 2. Gestão de Usuários
* **Setup Automático**: O `AdminUserConfig` é executado na inicialização da aplicação e garante a criação de um usuário "admin" com a role `ADMIN` caso ele não exista.
* **Cadastro Público**: Novos usuários podem se cadastrar através do endpoint `/users`. Por padrão, eles recebem a role `BASIC` e não podem usar um `username` já existente.

### 3. Tweets e Feed
* **Criação de Conteúdo**: Usuários autenticados podem publicar tweets. O sistema extrai o ID do usuário diretamente do token JWT (`token.getName()`) para vincular o autor à postagem.
* **Feed Global**: Um endpoint público `/feed` fornece uma listagem paginada (tamanho padrão de 10 itens) de todos os tweets, ordenados do mais recente para o mais antigo.
* **Exclusão Segura**: Um tweet só pode ser deletado pelo seu próprio autor ou por um usuário com a role `ADMIN`.

---

## Endpoints da API

| Método | Endpoint | Descrição | Requisito de Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/users` | Cria um novo usuário (`BASIC`) | **Público** |
| `POST` | `/login` | Autentica e retorna o JWT e tempo de expiração | **Público** |
| `GET` | `/feed?page=0&pageSize=10` | Retorna o feed de tweets paginado | **Autenticado** |
| `POST` | `/tweets` | Publica um novo tweet | **Autenticado** |
| `DELETE` | `/tweet/{id}` | Deleta um tweet específico | **Autor ou Admin** |
| `GET` | `/users` | Lista todos os usuários do sistema | **Apenas Admin** |

---

## Configuração do Ambiente

Para executar este projeto localmente, você precisará gerar um par de chaves RSA e configurá-las no seu arquivo `application.properties` ou como variáveis de ambiente, conforme o mapeamento esperado pela classe `SecurityConfig`:

```properties
jwt.publickey=classpath:app.pub
jwt.privatekey=classpath:app.key
````

### 1. Gerar a Chave Privada
Este comando deve ser inserido no terminal e cria uma chave privada RSA de 2048 bits.
openssl genrsa -out app.key 2048

### 2. Extrair a Chave Pública
A partir da chave privada gerada, extraímos a chave pública correspondente.
openssl rsa -in app.key -pubout -out app.pub
