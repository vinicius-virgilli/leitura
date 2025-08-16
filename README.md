# 📚 Projeto Leitura

Este é um projeto pessoal desenvolvido com o objetivo de organizar e acompanhar minha **lista de leitura** — contemplando livros que estou lendo, que já li ou pretendo ler, independentemente de possuí-los fisicamente.

## 🚀 Tecnologias utilizadas

- **Java 17**
- **Quarkus** (framework leve e moderno para aplicações Java)
- **PostgreSQL** (banco de dados em produção)
- **PostgreSQL** (banco de dados para desenvolvimento)
- **H2 Database** (para testes locais)
- **REST API** com padrão RESTful
- **OpenAPI/Swagger** (documentação interativa da API)
- **Docker** (containerização da aplicação)
- **Maven** (gerenciamento de dependências)

## 🎯 Objetivo

Gerenciar minha **leitura** com os seguintes dados:
- Nome do livro
- Número total de páginas
- Progresso da leitura (páginas lidas, página que eu deveria estar atualmente, etc.)
- Ordem de leitura
- Status da leitura (por ler, lendo, lido)
- Datas de início e término da leitura

## ✅ Funcionalidades implementadas

### Core Features
- ✅ Cadastro de livros na lista de leitura
- ✅ Listagem e busca de livros
- ✅ Atualização de status e progresso de leitura
- ✅ Exclusão de livros
- ✅ Cálculo automático de progresso diário
- ✅ Sistema de métricas de leitura

### Infraestrutura
- ✅ API REST completa e documentada
- ✅ Documentação interativa com Swagger/OpenAPI
- ✅ Containerização com Docker
- ✅ Banco PostgreSQL para produção
- ✅ Banco H2 para desenvolvimento local
- ✅ Agendamento automático de tarefas
- ✅ Health checks e monitoramento

## 🧠 Em andamento

- Cálculo automático de página atual e progresso  
  (atualizado automaticamente ao adicionar um livro ou por meio de **agendamento diário**)
- Diferenciação entre leitura **intelectual** e **espiritual**, com ajustes de dias úteis (ex: sem leitura espiritual aos domingos)
- Integração futura com o **Google Agenda** para registrar diariamente a página que devo estar em cada tipo de leitura
- Interface frontend (futuramente em React ou Vue)
- Deploy gratuito na nuvem (pesquisa em andamento)

## 🔧 Como rodar o projeto

### Desenvolvimento Local

```bash
# 1. Clone este repositório
git clone https://github.com/seu-usuario/leitura.git

# 2. Acesse a pasta
cd leitura

# 3. Execute a aplicação (modo dev)
./mvnw quarkus:dev

# 4. Acesse a API local em:
http://localhost:8080
```

### Docker

Para executar com Docker (banco PostgreSQL + aplicação):

```bash
# Subir ambiente completo
docker-compose up -d

# Acessar aplicação
http://localhost:8080
```

## 📖 Documentação

- **[🐳 Docker Setup](docs/DOCKER_README.md)** - Guia completo para execução com Docker
- **[📋 Swagger/OpenAPI](docs/SWAGGER_SETUP.md)** - Configuração e uso da documentação da API

## 🌐 URLs Importantes

Após executar a aplicação:

- **API Base:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui
- **OpenAPI Spec:** http://localhost:8080/openapi
- **Health Check:** http://localhost:8080/q/health

🤝 Contribuições
Pull Requests são bem-vindos!
Se quiser contribuir:

Faça um fork

Crie uma branch (feature/nome)

Commit e push das alterações

Abra um Pull Request explicando sua contribuição

Autor: Vinicius Virgilli

Projeto pessoal em andamento 🚧
