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

    address public user1 = address(1);

    string[10] public token_names = ["DSB", "LGT", "KIA", "HWE", "SSL", "SSG", "NCD", "LTG", "KWH", "KTW"];

    function setUp() public {
        btc = new Token("Betty Coin", "BTC", 1_000_000 * 1e18);
        exchange = new Exchange(address(btc));

        // 사용자에게 넉넉히 BTC 지급 (가스비 및 테스트 여유분 고려)
        btc.transfer(user1, 20_000 * 1e18);  // ✅ 20,000 BTC 지급

        for (uint i = 0; i < token_names.length; i++) {
            Token fanToken = new Token(token_names[i], token_names[i], 1_000_000 * 1e18);
            fanTokens[token_names[i]] = fanToken;

            LiquidityPool liquidityPool = new LiquidityPool(address(btc), address(fanToken));
            liquidityPools[token_names[i]] = liquidityPool;

            exchange.addFanToken(token_names[i], address(fanToken), address(liquidityPool));

            btc.transfer(address(liquidityPool), 10_000 * 1e18);
            fanToken.transfer(address(liquidityPool), 1_000 * 1e18);

            liquidityPool.setInitialLiquidity(10_000 * 1e18, 1_000 * 1e18);
        }
    }

    function testAll() public {
        vm.startPrank(user1);

        // ✅ BTC 입금 (충분한 여유분 추가, 2,000 BTC)
        btc.approve(address(exchange), 2_000 * 1e18);
        exchange.add(2_000 * 1e18);

        // ✅ DSB 팬토큰 매수 (정확히 100 BTC 사용)
        btc.approve(address(exchange), 100 * 1e18);
        exchange.buy("DSB", 100 * 1e18);

        // ✅ DSB 팬토큰 매도 (실제 보유한 약 9.9개 중 5개만 매도)
        fanTokens["DSB"].approve(address(exchange), 5 * 1e18);
        exchange.sell("DSB", 5 * 1e18);

        // ✅ DSB → LGT 팬토큰 스왑 (남은 약 4.9개 중 2개만 스왑)
        fanTokens["DSB"].approve(address(exchange), 2 * 1e18);
        exchange.swap("DSB", "LGT", 2 * 1e18);

        // ✅ BTC 일부 출금 (200 BTC)
        exchange.remove(200 * 1e18);

        vm.stopPrank();
    }
}
