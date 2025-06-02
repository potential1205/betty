$contracts = @("MVPVoting","LiquidityPool", "Exchange", "Token", "RewardPool", "ExchangeView")
foreach ($contract in $contracts) {
    Write-Host "`n▶️ Processing: $contract"

    $jsonPath = "out/$contract.sol/$contract.json"

    if (-Not (Test-Path $jsonPath)) {
        Write-Host "❌ JSON: $jsonPath"
        continue
    }

    $json = Get-Content $jsonPath -Raw | ConvertFrom-Json

    # .abi
    $json.abi | ConvertTo-Json -Depth 100 | Set-Content -Encoding UTF8 "$contract.abi"

    # .bin
    $json.bytecode.object | Set-Content -Encoding UTF8 "$contract.bin"

    Write-Host "✅ $contract.abi / $contract.bin succeed"
}
