// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import {Test, console} from "forge-std/Test.sol";
import {WinningTeamVoting} from "../src/WinningTeamVoting.sol";
import "../src/Token.sol";

contract WinningTeamVotingTest is Test {
    WinningTeamVoting public bettingContract;
    Token public teamAToken;
    Token public teamBToken;
    
    // 테스트용 주소 
    address public admin = address(1);
    address public user1 = address(2);
    address public user2 = address(3);
    address public user3 = address(4);
    
    // 테스트용 상수
    uint256 public constant GAME_ID = 1;
    uint256 public constant TEAM_A_ID = 100;
    uint256 public constant TEAM_B_ID = 200;
    uint256 public constant INITIAL_SUPPLY = 1000000;

    function setUp() public {
        // 관리자 주소로 설정
        vm.startPrank(admin);
        
        // 테스트용 팀별 팬토큰 배포
        teamAToken = new Token("Team A FanToken", INITIAL_SUPPLY);
        teamBToken = new Token("Team B FanToken", INITIAL_SUPPLY);
        
        // 베팅 컨트랙트 배포
        bettingContract = new WinningTeamVoting();
        
        // 각 사용자에게 두 종류의 팬토큰 전송
        teamAToken.transfer(user1, 1000 * 10**18);
        teamAToken.transfer(user2, 1000 * 10**18);
        teamAToken.transfer(user3, 1000 * 10**18);
        
        teamBToken.transfer(user1, 1000 * 10**18);
        teamBToken.transfer(user2, 1000 * 10**18);
        teamBToken.transfer(user3, 1000 * 10**18);
        
        vm.stopPrank();
    }

    function testCreateGame() public {
        vm.startPrank(admin);
        
        // 현재 시간 가져오기
        uint256 currentTime = block.timestamp;
        uint256 startTime = currentTime + 1 hours;
        
        // 게임 생성
        bettingContract.createGame(GAME_ID, TEAM_A_ID, TEAM_B_ID, startTime, address(teamAToken), address(teamBToken));
        
        // 게임 정보 검증
        (uint256 gameId, uint256 teamAId, uint256 teamBId, uint256 gameStartTime, , , bool finalized, uint256 winningTeamId, address tokenAddressA, address tokenAddressB) = 
            bettingContract.games(GAME_ID);
        
        assertEq(gameId, GAME_ID);
        assertEq(teamAId, TEAM_A_ID);
        assertEq(teamBId, TEAM_B_ID);
        assertEq(gameStartTime, startTime);
        assertEq(finalized, false);
        assertEq(winningTeamId, 0);
        assertEq(tokenAddressA, address(teamAToken));
        assertEq(tokenAddressB, address(teamBToken));
        
        vm.stopPrank();
    }

    function testPlaceBet() public {
        // 게임 생성
        vm.startPrank(admin);
        uint256 startTime = block.timestamp + 1 hours;
        bettingContract.createGame(GAME_ID, TEAM_A_ID, TEAM_B_ID, startTime, address(teamAToken), address(teamBToken));
        vm.stopPrank();
        
        // 유저1이 팀A에 베팅
        vm.startPrank(user1);
        uint256 betAmount1 = 100 * 10**18;
        teamAToken.approve(address(bettingContract), betAmount1);
        bettingContract.placeBet(GAME_ID, TEAM_A_ID, betAmount1);
        vm.stopPrank();
        
        // 유저2가 팀B에 베팅
        vm.startPrank(user2);
        uint256 betAmount2 = 200 * 10**18;
        teamBToken.approve(address(bettingContract), betAmount2);
        bettingContract.placeBet(GAME_ID, TEAM_B_ID, betAmount2);
        vm.stopPrank();
        
        // 베팅 정보 검증
        (uint256 amount1, uint256 teamId1, bool claimed1) = bettingContract.getUserBet(GAME_ID, user1);
        assertEq(amount1, betAmount1);
        assertEq(teamId1, TEAM_A_ID);
        assertEq(claimed1, false);
        
        (uint256 amount2, uint256 teamId2, bool claimed2) = bettingContract.getUserBet(GAME_ID, user2);
        assertEq(amount2, betAmount2);
        assertEq(teamId2, TEAM_B_ID);
        assertEq(claimed2, false);
        
        // 팀별 베팅 총액 검증
        assertEq(bettingContract.getTeamBets(GAME_ID, TEAM_A_ID), betAmount1);
        assertEq(bettingContract.getTeamBets(GAME_ID, TEAM_B_ID), betAmount2);
    }

    function testBettingClosed() public {
        // 게임 생성
        vm.startPrank(admin);
        uint256 startTime = block.timestamp + 1 hours;
        bettingContract.createGame(GAME_ID, TEAM_A_ID, TEAM_B_ID, startTime, address(teamAToken), address(teamBToken));
        vm.stopPrank();
        
        // 시간을 베팅 마감 이후로 설정
        vm.warp(startTime + 1);
        
        // 유저1이 팀A에 베팅 시도 (실패해야 함)
        vm.startPrank(user1);
        uint256 betAmount = 100 * 10**18;
        teamAToken.approve(address(bettingContract), betAmount);
        
        vm.expectRevert("Betting closed");
        bettingContract.placeBet(GAME_ID, TEAM_A_ID, betAmount);
        
        vm.stopPrank();
    }

    function testFinalize() public {
        // 게임 생성 및 베팅
        vm.startPrank(admin);
        uint256 startTime = block.timestamp + 1 hours;
        bettingContract.createGame(GAME_ID, TEAM_A_ID, TEAM_B_ID, startTime, address(teamAToken), address(teamBToken));
        vm.stopPrank();
        
        // 유저1이 팀A에 베팅
        vm.startPrank(user1);
        uint256 betAmount1 = 100 * 10**18;
        teamAToken.approve(address(bettingContract), betAmount1);
        bettingContract.placeBet(GAME_ID, TEAM_A_ID, betAmount1);
        vm.stopPrank();
        
        // 유저2가 팀B에 베팅
        vm.startPrank(user2);
        uint256 betAmount2 = 200 * 10**18;
        teamBToken.approve(address(bettingContract), betAmount2);
        bettingContract.placeBet(GAME_ID, TEAM_B_ID, betAmount2);
        vm.stopPrank();
        
        // 게임 종료 및 팀A를 승리팀으로 설정
        vm.startPrank(admin);
        bettingContract.finalize(GAME_ID, TEAM_A_ID);
        vm.stopPrank();
        
        // 게임 종료 상태 검증
        (, , , , , , bool finalized, uint256 winningTeamId, , ) = bettingContract.games(GAME_ID);
        assertEq(finalized, true);
        assertEq(winningTeamId, TEAM_A_ID);
    }

    function testRewardClaim() public {
        // 게임 생성 및 베팅
        vm.startPrank(admin);
        uint256 startTime = block.timestamp + 1 hours;
        bettingContract.createGame(GAME_ID, TEAM_A_ID, TEAM_B_ID, startTime, address(teamAToken), address(teamBToken));
        vm.stopPrank();
        
        // 유저1이 팀A에 베팅
        vm.startPrank(user1);
        uint256 betAmount1 = 100 * 10**18;
        teamAToken.approve(address(bettingContract), betAmount1);
        bettingContract.placeBet(GAME_ID, TEAM_A_ID, betAmount1);
        vm.stopPrank();
        
        // 유저2가 팀B에 베팅
        vm.startPrank(user2);
        uint256 betAmount2 = 200 * 10**18;
        teamBToken.approve(address(bettingContract), betAmount2);
        bettingContract.placeBet(GAME_ID, TEAM_B_ID, betAmount2);
        vm.stopPrank();
        
        // 게임 종료 및 팀A를 승리팀으로 설정
        vm.startPrank(admin);
        bettingContract.finalize(GAME_ID, TEAM_A_ID);
        vm.stopPrank();
        
        // 유저1의 보상 청구 전 잔액 기록
        uint256 user1TeamABalanceBefore = teamAToken.balanceOf(user1);
        uint256 user1TeamBBalanceBefore = teamBToken.balanceOf(user1);
        
        // 유저1이 보상 청구
        vm.startPrank(user1);
        bettingContract.claimReward(GAME_ID);
        vm.stopPrank();
        
        // 유저1의 보상 청구 후 잔액 및 claimed 상태 검증
        uint256 user1TeamABalanceAfter = teamAToken.balanceOf(user1);
        uint256 user1TeamBBalanceAfter = teamBToken.balanceOf(user1);
        (,,bool claimed1) = bettingContract.getUserBet(GAME_ID, user1);
        
        // 예상 보상 금액 계산
        // 1. 원금은 원래 자신이 배팅한 팀A 토큰으로 반환
        uint256 expectedTeamAReturn = betAmount1; // 원금
        
        // 2. 보너스는 패배팀 토큰으로 지급 (betAmount2의 전액)
        uint256 expectedTeamBBonus = betAmount2; // 패배팀 토큰 전액
        
        assertEq(claimed1, true);
        assertEq(user1TeamABalanceAfter - user1TeamABalanceBefore, expectedTeamAReturn); // 원금 반환
        assertEq(user1TeamBBalanceAfter - user1TeamBBalanceBefore, expectedTeamBBonus); // 패배팀 토큰으로 보상
    }

    function testClaimForUser() public {
        // 게임 생성 및 베팅
        vm.startPrank(admin);
        uint256 startTime = block.timestamp + 1 hours;
        bettingContract.createGame(GAME_ID, TEAM_A_ID, TEAM_B_ID, startTime, address(teamAToken), address(teamBToken));
        vm.stopPrank();
        
        // 유저1이 팀A에 베팅
        vm.startPrank(user1);
        uint256 betAmount1 = 100 * 10**18;
        teamAToken.approve(address(bettingContract), betAmount1);
        bettingContract.placeBet(GAME_ID, TEAM_A_ID, betAmount1);
        vm.stopPrank();
        
        // 유저2가 팀B에 베팅
        vm.startPrank(user2);
        uint256 betAmount2 = 200 * 10**18;
        teamBToken.approve(address(bettingContract), betAmount2);
        bettingContract.placeBet(GAME_ID, TEAM_B_ID, betAmount2);
        vm.stopPrank();
        
        // 게임 종료 및 팀A를 승리팀으로 설정
        vm.startPrank(admin);
        bettingContract.finalize(GAME_ID, TEAM_A_ID);
        
        // 유저1의 보상 청구 전 잔액 기록
        uint256 user1TeamABalanceBefore = teamAToken.balanceOf(user1);
        uint256 user1TeamBBalanceBefore = teamBToken.balanceOf(user1);
        
        // 관리자가 유저1 대신 보상 청구
        bettingContract.claimForUser(GAME_ID, user1);
        vm.stopPrank();
        
        // 유저1의 보상 청구 후 잔액 및 claimed 상태 검증
        uint256 user1TeamABalanceAfter = teamAToken.balanceOf(user1);
        uint256 user1TeamBBalanceAfter = teamBToken.balanceOf(user1);
        (,,bool claimed1) = bettingContract.getUserBet(GAME_ID, user1);
        
        // 예상 보상 금액 계산
        uint256 expectedTeamAReturn = betAmount1; // 원금
        uint256 expectedTeamBBonus = betAmount2; // 패배팀 토큰 전액
        
        assertEq(claimed1, true);
        assertEq(user1TeamABalanceAfter - user1TeamABalanceBefore, expectedTeamAReturn); // 원금 반환
        assertEq(user1TeamBBalanceAfter - user1TeamBBalanceBefore, expectedTeamBBonus); // 패배팀 토큰으로 보상
    }
    
    function testGetBettorsList() public {
        // 게임 생성
        vm.startPrank(admin);
        uint256 startTime = block.timestamp + 1 hours;
        bettingContract.createGame(GAME_ID, TEAM_A_ID, TEAM_B_ID, startTime, address(teamAToken), address(teamBToken));
        vm.stopPrank();
        
        // 초기 베터 목록 검증: 비어있어야 함
        address[] memory initialBettors = bettingContract.getAllGameBettors(GAME_ID);
        assertEq(initialBettors.length, 0);
        
        // 유저1이 팀A에 베팅
        vm.startPrank(user1);
        uint256 betAmount1 = 100 * 10**18;
        teamAToken.approve(address(bettingContract), betAmount1);
        bettingContract.placeBet(GAME_ID, TEAM_A_ID, betAmount1);
        vm.stopPrank();
        
        // 베터 목록 검증: 유저1만 있어야 함
        address[] memory bettorsAfterUser1 = bettingContract.getAllGameBettors(GAME_ID);
        assertEq(bettorsAfterUser1.length, 1);
        assertEq(bettorsAfterUser1[0], user1);
        
        // 팀별 베터 목록 검증
        address[] memory teamABettors = bettingContract.getTeamBettors(GAME_ID, TEAM_A_ID);
        assertEq(teamABettors.length, 1);
        assertEq(teamABettors[0], user1);
        
        address[] memory teamBBettors = bettingContract.getTeamBettors(GAME_ID, TEAM_B_ID);
        assertEq(teamBBettors.length, 0);
        
        // 유저2가 팀B에 베팅
        vm.startPrank(user2);
        uint256 betAmount2 = 200 * 10**18;
        teamBToken.approve(address(bettingContract), betAmount2);
        bettingContract.placeBet(GAME_ID, TEAM_B_ID, betAmount2);
        vm.stopPrank();
        
        // 베터 목록 검증: 유저1, 유저2가 있어야 함
        address[] memory bettorsAfterUser2 = bettingContract.getAllGameBettors(GAME_ID);
        assertEq(bettorsAfterUser2.length, 2);
        assertEq(bettorsAfterUser2[0], user1);
        assertEq(bettorsAfterUser2[1], user2);
        
        // 팀별 베터 목록 검증
        teamABettors = bettingContract.getTeamBettors(GAME_ID, TEAM_A_ID);
        assertEq(teamABettors.length, 1);
        assertEq(teamABettors[0], user1);
        
        teamBBettors = bettingContract.getTeamBettors(GAME_ID, TEAM_B_ID);
        assertEq(teamBBettors.length, 1);
        assertEq(teamBBettors[0], user2);
        
        // 유저3도 팀A에 베팅
        vm.startPrank(user3);
        uint256 betAmount3 = 300 * 10**18;
        teamAToken.approve(address(bettingContract), betAmount3);
        bettingContract.placeBet(GAME_ID, TEAM_A_ID, betAmount3);
        vm.stopPrank();
        
        // 모든 베터 목록 검증
        address[] memory allBettors = bettingContract.getAllGameBettors(GAME_ID);
        assertEq(allBettors.length, 3);
        assertEq(allBettors[0], user1);
        assertEq(allBettors[1], user2);
        assertEq(allBettors[2], user3);
        
        // 게임 종료 및 팀A를 승리팀으로 설정
        vm.startPrank(admin);
        bettingContract.finalize(GAME_ID, TEAM_A_ID);
        vm.stopPrank();
        
        // 승리팀/패배팀 베터 목록 검증
        address[] memory winningBettors = bettingContract.getWinningTeamBettors(GAME_ID);
        assertEq(winningBettors.length, 2);
        assertEq(winningBettors[0], user1);
        assertEq(winningBettors[1], user3);
        
        address[] memory losingBettors = bettingContract.getLosingTeamBettors(GAME_ID);
        assertEq(losingBettors.length, 1);
        assertEq(losingBettors[0], user2);
    }
}