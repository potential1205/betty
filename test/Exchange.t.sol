// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "forge-std/Test.sol";
import "../src/Token.sol";
import "../src/Exchange.sol";

contract ExchangeTest is Test {
    Token token;
    Exchange exchange;

    address user1 = address(1);
    address user2 = address(2);

    function setUp() public {
        token = new Token();
        exchange = new Exchange(address(token));

        // 유저에게 토큰 전송
        token.transfer(user1, 1000 * 10**18);
        token.transfer(user2, 1000 * 10**18);

        // approve 추가 (테스트 실패 원인 수정)
        vm.startPrank(user1);

        vm.deal(user1, 10 ether);

        token.approve(address(exchange), 500 * 10**18);
        vm.stopPrank();
    }

    function testAddLiquidity() public {
        vm.startPrank(user1);
        exchange.addLiquidity{value: 1 ether}(500 * 10**18);
        vm.stopPrank();

        uint256 lpBalance = exchange.balanceOf(user1);
        assertEq(lpBalance, 1 ether);
    }

    function testSwapEthToToken() public {
        // 먼저 유동성을 공급해야 함
        vm.startPrank(user1);
        exchange.addLiquidity{value: 1 ether}(500 * 10**18);
        uint256 tokenReserveBefore = exchange.getReserve();
        console.log("Token Reserve Before Swap:", tokenReserveBefore);

        vm.stopPrank();

        // 이후에 스왑 실행
        vm.startPrank(user1);
        exchange.ethToTokenSwap{value: 1 ether}(0);
        vm.stopPrank();

        uint256 tokenBalanceAfter = token.balanceOf(user1);
        console.log("User1 Token Balance After Swap:", tokenBalanceAfter);

        assertGt(tokenBalanceAfter, 740000000000000000000);
    }
}
