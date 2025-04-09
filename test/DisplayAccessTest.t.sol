// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Test.sol";
import "../src/DisplayAccess.sol";
import "../src/Token.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

contract DisplayAccessTest is Test {
    DisplayAccess public displayAccess;
    Token public fanToken;
    
    // 테스트용 실제 지갑 주소
    address public user = 0x51F290AA31377bD59d722d09233FEb363c09bDc3;
    
    function setUp() public {
        // DisplayAccess 컨트랙트 배포
        displayAccess = new DisplayAccess();
        
        // 테스트용 팬토큰 배포 (DSB 팬토큰)
        fanToken = new Token("DSB", 1_000_000 * 1e18);
        
        // 테스트 유저에게 팬토큰 전송
        fanToken.transfer(user, 1_000 * 1e18); // 1,000 팬토큰 지급
        
        // Reward Pool 설정 (테스트를 위해 컨트랙트 자신을 rewardPool로 설정)
        displayAccess.setRewardPool(address(this));
        
        // 로그 출력
        console.log("DisplayAccess deployed at:", address(displayAccess));
        console.log("FanToken deployed at:", address(fanToken));
        console.log("User address:", user);
        console.log("Initial fan token balance:", fanToken.balanceOf(user) / 1e18);
    }

    function test_PurchaseAccess() public {
        vm.startPrank(user);

        // 초기 상태 확인
        uint256 initialBalance = fanToken.balanceOf(user);
        console.log("Initial token balance:", initialBalance / 1e18);
        
        // 토큰 소각 승인
        fanToken.approve(address(displayAccess), 1 * 1e18);
        console.log("Approved 1 token for burning");
        
        // 액세스 구매 (토큰 소각)
        displayAccess.purchaseAccess(address(fanToken), 1, 1);
        console.log("Purchased access for teamId: 1, gameId: 1");

        // 액세스 권한 확인
        bool hasAccess = displayAccess.checkAccess(1, 1, user);
        console.log("Has access after purchase:", hasAccess);
        
        // 잔액 확인 (소각되었으므로 1 감소)
        uint256 finalBalance = fanToken.balanceOf(user);
        console.log("Final token balance:", finalBalance / 1e18);
        
        // 총 공급량 확인 (소각으로 인해 감소)
        uint256 totalSupply = fanToken.totalSupply();
        console.log("Total supply after burn:", totalSupply / 1e18);

        // 검증
        assertTrue(hasAccess, "User should have access");
        assertEq(finalBalance, initialBalance - 1 * 1e18, "Token balance should decrease by 1");

        vm.stopPrank();
    }

    function test_RevertWhen_InsufficientBalance() public {
        address poorUser = address(0x1234);
        
        vm.startPrank(poorUser);
        
        // 승인
        fanToken.approve(address(displayAccess), 1 * 1e18);
        
        // 잔액 확인
        uint256 balance = fanToken.balanceOf(poorUser);
        console.log("Poor user balance:", balance);
        
        // 잔액이 없으므로 실패해야 함
        vm.expectRevert();  // 구체적인 에러 메시지 대신 모든 revert를 catch
        displayAccess.purchaseAccess(address(fanToken), 1, 1);
        
        vm.stopPrank();
    }

    function test_RevertWhen_DuplicatePurchase() public {
        vm.startPrank(user);
        
        // 첫 번째 구매를 위한 승인 및 구매
        fanToken.approve(address(displayAccess), 2 * 1e18);
        displayAccess.purchaseAccess(address(fanToken), 1, 1);
        console.log("First purchase successful");
        
        // 두 번째 구매 시도 (실패해야 함)
        vm.expectRevert("has access");
        displayAccess.purchaseAccess(address(fanToken), 1, 1);
        
        vm.stopPrank();
    }

    function test_MultipleTeamAccess() public {
        vm.startPrank(user);
        
        // 여러 팀에 대한 액세스 구매
        fanToken.approve(address(displayAccess), 3 * 1e18);
        
        // 팀1, 게임1 구매
        displayAccess.purchaseAccess(address(fanToken), 1, 1);
        console.log("Purchased access for team 1, game 1");
        
        // 팀2, 게임1 구매
        displayAccess.purchaseAccess(address(fanToken), 2, 1);
        console.log("Purchased access for team 2, game 1");
        
        // 팀1, 게임2 구매
        displayAccess.purchaseAccess(address(fanToken), 1, 2);
        console.log("Purchased access for team 1, game 2");
        
        // 액세스 권한 확인
        assertTrue(displayAccess.checkAccess(1, 1, user), "Should have access to team 1, game 1");
        assertTrue(displayAccess.checkAccess(2, 1, user), "Should have access to team 2, game 1");
        assertTrue(displayAccess.checkAccess(1, 2, user), "Should have access to team 1, game 2");
        assertFalse(displayAccess.checkAccess(2, 2, user), "Should not have access to team 2, game 2");
        
        vm.stopPrank();
    }
}