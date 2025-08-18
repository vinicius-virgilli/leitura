# Script PowerShell para Automação de Testes de Carga - API de Leitura
# Autor: Sistema de Otimização de Performance
# Data: $(Get-Date -Format 'yyyy-MM-dd')

param(
    [string]$Host = "localhost",
    [int]$Port = 8080,
    [string]$JMeterPath = "",
    [string]$TestType = "all",
    [string]$OutputDir = "results",
    [switch]$SkipAppCheck,
    [switch]$GenerateReport,
    [switch]$OpenReport
)

# Configurações
$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

# Cores para output
function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

# Banner
function Show-Banner {
    Write-ColorOutput "" "Cyan"
    Write-ColorOutput "╔══════════════════════════════════════════════════════════════╗" "Cyan"
    Write-ColorOutput "║                    TESTE DE CARGA - API LEITURA             ║" "Cyan"
    Write-ColorOutput "║                     Performance Testing Suite               ║" "Cyan"
    Write-ColorOutput "╚══════════════════════════════════════════════════════════════╝" "Cyan"
    Write-ColorOutput "" "Cyan"
}

# Verificar se aplicação está rodando
function Test-Application {
    param(
        [string]$Url
    )
    
    Write-ColorOutput "🔍 Verificando se a aplicação está rodando em $Url..." "Yellow"
    
    try {
        $response = Invoke-WebRequest -Uri "$Url/health" -Method GET -TimeoutSec 10
        if ($response.StatusCode -eq 200) {
            Write-ColorOutput "✅ Aplicação está rodando e saudável!" "Green"
            return $true
        }
    }
    catch {
        Write-ColorOutput "❌ Aplicação não está respondendo em $Url" "Red"
        Write-ColorOutput "   Erro: $($_.Exception.Message)" "Red"
        return $false
    }
    
    return $false
}

# Encontrar JMeter
function Find-JMeter {
    if ($JMeterPath -and (Test-Path $JMeterPath)) {
        return $JMeterPath
    }
    
    # Procurar em locais comuns
    $commonPaths = @(
        "C:\apache-jmeter*\bin\jmeter.bat",
        "C:\Program Files\apache-jmeter*\bin\jmeter.bat",
        "${env:JMETER_HOME}\bin\jmeter.bat",
        "jmeter.bat"  # Se estiver no PATH
    )
    
    foreach ($path in $commonPaths) {
        $resolved = Get-ChildItem $path -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($resolved) {
            return $resolved.FullName
        }
    }
    
    # Tentar comando direto
    try {
        $null = Get-Command "jmeter" -ErrorAction Stop
        return "jmeter"
    }
    catch {
        return $null
    }
}

# Criar diretório de resultados
function Initialize-OutputDirectory {
    param([string]$Dir)
    
    if (Test-Path $Dir) {
        Write-ColorOutput "🗂️  Limpando diretório de resultados existente..." "Yellow"
        Remove-Item "$Dir\*" -Recurse -Force -ErrorAction SilentlyContinue
    }
    else {
        Write-ColorOutput "📁 Criando diretório de resultados: $Dir" "Yellow"
        New-Item -ItemType Directory -Path $Dir -Force | Out-Null
    }
}

# Executar teste específico
function Invoke-LoadTest {
    param(
        [string]$JMeterExe,
        [string]$TestFile,
        [string]$TestName,
        [string]$OutputFile,
        [string]$ReportDir,
        [hashtable]$Parameters
    )
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "🚀 Executando: $TestName" "Cyan"
    Write-ColorOutput "   Arquivo: $TestFile" "Gray"
    Write-ColorOutput "   Resultado: $OutputFile" "Gray"
    
    # Construir parâmetros
    $paramString = ""
    foreach ($key in $Parameters.Keys) {
        $paramString += " -J$key=$($Parameters[$key])"
    }
    
    # Comando JMeter
    $jmeterArgs = @(
        "-n",
        "-t", $TestFile,
        "-l", $OutputFile
    )
    
    if ($GenerateReport) {
        $jmeterArgs += @("-e", "-o", $ReportDir)
    }
    
    # Adicionar parâmetros customizados
    if ($paramString) {
        $jmeterArgs += $paramString.Split(' ') | Where-Object { $_ -ne "" }
    }
    
    Write-ColorOutput "   Comando: $JMeterExe $($jmeterArgs -join ' ')" "Gray"
    
    $startTime = Get-Date
    
    try {
        & $JMeterExe $jmeterArgs
        
        if ($LASTEXITCODE -eq 0) {
            $duration = (Get-Date) - $startTime
            Write-ColorOutput "✅ Teste concluído com sucesso! Duração: $($duration.ToString('mm\:ss'))" "Green"
            return $true
        }
        else {
            Write-ColorOutput "❌ Teste falhou com código de saída: $LASTEXITCODE" "Red"
            return $false
        }
    }
    catch {
        Write-ColorOutput "❌ Erro ao executar teste: $($_.Exception.Message)" "Red"
        return $false
    }
}

