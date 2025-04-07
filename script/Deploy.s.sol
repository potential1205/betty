// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/Token.sol";
import "../src/Exchange.sol";
import "../src/LiquidityPool.sol";

// 배포 스크립트: BETTY, 팬토큰 10종, 풀 및 등록 + 유동성 세팅 포함
contract DeployExchange is Script {
    function run() external {
        vm.startBroadcast();

        // BETTY 토큰 배포
        Token betToken = new Token("BET", 1_000_000);
        console.log("BET Token deployed at:", address(betToken));

        // Exchange 컨트랙트 배포
        Exchange exchange = new Exchange(address(betToken));
        console.log("Exchange deployed at:", address(exchange));

        // 팬토큰 token_name 목록
        string[10] memory token_names = ["DSB", "SSG", "LGT", "LTG", "KWH", "NCD", "KTW", "KIA", "SSL", "HWE"];

        uint256 initialBET = 10_000 * 1e18;
        uint256 initialFanToken = 1_000 * 1e18;

        for (uint i = 0; i < token_names.length; i++) {
            string memory token_name = token_names[i];

            // 팬토큰 배포
            Token fanToken = new Token(token_name, 1_000_000);

            // 유동성 풀 생성
            LiquidityPool pool = new LiquidityPool(address(betToken), address(fanToken));

            // 팬토큰 등록
            exchange.addFanToken(token_name, address(fanToken), address(pool));

            // 초기 유동성 공급
            betToken.transfer(address(pool), initialBET);
            fanToken.transfer(address(pool), initialFanToken);
            pool.setInitialLiquidity(initialBET, initialFanToken);

            // 로그 출력
             console.log(string.concat(token_name, " Token deployed at:"), address(fanToken));
             console.log(string.concat(token_name, " Pool deployed at:"), address(pool));
        }

        vm.stopBroadcast();
    }
}
