# 🐳 Docker Setup - Projeto Leitura

Este guia contém todos os comandos necessários para executar o projeto Leitura usando Docker.

## 📋 Pré-requisitos

- Docker instalado
- Docker Compose instalado
- Porta 5432 (PostgreSQL) e 8080 (aplicação) disponíveis

## 🗄️ Banco de Dados PostgreSQL

### Subir apenas o banco de dados

```bash
# Subir apenas o serviço do banco de dados
docker-compose up -d db

# Verificar se o banco está rodando
docker-compose ps

# Ver logs do banco de dados
docker-compose logs db

# Parar o banco de dados
docker-compose stop db
```

### Configurações do Banco

- **Host:** localhost
- **Porta:** 5432
- **Database:** leitura
- **Usuário:** postgres
- **Senha:** postgres
- **Volume:** `./postgres-data` (dados persistidos localmente)

## 🚀 Aplicação Quarkus

### Subir a aplicação completa (banco + app)

```bash
# Subir todos os serviços (banco + aplicação)
docker-compose up -d

# Verificar status dos containers
docker-compose ps

# Ver logs da aplicação
docker-compose logs app

# Ver logs de todos os serviços
docker-compose logs

# Seguir logs em tempo real
docker-compose logs -f
```

### Subir apenas a aplicação (banco já rodando)

```bash
# Subir apenas o serviço da aplicação
docker-compose up -d app
```

### Build e execução da aplicação

```bash
# Rebuild da aplicação (após mudanças no código)
docker-compose build app

# Rebuild e subir
docker-compose up -d --build app

# Rebuild completo (banco + app)
docker-compose up -d --build
```

## 🔧 Comandos Úteis

### Gerenciamento dos containers

```bash
# Parar todos os serviços
docker-compose stop

# Parar e remover containers
docker-compose down

# Parar, remover containers e volumes
docker-compose down -v

# Reiniciar serviços
docker-compose restart

# Reiniciar apenas a aplicação
docker-compose restart app
```

### Monitoramento

```bash
# Status dos containers
docker-compose ps

# Uso de recursos
docker stats

# Inspecionar container da aplicação
docker inspect leitura-app-1

# Inspecionar container do banco
docker inspect leitura-db-1
```

### Acesso aos containers

```bash
# Acessar container da aplicação
docker-compose exec app sh

# Acessar container do banco de dados
docker-compose exec db psql -U postgres -d leitura

# Executar comando no container da aplicação
docker-compose exec app java -version
```

## 🌐 URLs de Acesso

Após subir a aplicação, você pode acessar:

- **Aplicação:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui
- **OpenAPI JSON:** http://localhost:8080/openapi
- **Health Check:** http://localhost:8080/q/health

## 🗃️ Backup e Restore do Banco

### Backup

```bash
# Backup do banco de dados
docker-compose exec db pg_dump -U postgres leitura > backup_leitura.sql

# Backup com timestamp
docker-compose exec db pg_dump -U postgres leitura > backup_leitura_$(date +%Y%m%d_%H%M%S).sql
```

### Restore

```bash
# Restore do banco de dados
docker-compose exec -T db psql -U postgres leitura < backup_leitura.sql
```

## 🐛 Troubleshooting

### Problemas comuns

1. **Porta 5432 já em uso:**
   ```bash
   # Verificar o que está usando a porta
   netstat -tulpn | grep 5432
   
   # Parar PostgreSQL local se necessário
   sudo systemctl stop postgresql
   ```

2. **Porta 8080 já em uso:**
   ```bash
   # Verificar o que está usando a porta
   netstat -tulpn | grep 8080
   
   # Alterar porta no docker-compose.yml se necessário
   # ports: "8081:8080"
   ```

3. **Aplicação não conecta no banco:**
   ```bash
   # Verificar se o banco está rodando
   docker-compose logs db
   
   # Verificar variáveis de ambiente
   docker-compose exec app env | grep QUARKUS
   ```

4. **Limpar dados do banco:**
   ```bash
   # Parar containers e remover volume
   docker-compose down -v
   
   # Remover pasta de dados
   rm -rf postgres-data
   
   # Subir novamente
   docker-compose up -d
   ```

### Logs detalhados

```bash
# Logs da aplicação com timestamp
docker-compose logs -t app

# Logs do banco com timestamp
docker-compose logs -t db

# Últimas 50 linhas dos logs
docker-compose logs --tail=50 app
```

## 📝 Variáveis de Ambiente

A aplicação usa as seguintes variáveis de ambiente no Docker:

- `QUARKUS_DATASOURCE_USERNAME=postgres`
- `QUARKUS_DATASOURCE_PASSWORD=postgres`
- `QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://db:5432/leitura`

## 🔄 Workflow Recomendado

### Desenvolvimento

```bash
# 1. Subir apenas o banco para desenvolvimento local
docker-compose up -d db

# 2. Executar aplicação localmente com Quarkus dev mode
./mvnw quarkus:dev
```

### Produção/Teste

```bash
# 1. Subir ambiente completo
docker-compose up -d --build

# 2. Verificar saúde da aplicação
curl http://localhost:8080/q/health

# 3. Acessar Swagger para testes
# http://localhost:8080/swagger-ui
```

---

**📌 Nota:** Certifique-se de que as portas 5432 e 8080 estejam disponíveis antes de executar os comandos.