# Analisar resultados
function Show-TestSummary {
    param([string]$ResultFile)
    
    if (-not (Test-Path $ResultFile)) {
        Write-ColorOutput "⚠️  Arquivo de resultado não encontrado: $ResultFile" "Yellow"
        return
    }
    
    try {
        $content = Get-Content $ResultFile
        $lines = $content | Where-Object { $_ -match '^\d+,' }  # Linhas de dados
        
        if ($lines.Count -eq 0) {
            Write-ColorOutput "⚠️  Nenhum dado encontrado no arquivo de resultado" "Yellow"
            return
        }
        
        $total = $lines.Count
        $errors = ($lines | Where-Object { $_.Split(',')[7] -eq 'false' }).Count
        $successRate = [math]::Round((($total - $errors) / $total) * 100, 2)
        
        # Calcular tempos de resposta
        $responseTimes = $lines | ForEach-Object { [int]$_.Split(',')[1] }
        $avgResponseTime = [math]::Round(($responseTimes | Measure-Object -Average).Average, 2)
        $maxResponseTime = ($responseTimes | Measure-Object -Maximum).Maximum
        $minResponseTime = ($responseTimes | Measure-Object -Minimum).Minimum
        
        Write-ColorOutput "" "White"
        Write-ColorOutput "📊 RESUMO DOS RESULTADOS:" "Cyan"
        Write-ColorOutput "   Total de Requisições: $total" "White"
        Write-ColorOutput "   Taxa de Sucesso: $successRate%" $(if ($successRate -ge 95) { "Green" } elseif ($successRate -ge 90) { "Yellow" } else { "Red" })
        Write-ColorOutput "   Erros: $errors" $(if ($errors -eq 0) { "Green" } else { "Red" })
        Write-ColorOutput "   Tempo de Resposta Médio: ${avgResponseTime}ms" $(if ($avgResponseTime -le 500) { "Green" } elseif ($avgResponseTime -le 1000) { "Yellow" } else { "Red" })
        Write-ColorOutput "   Tempo de Resposta Min/Max: ${minResponseTime}ms / ${maxResponseTime}ms" "White"
    }
    catch {
        Write-ColorOutput "❌ Erro ao analisar resultados: $($_.Exception.Message)" "Red"
    }
}

