# Script PowerShell para configurar ambiente de desenvolvimento
# Uso: .\scripts\setup-env.ps1 [dev|prod]

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("dev", "prod")]
    [string]$Environment
)

$projectRoot = Split-Path -Parent $PSScriptRoot

Write-Host "🔧 Configurando ambiente: $Environment" -ForegroundColor Green

switch ($Environment) {
    "dev" {
        Write-Host "📝 Copiando configurações de desenvolvimento..." -ForegroundColor Yellow
        Copy-Item "$projectRoot\.env.example" "$projectRoot\.env" -Force
        Write-Host "✅ Ambiente de desenvolvimento configurado!" -ForegroundColor Green
        Write-Host "💡 Para executar: mvn quarkus:dev" -ForegroundColor Cyan
    }
    "prod" {
        Write-Host "📝 Copiando configurações de produção..." -ForegroundColor Yellow
        Copy-Item "$projectRoot\.env.prod" "$projectRoot\.env" -Force
        Write-Host "⚠️  ATENÇÃO: Você está usando o banco de PRODUÇÃO!" -ForegroundColor Red
        Write-Host "🔗 OBRIGATÓRIO: Execute o proxy do Fly.io primeiro:" -ForegroundColor Yellow
        Write-Host "   flyctl proxy 5432:5432 -a leitura-db" -ForegroundColor White
        Write-Host "📋 Aguarde a mensagem: 'Proxying local port 5432 to remote [leitura-db.internal]:5432'" -ForegroundColor Cyan
        Write-Host "✅ Ambiente de produção configurado!" -ForegroundColor Green
        Write-Host "💡 Para executar: mvn quarkus:dev `"-Dquarkus.profile=prod`"" -ForegroundColor Cyan
    }
}

Write-Host ""
Write-Host "📋 Próximos passos:" -ForegroundColor Blue
if ($Environment -eq "prod") {
    Write-Host "1. Execute: flyctl proxy 5432:5432 -a leitura-db" -ForegroundColor White
    Write-Host "2. Em outro terminal: mvn quarkus:dev -Dquarkus.profile=prod" -ForegroundColor White
} else {
    Write-Host "1. Execute: mvn quarkus:dev" -ForegroundColor White
}
Write-Host "2. Acesse: http://localhost:8080/q/swagger-ui" -ForegroundColor White
Write-Host "3. Health Check: http://localhost:8080/health" -ForegroundColor White