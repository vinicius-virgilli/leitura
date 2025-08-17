#!/bin/bash

# Script para configurar ambiente de desenvolvimento
# Uso: ./scripts/setup-env.sh [dev|prod]

if [ $# -eq 0 ]; then
    echo "❌ Erro: Especifique o ambiente (dev ou prod)"
    echo "💡 Uso: ./scripts/setup-env.sh [dev|prod]"
    exit 1
fi

ENVIRONMENT=$1
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "🔧 Configurando ambiente: $ENVIRONMENT"

case $ENVIRONMENT in
    "dev")
        echo "📝 Copiando configurações de desenvolvimento..."
        cp "$PROJECT_ROOT/.env.example" "$PROJECT_ROOT/.env"
        echo "✅ Ambiente de desenvolvimento configurado!"
        echo "💡 Para executar: mvn quarkus:dev"
        ;;
    "prod")
        echo "📝 Copiando configurações de produção..."
        cp "$PROJECT_ROOT/.env.prod" "$PROJECT_ROOT/.env"
        echo "⚠️  ATENÇÃO: Você está usando o banco de PRODUÇÃO!"
        echo "🔗 Para conectar ao banco, execute primeiro:"
        echo "   flyctl proxy 5432:5432 -a leitura-db"
        echo "✅ Ambiente de produção configurado!"
        echo "💡 Para executar: mvn quarkus:dev -Dquarkus.profile=prod"
        ;;
    *)
        echo "❌ Erro: Ambiente inválido. Use 'dev' ou 'prod'"
        exit 1
        ;;
esac

echo ""
echo "📋 Próximos passos:"
if [ "$ENVIRONMENT" = "prod" ]; then
    echo "1. Execute: flyctl proxy 5432:5432 -a leitura-db"
    echo "2. Em outro terminal: mvn quarkus:dev -Dquarkus.profile=prod"
else
    echo "1. Execute: mvn quarkus:dev"
fi
echo "2. Acesse: http://localhost:8080/q/swagger-ui"
echo "3. Health Check: http://localhost:8080/health"