# Função principal
function Main {
    Show-Banner
    
    # Verificar aplicação
    if (-not $SkipAppCheck) {
        $appUrl = "http://${Host}:${Port}"
        if (-not (Test-Application -Url $appUrl)) {
            Write-ColorOutput "" "Red"
            Write-ColorOutput "💡 Dicas para resolver:" "Yellow"
            Write-ColorOutput "   1. Verifique se a aplicação está rodando: java -jar target/quarkus-app/quarkus-run.jar" "White"
            Write-ColorOutput "   2. Confirme a porta: netstat -an | findstr :8080" "White"
            Write-ColorOutput "   3. Use -SkipAppCheck para pular esta verificação" "White"
            Write-ColorOutput "" "White"
            exit 1
        }
    }
    
    # Encontrar JMeter
    Write-ColorOutput "🔍 Procurando instalação do JMeter..." "Yellow"
    $jmeterExe = Find-JMeter
    
    if (-not $jmeterExe) {
        Write-ColorOutput "❌ JMeter não encontrado!" "Red"
        Write-ColorOutput "" "White"
        Write-ColorOutput "💡 Soluções:" "Yellow"
        Write-ColorOutput "   1. Baixe o JMeter: https://jmeter.apache.org/download_jmeter.cgi" "White"
        Write-ColorOutput "   2. Adicione ao PATH ou use -JMeterPath" "White"
        Write-ColorOutput "   3. Defina JMETER_HOME" "White"
        Write-ColorOutput "" "White"
        exit 1
    }
    
    Write-ColorOutput "✅ JMeter encontrado: $jmeterExe" "Green"
    
    # Verificar arquivo de teste
    $testFile = "Leitura_API_JMeter_LoadTest.jmx"
    if (-not (Test-Path $testFile)) {
        Write-ColorOutput "❌ Arquivo de teste não encontrado: $testFile" "Red"
        Write-ColorOutput "   Certifique-se de que está no diretório correto" "White"
        exit 1
    }
    
    # Preparar diretórios
    Initialize-OutputDirectory -Dir $OutputDir
    
    # Parâmetros para JMeter
    $jmeterParams = @{
        "host" = $Host
        "port" = $Port
    }
    
    # Definir testes a executar
    $tests = @()
    
    switch ($TestType.ToLower()) {
        "login" {
            $tests += @{ Name = "Login Intensivo"; File = $testFile; Output = "$OutputDir/login_test.jtl"; Report = "$OutputDir/login_report" }
        }
        "cache" {
            $tests += @{ Name = "Teste de Cache"; File = $testFile; Output = "$OutputDir/cache_test.jtl"; Report = "$OutputDir/cache_report" }
        }
        "write" {
            $tests += @{ Name = "Operações de Escrita"; File = $testFile; Output = "$OutputDir/write_test.jtl"; Report = "$OutputDir/write_report" }
        }
        "stress" {
            $tests += @{ Name = "Teste de Stress"; File = $testFile; Output = "$OutputDir/stress_test.jtl"; Report = "$OutputDir/stress_report" }
        }
        "health" {
            $tests += @{ Name = "Health Check"; File = $testFile; Output = "$OutputDir/health_test.jtl"; Report = "$OutputDir/health_report" }
        }
        "all" {
            $tests += @{ Name = "Teste Completo"; File = $testFile; Output = "$OutputDir/complete_test.jtl"; Report = "$OutputDir/complete_report" }
        }
        default {
            Write-ColorOutput "❌ Tipo de teste inválido: $TestType" "Red"
            Write-ColorOutput "   Tipos válidos: login, cache, write, stress, health, all" "White"
            exit 1
        }
    }
    
    # Executar testes
    $successCount = 0
    $totalTests = $tests.Count
    
    foreach ($test in $tests) {
        $success = Invoke-LoadTest -JMeterExe $jmeterExe -TestFile $test.File -TestName $test.Name -OutputFile $test.Output -ReportDir $test.Report -Parameters $jmeterParams
        
        if ($success) {
            $successCount++
            Show-TestSummary -ResultFile $test.Output
        }
        
        Start-Sleep -Seconds 2
    }
    
    # Resumo final
    Write-ColorOutput "" "White"
    Write-ColorOutput "🏁 RESUMO FINAL" "Cyan"
    Write-ColorOutput "   Testes Executados: $totalTests" "White"
    Write-ColorOutput "   Sucessos: $successCount" $(if ($successCount -eq $totalTests) { "Green" } else { "Yellow" })
    Write-ColorOutput "   Falhas: $($totalTests - $successCount)" $(if ($successCount -eq $totalTests) { "Green" } else { "Red" })
    Write-ColorOutput "   Resultados em: $OutputDir" "White"
    
    # Abrir relatório se solicitado
    if ($OpenReport -and $GenerateReport) {
        $reportIndex = "$OutputDir/complete_report/index.html"
        if (Test-Path $reportIndex) {
            Write-ColorOutput "🌐 Abrindo relatório no navegador..." "Yellow"
            Start-Process $reportIndex
        }
    }
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "✨ Testes de carga concluídos!" "Green"
}

# Executar
try {
    Main
}
catch {
    Write-ColorOutput "❌ Erro fatal: $($_.Exception.Message)" "Red"
    Write-ColorOutput "   Stack trace: $($_.ScriptStackTrace)" "Gray"
    exit 1
}