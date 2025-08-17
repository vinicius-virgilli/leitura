# Collection Postman - API de Leitura

Este diretório contém os arquivos necessários para testar a API de Leitura usando o Postman.

## Arquivos Incluídos

- `Leitura API.postman_collection.json` - Collection principal com todos os endpoints
- `Leitura API Environments.postman_environment.json` - Arquivo de ambiente com variáveis

## Como Importar no Postman

### 1. Importar a Collection

1. Abra o Postman
2. Clique em **Import** no canto superior esquerdo
3. Selecione o arquivo `Leitura API.postman_collection.json`
4. Clique em **Import**

### 2. Importar o Environment (Opcional)

1. No Postman, clique no ícone de engrenagem (⚙️) no canto superior direito
2. Selecione **Import**
3. Selecione o arquivo `Leitura API Environments.postman_environment.json`
4. Clique em **Import**

### 3. Configurar o Environment

1. No dropdown de environments (canto superior direito), selecione **Leitura API Environments**
2. Clique no ícone de olho (👁️) para visualizar as variáveis
3. Edite a variável `production` com a URL real do seu ambiente de produção

## Estrutura da Collection

A collection está organizada em duas pastas principais:

### 📁 Localhost
Contém todos os endpoints configurados para o ambiente de desenvolvimento local (`http://localhost:8080`)

### 📁 Produção
Contém os mesmos endpoints configurados para o ambiente de produção

Cada pasta contém as seguintes sub-pastas:

#### 👥 Usuários
- **POST** Criar Usuário
- **GET** Listar Usuários
- **GET** Buscar Usuário por ID
- **PUT** Atualizar Usuário
- **DELETE** Deletar Usuário

#### 📚 Livros
- **POST** Criar Livro
- **GET** Listar Todos os Livros
- **GET** Listar Livros por Usuário
- **GET** Buscar Livro por ID
- **PUT** Atualizar Status do Livro
- **PUT** Atualizar Saldo do Livro
- **DELETE** Deletar Livro
- **POST** Atualizar Progresso Manual

#### 📊 Métricas
- **GET** Listar Métricas por Usuário

#### 🏥 Health Check
- **GET** Verificar saúde da aplicação

## Variáveis de Ambiente

| Variável | Valor Padrão | Descrição |
|----------|--------------|-----------|
| `localhost` | `http://localhost:8080` | URL do ambiente de desenvolvimento |
| `production` | `https://sua-url-de-producao.com` | URL do ambiente de produção (editar conforme necessário) |

## Exemplos de Uso

### Criar um Usuário
```json
{
  "nome": "João Silva",
  "email": "joao@exemplo.com",
  "senha": "senha123"
}
```

### Criar um Livro
```json
{
  "nome": "O Poder do Hábito",
  "categoria": "INTELECTUAL",
  "totalPaginas": 300
}
```

### Status de Livro Disponíveis
- `A_LER` - Livro adicionado mas ainda não iniciado
- `LENDO` - Livro em progresso de leitura
- `LIDO` - Livro finalizado

### Categorias de Livro Disponíveis
- `ESPIRITUAL` - Livros de desenvolvimento espiritual
- `INTELECTUAL` - Livros de desenvolvimento intelectual

## Dicas de Uso

1. **IDs Dinâmicos**: Substitua os IDs nos endpoints (ex: `/usuarios/1`) pelos IDs reais retornados pelas requisições
2. **Ordem de Teste**: Recomenda-se testar na seguinte ordem:
   - Criar usuário
   - Criar livro para o usuário
   - Testar operações de atualização
   - Testar consultas
   - Testar exclusões
3. **Ambiente Ativo**: Certifique-se de que o ambiente correto está selecionado antes de fazer as requisições
4. **Servidor Local**: Para testar localmente, certifique-se de que a aplicação está rodando em `http://localhost:8080`

## Troubleshooting

- **Erro 404**: Verifique se a aplicação está rodando e se a URL está correta
- **Erro 500**: Verifique os logs da aplicação para identificar problemas internos
- **Variáveis não resolvidas**: Certifique-se de que o environment está selecionado e as variáveis estão definidas