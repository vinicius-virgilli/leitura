# 📚 Projeto Leitura

Este é um projeto pessoal desenvolvido com o objetivo de organizar e acompanhar minha **lista de leitura** — contemplando livros que estou lendo, que já li ou pretendo ler, independentemente de possuí-los fisicamente.

## 🚀 Tecnologias utilizadas

- **Java 17**
- **Quarkus** (framework leve e moderno para aplicações Java)
- **H2 Database** (persistente em disco)
- **REST API** com padrão RESTful

## 🎯 Objetivo

Gerenciar minha **leitura** com os seguintes dados:
- Nome do livro
- Número total de páginas
- Progresso da leitura (páginas lidas, página que eu deveria estar atualmente, etc.)
- Ordem de leitura
- Status da leitura (por ler, lendo, lido)
- Datas de início e término da leitura

## ✅ MVP já implementado

- Cadastro de livros na lista de leitura
- Listagem completa
- Banco de dados H2 persistente em disco (`./data/leitura.mv.db`)
- Lógica para definir ordem de leitura automaticamente
- Inicialização automática do banco com base nas models

## 🧠 Em andamento

- Cálculo automático de página atual e progresso  
  (atualizado automaticamente ao adicionar um livro ou por meio de **agendamento diário**)
- Diferenciação entre leitura **intelectual** e **espiritual**, com ajustes de dias úteis (ex: sem leitura espiritual aos domingos)
- Integração futura com o **Google Agenda** para registrar diariamente a página que devo estar em cada tipo de leitura
- Interface frontend (futuramente em React ou Vue)
- Deploy gratuito na nuvem (pesquisa em andamento)

## 🔧 Como rodar o projeto localmente


```bash
1. Clone este repositório:
git clone https://github.com/seu-usuario/leitura.git

2. Acesse a pasta:
cd leitura

3. Execute a aplicação (modo dev):
./mvnw quarkus:dev

4. Acesse a API local em:
http://localhost:8080

Obs: O banco H2 salva os dados em disco, então ao reiniciar, os dados são mantidos.
```

🤝 Contribuições
Pull Requests são bem-vindos!
Se quiser contribuir:

Faça um fork

Crie uma branch (feature/nome)

Commit e push das alterações

Abra um Pull Request explicando sua contribuição

Autor: Vinicius Virgilli

Projeto pessoal em andamento 🚧
