# Changelog

Todas as mudanças importantes neste projeto serão documentadas neste arquivo.

O formato é baseado no [Keep a Changelog](https://keepachangelog.com/), e este projeto segue a [Semantic Versioning](https://semver.org/).

---

## [1.0.0] - 2025-01-23

### Adicionado
- Configuração inicial do **Gateway Service** usando Spring Cloud Gateway.
- Integração com o `user-service` para gerenciar rotas internas.
- Configuração dinâmica de rotas com `RouteLocator` e filtro de autenticação.
- Filtro para validação de token JWT e autorização baseada no tipo de usuário (`ADMIN` ou `USER`).
- Suporte a rotas públicas, como `/users/auth`, ignoradas pelo filtro de autenticação.
- Rotas protegidas verificadas pelo tipo de usuário
- Configuração global para desabilitar CSRF, permitindo chamadas REST.
- Validação de tokens JWT com comunicação via Feign Client ao `user-service`.

---

## Como Usar Este Changelog

- **Adicionado**: Funcionalidades novas introduzidas no serviço.
- **Alterado**: Ajustes ou melhorias em funcionalidades existentes.
- **Removido**: Funcionalidades que foram descontinuadas ou removidas.
- **Corrigido**: Erros e bugs corrigidos.

---

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).
