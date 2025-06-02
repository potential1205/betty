// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "forge-std/Test.sol";
import "../src/Token.sol";
import "../src/Exchange.sol";
import "../src/LiquidityPool.sol";

contract ExchangeTest is Test {
    Token public btc;
    Exchange public exchange;
    mapping(string => Token) public fanTokens;
    mapping(string => LiquidityPool) public liquidityPools;

    // 테스트용 실제 지갑 주소로 변경
    address public user1 = 0x51F290AA31377bD59d722d09233FEb363c09bDc3;

    string[10] public token_names = ["DSB", "LGT", "KIA", "HWE", "SSL", "SSG", "NCD", "LTG", "KWH", "KTW"];

    function setUp() public {
        btc = new Token("BTC", 1_000_000_000 * 1e18); // 총량만 1000배
        exchange = new Exchange(address(btc));

        // 실제 테스트 유저에게 충분한 BETTY 지급
        btc.transfer(user1, 20_000 * 1e18); // BETTY 잔고: 20,000

        for (uint i = 0; i < token_names.length; i++) {
            string memory token_name = token_names[i];
            Token fanToken = new Token(token_name, 1_000_000_000 * 1e18); // 총량만 1000배
            LiquidityPool pool = new LiquidityPool(address(btc), address(fanToken));

            fanTokens[token_name] = fanToken;
            liquidityPools[token_name] = pool;

            exchange.addFanToken(token_name, address(fanToken), address(pool));

            // 유동성 풀에 초기 자금 공급: 10,000 BETTY / 1,000 팬토큰
            btc.transfer(address(pool), 10_000 * 1e18);
            fanToken.transfer(address(pool), 1_000 * 1e18);
            pool.setInitialLiquidity(10_000 * 1e18, 1_000 * 1e18);
        }
    }

    function testAll() public {
        vm.startPrank(user1);

        // BETTY 예치
        btc.approve(address(exchange), 2_000 * 1e18);
        exchange.add(2_000 * 1e18);

        // 팬토큰 구매
        btc.approve(address(exchange), 100 * 1e18);
        exchange.buy("DSB", 100 * 1e18);

        // 팬토큰 판매
        fanTokens["DSB"].approve(address(exchange), 5 * 1e18);
        exchange.sell("DSB", 5 * 1e18);

        // 팬토큰 스왑
        fanTokens["DSB"].approve(address(exchange), 2 * 1e18);
        exchange.swap("DSB", "LGT", 2 * 1e18);

        // BETTY 출금
        exchange.remove(200 * 1e18);

        vm.stopPrank();
    }

    // 팬토큰 사용 (소각) 테스트
    function testUseFanToken() public {
        vm.startPrank(user1);

        // BETTY → DSB 구매
        btc.approve(address(exchange), 100 * 1e18);
        exchange.buy("DSB", 100 * 1e18);

        // DSB 토큰 approve
        fanTokens["DSB"].approve(address(exchange), 10 * 1e18);

        // 팬토큰 사용 (소각)
        exchange.use("DSB", 2 * 1e18);

        // 잔여 DSB 로그 출력
        uint256 remaining = fanTokens["DSB"].balanceOf(user1);
        console.log("Remaining DSB Balance (after burn):", remaining / 1e18, "DSB");

        vm.stopPrank();
    }
}
