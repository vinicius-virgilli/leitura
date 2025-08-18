# Guia de Teste de Carga com JMeter - API de Leitura

## Visão Geral

Este guia fornece instruções completas para executar testes de carga na API de Leitura usando Apache JMeter. A collection inclui 5 cenários diferentes que testam aspectos específicos da performance da aplicação.

## Pré-requisitos

### Software Necessário
- **Apache JMeter 5.6.3+** - [Download](https://jmeter.apache.org/download_jmeter.cgi)
- **Java 8+** - Para executar o JMeter
- **API de Leitura** - Aplicação rodando localmente

### Preparação da Aplicação

1. **Compile e execute a aplicação em modo produção:**
```bash
./mvnw clean package -Pnative
./target/leitura-1.0.0-SNAPSHOT-runner
```

2. **Ou execute com JVM otimizada:**
```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

3. **Verifique se a aplicação está rodando:**
```bash
curl http://localhost:8080/health
```

### Preparação dos Dados de Teste

1. **Crie usuários de teste no banco:**
```sql
INSERT INTO usuario (nome, email, senha, perfil, ativo) VALUES 
('Admin Teste', 'admin@teste.com', '$2a$10$...', 'ADMIN', true),
('User Teste', 'user@teste.com', '$2a$10$...', 'USER', true);
```

2. **Crie alguns livros e métricas iniciais para testes de leitura**

## Estrutura da Collection

### Cenários de Teste

| Cenário | Objetivo | Threads | Duração | Foco |
|---------|----------|---------|---------|------|
| **1. Login Intensivo** | Testar autenticação sob carga | 50 | 5 min | Rate limiting de login |
| **2. Operações de Leitura** | Testar cache e consultas | 100 | 10 min | Performance de cache |
| **3. Operações de Escrita** | Testar criação de dados | 30 | 5 min | Rate limiting de escrita |
| **4. Stress Misto** | Simular tráfego real | 200 | 15 min | Performance geral |
| **5. Health Check** | Monitorar saúde da aplicação | 5 | 15 min | Disponibilidade |

### Endpoints Testados

#### Autenticação
- `POST /usuarios/login` - Login de usuários

#### Usuários
- `GET /usuarios` - Listar usuários (com cache)
- `POST /usuarios` - Criar usuário (com rate limiting)
- `GET /usuarios/{id}` - Buscar usuário por ID
- `GET /usuarios/email/{email}` - Buscar por email

#### Livros
- `GET /livros` - Listar todos os livros (com cache)
- `GET /livros/usuario/{id}` - Livros por usuário (com cache)
- `POST /livros/usuario/{id}` - Criar livro (com rate limiting)
- `PUT /livros/status/atualizar` - Atualizar status
- `PUT /livros/progresso/manual` - Atualizar progresso

#### Métricas
- `GET /metricas/usuario/{id}` - Métricas por usuário (com cache)
- `GET /metricas` - Todas as métricas (com cache)
- `POST /metricas/usuario/{id}` - Criar métrica (com rate limiting)

#### Monitoramento
- `GET /health` - Health check
- `GET /metrics/prometheus` - Métricas Prometheus

## Como Executar os Testes

### 1. Execução via Interface Gráfica

```bash
# Abrir JMeter GUI
jmeter

# Carregar a collection
File > Open > Leitura_API_JMeter_LoadTest.jmx

# Configurar variáveis se necessário
# Executar teste
Run > Start
```

### 2. Execução via Linha de Comando (Recomendado)

```bash
# Teste completo
jmeter -n -t Leitura_API_JMeter_LoadTest.jmx -l results/test_results.jtl -e -o results/html_report

# Teste com parâmetros customizados
jmeter -n -t Leitura_API_JMeter_LoadTest.jmx \
  -Jhost=localhost \
  -Jport=8080 \
  -l results/custom_test.jtl \
  -e -o results/custom_report

# Teste apenas um cenário específico
jmeter -n -t Leitura_API_JMeter_LoadTest.jmx \
  -JtestPlan.enabled=false \
  -J"Cenário 2 - Operações de Leitura.enabled"=true \
  -l results/cache_test.jtl
```

### 3. Parâmetros Configuráveis

| Parâmetro | Padrão | Descrição |
|-----------|--------|----------|
| `host` | localhost | Host da aplicação |
| `port` | 8080 | Porta da aplicação |
| `admin_email` | admin@teste.com | Email do admin |
| `admin_password` | 123456 | Senha do admin |
| `user_email` | user@teste.com | Email do usuário |
| `user_password` | 123456 | Senha do usuário |

## Interpretação dos Resultados

### Métricas Importantes

#### Performance
- **Response Time (ms)**: Tempo de resposta médio
  - ✅ Excelente: < 100ms
  - ✅ Bom: 100-500ms
  - ⚠️ Aceitável: 500ms-1s
  - ❌ Ruim: > 1s

- **Throughput (req/s)**: Requisições por segundo
  - ✅ Target: > 1000 req/s para leitura
  - ✅ Target: > 100 req/s para escrita

- **Error Rate (%)**: Taxa de erro
  - ✅ Excelente: < 0.1%
  - ✅ Bom: < 1%
  - ⚠️ Aceitável: < 5%
  - ❌ Ruim: > 5%

#### Rate Limiting
- **HTTP 429**: Esperado quando rate limit é atingido
- **Distribuição**: Deve ser uniforme entre threads

#### Cache
- **Cache Hit**: Tempos de resposta muito baixos (< 50ms)
- **Cache Miss**: Tempos maiores na primeira requisição

### Relatórios Gerados

1. **HTML Report** (`results/html_report/index.html`)
   - Dashboard completo com gráficos
   - Análise de performance por endpoint
   - Distribuição de tempos de resposta

2. **JTL Files** (`results/*.jtl`)
   - Dados brutos para análise customizada
   - Importação em ferramentas de BI

3. **Logs de Console**
   - Progresso em tempo real
   - Erros e warnings

## Cenários de Teste Detalhados

### Cenário 1: Login Intensivo
**Objetivo**: Testar rate limiting e performance de autenticação

- **Carga**: 50 usuários simultâneos
- **Duração**: 5 minutos
- **Padrão**: 10 logins por usuário
- **Expectativa**: Rate limiting deve ativar após limite configurado

### Cenário 2: Operações de Leitura
**Objetivo**: Validar eficiência do cache

- **Carga**: 100 usuários simultâneos
- **Duração**: 10 minutos
- **Padrão**: 20 requisições por usuário
- **Expectativa**: Tempos de resposta baixos após cache warming

### Cenário 3: Operações de Escrita
**Objetivo**: Testar rate limiting em operações de criação

- **Carga**: 30 usuários simultâneos
- **Duração**: 5 minutos
- **Padrão**: 5 criações por usuário
- **Expectativa**: Rate limiting deve proteger contra sobrecarga

### Cenário 4: Stress Misto
**Objetivo**: Simular tráfego real com mix 70/30 leitura/escrita

- **Carga**: 200 usuários simultâneos
- **Duração**: 15 minutos
- **Padrão**: 15 operações por usuário
- **Expectativa**: Performance estável sob carga mista

### Cenário 5: Health Check
**Objetivo**: Monitorar disponibilidade durante testes

- **Carga**: 5 usuários
- **Duração**: Durante todos os testes
- **Padrão**: Verificação a cada 30 segundos
- **Expectativa**: 100% de disponibilidade

## Troubleshooting

### Problemas Comuns

#### 1. Conexão Recusada
```
Connection refused
```
**Solução**: Verificar se a aplicação está rodando na porta correta

#### 2. Rate Limiting Excessivo
```
HTTP 429 - Too Many Requests
```
**Solução**: Ajustar configurações de rate limiting ou reduzir carga

#### 3. Timeout de Conexão
```
Read timeout
```
**Solução**: Aumentar timeouts no JMeter ou otimizar aplicação

#### 4. Memória Insuficiente
```
OutOfMemoryError
```
**Solução**: Aumentar heap do JMeter:
```bash
export JVM_ARGS="-Xms1g -Xmx4g"
jmeter -n -t test.jmx
```

### Otimizações de Performance

#### JMeter
```bash
# Configurações recomendadas para testes de alta carga
export JVM_ARGS="-Xms2g -Xmx4g -XX:+UseG1GC"

# Desabilitar listeners desnecessários em modo CLI
# Usar apenas Summary Report para monitoramento
```

#### Sistema Operacional
```bash
# Linux - Aumentar limites de arquivo
ulimit -n 65536

# Windows - Configurar TCP/IP
netsh int tcp set global autotuninglevel=normal
```

## Análise de Resultados Esperados

### Performance Targets

| Endpoint | Response Time (P95) | Throughput | Error Rate |
|----------|-------------------|------------|------------|
| Login | < 200ms | > 100 req/s | < 1% |
| Listar Livros (Cache) | < 50ms | > 1000 req/s | < 0.1% |
| Criar Livro | < 500ms | > 50 req/s | < 2% |
| Health Check | < 100ms | N/A | 0% |
| Métricas Prometheus | < 200ms | N/A | < 0.1% |

### Indicadores de Sucesso

✅ **Cache Funcionando**:
- Primeira requisição: ~200-500ms
- Requisições subsequentes: <50ms
- Hit ratio > 80%

✅ **Rate Limiting Ativo**:
- HTTP 429 após limite atingido
- Distribuição uniforme de rejeições
- Aplicação permanece estável

✅ **Performance Geral**:
- CPU < 80% durante picos
- Memória estável (sem vazamentos)
- Conexões de DB < 80% do pool

## Próximos Passos

1. **Análise de Bottlenecks**: Identificar gargalos específicos
2. **Tuning de Performance**: Ajustar configurações baseado nos resultados
3. **Testes de Regressão**: Executar após mudanças no código
4. **Monitoramento Contínuo**: Integrar com pipeline CI/CD

## Recursos Adicionais

- [Documentação JMeter](https://jmeter.apache.org/usermanual/)
- [Quarkus Performance Guide](https://quarkus.io/guides/performance-measure)
- [Prometheus Metrics](http://localhost:8080/metrics/prometheus)
- [Health Check](http://localhost:8080/health)