// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "forge-std/Script.sol";
import "../src/Token.sol";
import "../src/Exchange.sol";
import "../src/LiquidityPool.sol";

contract DeployExchange is Script {
    function run() external {
        vm.startBroadcast();

        // ✅ BTC (BETTY) 토큰 배포
        Token btcToken = new Token("Betty Coin", "BTC", 1000000);
        console.log("BTC Token deployed at:", address(btcToken));

        // ✅ Exchange 컨트랙트 배포
        Exchange exchange = new Exchange(address(btcToken));
        console.log("Exchange deployed at:", address(exchange));

        // ✅ 팬토큰 예제 배포 (SpringBoot(Web3j)에서 받아오는 방식 가정)
        Token fanTokenA = new Token("Fan Token A", "DSB", 1000000);
        Token fanTokenB = new Token("Fan Token B", "LGT", 1000000);

        console.log("Fan Token A (DSB) deployed at:", address(fanTokenA));
        console.log("Fan Token B (LGT) deployed at:", address(fanTokenB));

        // ✅ 유동성 풀 배포
        LiquidityPool poolA = new LiquidityPool(address(btcToken), address(fanTokenA));
        LiquidityPool poolB = new LiquidityPool(address(btcToken), address(fanTokenB));

        console.log("Liquidity Pool A (DSB) deployed at:", address(poolA));
        console.log("Liquidity Pool B (LGT) deployed at:", address(poolB));

        // ✅ 팬토큰 등록
        exchange.addFanToken("DSB", address(fanTokenA), address(poolA));
        exchange.addFanToken("LGT", address(fanTokenB), address(poolB));
        
        vm.stopBroadcast();
    }
}
