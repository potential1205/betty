# generate-all-wrappers.ps1 (최신 web3j CLI용)

$contracts = @("MVPVoting","LiquidityPool", "Exchange", "Token", "RewardPool", "ExchangeView")

$abiBase = "../../BETTY/S12P21A609/"
$output = "./src/main/java"
$package = "org.example.betty.contract"

foreach ($contract in $contracts) {
    Write-Host "`n▶️ Generating wrapper for $contract"

    $abiPath = "$abiBase$contract.abi"
    $binPath = "$abiBase$contract.bin"

    $command = "web3j generate solidity -a `"$abiPath`" -b `"$binPath`" -o `"$output`" -p $package"

    Invoke-Expression $command

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ $contract.java success"
    } else {
        Write-Host "❌ $contract.java failed"
    }
}


