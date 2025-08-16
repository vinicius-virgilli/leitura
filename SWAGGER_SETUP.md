# Configuração do Swagger/OpenAPI no Projeto Leitura

## O que foi implementado

### 1. Dependência Maven
Foi adicionada a dependência `quarkus-smallrye-openapi` no `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-openapi</artifactId>
</dependency>
```

### 2. Configurações no application.properties

```properties
# OpenAPI/Swagger Configuration
quarkus.smallrye-openapi.info-title=Leitura API
quarkus.smallrye-openapi.info-version=1.0.0
quarkus.smallrye-openapi.info-description=API para gerenciamento de leituras
quarkus.smallrye-openapi.info-contact-email=contato@viniciusvirgilli.org
quarkus.smallrye-openapi.info-contact-name=Vinicius Virgilli
quarkus.smallrye-openapi.info-license-name=MIT
quarkus.smallrye-openapi.path=/openapi
quarkus.swagger-ui.path=/swagger-ui
quarkus.swagger-ui.always-include=true
```

### 3. Classe de Configuração OpenAPI

Criada a classe `OpenApiConfig.java` com configurações avançadas:
- Informações da API (título, versão, descrição)
- Contato e licença
- Servidores (desenvolvimento e produção)
- Tags para organização dos endpoints

### 4. Anotações nos Resources

Adicionadas anotações OpenAPI nos recursos:
- `@Tag`: Para categorizar os endpoints
- `@Operation`: Para descrever cada operação
- `@ApiResponse`/`@ApiResponses`: Para documentar as respostas
- `@Parameter`: Para documentar parâmetros

## Como acessar o Swagger

### 1. Iniciar a aplicação
```bash
./mvnw quarkus:dev
```

### 2. Acessar as URLs

- **Swagger UI**: http://localhost:8080/swagger-ui
- **OpenAPI JSON**: http://localhost:8080/openapi
- **OpenAPI YAML**: http://localhost:8080/openapi?format=yaml

## Funcionalidades disponíveis

### Interface Swagger UI
- Visualização interativa de todos os endpoints
- Teste direto dos endpoints através da interface
- Documentação detalhada de parâmetros e respostas
- Exemplos de requisições e respostas

### Endpoints documentados
- **Livros**: Criar, listar, buscar por ID, atualizar status, etc.
- **Métricas**: Gerenciamento de métricas de leitura

## Benefícios da implementação

1. **Documentação automática**: A API é documentada automaticamente
2. **Testes interativos**: Possibilidade de testar endpoints diretamente
3. **Padronização**: Seguindo padrões OpenAPI 3.0
4. **Facilita integração**: Outros desenvolvedores podem entender e usar a API facilmente
5. **Geração de clientes**: Possibilidade de gerar clientes em diferentes linguagens

## Próximos passos recomendados

1. **Adicionar mais anotações**: Documentar todos os endpoints restantes
2. **Exemplos de requisição**: Adicionar exemplos usando `@ExampleObject`
3. **Esquemas personalizados**: Documentar DTOs com `@Schema`
4. **Segurança**: Adicionar documentação de autenticação se necessário
5. **Versionamento**: Configurar versionamento da API

## Exemplo de uso avançado

```java
@POST
@Operation(summary = "Criar livro", description = "Cria um novo livro no sistema")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Livro criado com sucesso",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Livro.class))),
    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
    @ApiResponse(responseCode = "500", description = "Erro interno")
})
public Response criarLivro(
    @RequestBody(description = "Dados do livro", required = true,
            content = @Content(schema = @Schema(implementation = LivroCriacaoDto.class)))
    LivroCriacaoDto livro) {
    // implementação
}
```

Com essa configuração, o projeto agora possui uma documentação completa e interativa da API!