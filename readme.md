# **Gateway Service**

O **Gateway Service** é responsável por gerenciar o controle de acesso, autenticação e autorização para os serviços internos da aplicação. Ele atua como um proxy centralizado, encaminhando requisições para os serviços apropriados após validar permissões.

---

## **Índice**
- [Características](#características)
- [Pré-requisitos](#pré-requisitos)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Endpoints](#endpoints)
- [Execução](#execução)
- [Contribuição](#contribuição)
- [Licença](#licença)

---

## **Características**
- Controle centralizado de autenticação e autorização.
- Validação de tokens JWT para todas as requisições protegidas.
- Suporte a rotas públicas e privadas.
- Permissões configuráveis com base no tipo de usuário (`ADMIN` ou `USER`).
- Encaminhamento inteligente de requisições para os serviços internos (ex.: `user-service`).

---

## **Pré-requisitos**
Certifique-se de que os seguintes itens estão instalados na sua máquina:
- **Java 21+**
- **Maven 3.8+**
- **Banco de Dados (se necessário para integração com serviços internos)**

---

## **Tecnologias Utilizadas**
- **Spring Boot 3.x**: Framework principal.
- **Spring Cloud Gateway**: Gerenciamento de rotas e proxy.
- **Spring Cloud OpenFeign**: Comunicação entre serviços.
- **JWT (Json Web Token)**: Autenticação e validação de tokens.
- **Reactor**: Programação reativa com Spring WebFlux.

---

## Arquitetura

### Fluxo de Requisição
1. O cliente envia uma requisição para o **Gateway Service**.
2. O Gateway valida o token JWT nas rotas protegidas.
3. Com base no tipo de usuário (`ADMIN` ou `USER`), o Gateway decide se a requisição pode prosseguir.
4. A requisição é encaminhada para o serviço interno correspondente (ex.: `user-service`).
5. O serviço interno processa a lógica de negócio e retorna a resposta.

---

## Endpoints

| Método | Endpoint                | Descrição                                     | Acesso    |
|--------|-------------------------|-----------------------------------------------|-----------|
| POST   | `/users/auth`           | Endpoint público para autenticação de usuários.| Público   |
| GET    | `/users/all`            | Lista todos os usuários (apenas ADMIN).       | Protegido |
| GET    | `/users/{id}`           | Detalhes de um usuário específico.            | Protegido |
| GET    | `/users/auth/validate`  | Validação de token (somente Gateway).         | Protegido |

---

## Execução

### Passo 1: Clonar o repositório
```bash
git clone https://github.com/seu-usuario/gateway-service.git
cd gateway-service 
```

### Passo 2: Compilar e executar
```bash
mvn clean install
mvn spring-boot:run
````

---

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou enviar pull requests.

---

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).