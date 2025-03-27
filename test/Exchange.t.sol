// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "forge-std/Test.sol";

import "src/Token.sol";
import "src/Exchange.sol";
import "src/LiquidityPool.sol";

contract ExchangeTest is Test {
    Token btcToken;
    Token fanTokenA;
    Token fanTokenB;
    Exchange exchange;
    LiquidityPool poolA;
    LiquidityPool poolB;

    address user1 = address(1);

    function setUp() public {
        btcToken = new Token("Betty Coin", "BTC", 1000000);
        fanTokenA = new Token("Team A Token", "DSB", 1000000);
        fanTokenB = new Token("Team B Token", "LGT", 1000000);

        poolA = new LiquidityPool(address(btcToken), address(fanTokenA));
        poolB = new LiquidityPool(address(btcToken), address(fanTokenB));

        exchange = new Exchange(address(btcToken));
        
        exchange.addFanToken("DSB", address(fanTokenA), address(poolA));
        exchange.addFanToken("LGT", address(fanTokenB), address(poolB));

        btcToken.transfer(user1, 1000 * 10**18);

        vm.startPrank(user1);
        btcToken.approve(address(exchange), type(uint256).max);
        fanTokenA.approve(address(exchange), type(uint256).max);
        fanTokenB.approve(address(exchange), type(uint256).max);
        vm.stopPrank();
    }

    function testAdd() public {
        vm.startPrank(user1);
        exchange.add{value: 1 ether}(1 ether);
        vm.stopPrank();

        assertEq(btcToken.balanceOf(user1), 1 ether);
    }

    function testRemove() public {
        vm.startPrank(user1);
        exchange.add{value: 1 ether}(1 ether);
        exchange.remove(1 ether);
        vm.stopPrank();

        assertEq(address(user1).balance, 10 ether);
    }

    function testBuy() public {
        vm.startPrank(user1);
        exchange.add{value: 1 ether}(1 ether);
        exchange.buy("DSB", 1 ether);
        vm.stopPrank();

        assertGt(fanTokenA.balanceOf(user1), 0);
    }

    function testSell() public {
        vm.startPrank(user1);
        exchange.add{value: 1 ether}(1 ether);
        exchange.buy("DSB", 1 ether);
        exchange.sell("DSB", 1 ether);
        vm.stopPrank();

        assertEq(btcToken.balanceOf(user1), 1 ether);
    }
}
