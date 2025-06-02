// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/WinningTeamVoting.sol";

contract WinningTeamVotingScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");

        vm.startBroadcast(privateKey);

        WinningTeamVoting bettingContract = new WinningTeamVoting();
        console.log("WinningTeamVoting deployed at:", address(bettingContract));

        vm.stopBroadcast();
    }
}

// 게임 등록 스크립트 (기존에 배포된 팬토큰 주소 사용)
contract RegisterGameScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        address teamATokenAddress = vm.envAddress("TEAM_A_TOKEN_ADDRESS");
        address teamBTokenAddress = vm.envAddress("TEAM_B_TOKEN_ADDRESS");
        address bettingContractAddress = vm.envAddress("BETTING_CONTRACT_ADDRESS");
        uint256 gameId = vm.envUint("GAME_ID");
        uint256 teamAId = vm.envUint("TEAM_A_ID");
        uint256 teamBId = vm.envUint("TEAM_B_ID");
        uint256 startTime = vm.envUint("START_TIME");
        
        require(teamATokenAddress != address(0), "Invalid Team A token address");
        require(teamBTokenAddress != address(0), "Invalid Team B token address");
        require(bettingContractAddress != address(0), "Invalid betting contract address");
        require(startTime > block.timestamp, "Start time must be in the future");

        vm.startBroadcast(privateKey);

        // 기존 베팅 컨트랙트에 게임 등록
        WinningTeamVoting bettingContract = WinningTeamVoting(bettingContractAddress);
        bettingContract.createGame(gameId, teamAId, teamBId, startTime, teamATokenAddress, teamBTokenAddress);
        
        console.log("Game registered:");
        console.log("- Game ID:", gameId);
        console.log("- Team A ID:", teamAId);
        console.log("- Team B ID:", teamBId);
        console.log("- Start Time:", startTime);
        console.log("- Team A Token:", teamATokenAddress);
        console.log("- Team B Token:", teamBTokenAddress);

        vm.stopBroadcast();
    }
}

// 게임 종료 스크립트 (백엔드에서 호출)
contract FinalizeGameScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        address bettingContractAddress = vm.envAddress("BETTING_CONTRACT_ADDRESS");
        uint256 gameId = vm.envUint("GAME_ID");
        uint256 winningTeamId = vm.envUint("WINNING_TEAM_ID");
        
        require(bettingContractAddress != address(0), "Invalid betting contract address");

        vm.startBroadcast(privateKey);

        // 게임 종료 및 승리팀 설정
        WinningTeamVoting bettingContract = WinningTeamVoting(bettingContractAddress);
        bettingContract.finalize(gameId, winningTeamId);
        
        console.log("Game finalized:");
        console.log("- Game ID:", gameId);
        console.log("- Winning Team ID:", winningTeamId);

        vm.stopBroadcast();
    }
}

// 사용자 대신 보상 청구 스크립트
contract ClaimForUserScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        address bettingContractAddress = vm.envAddress("BETTING_CONTRACT_ADDRESS");
        uint256 gameId = vm.envUint("GAME_ID");
        address userAddress = vm.envAddress("USER_ADDRESS");
        
        require(bettingContractAddress != address(0), "Invalid betting contract address");
        require(userAddress != address(0), "Invalid user address");

        vm.startBroadcast(privateKey);

        // 사용자 대신 보상 청구
        WinningTeamVoting bettingContract = WinningTeamVoting(bettingContractAddress);
        bettingContract.claimForUser(gameId, userAddress);
        
        console.log("Reward claimed for user:");
        console.log("- Game ID:", gameId);
        console.log("- User Address:", userAddress);

        vm.stopBroadcast();
    }
}

// 게임 토큰 주소 업데이트 스크립트
contract UpdateGameTokensScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        address bettingContractAddress = vm.envAddress("BETTING_CONTRACT_ADDRESS");
        uint256 gameId = vm.envUint("GAME_ID");
        address newTeamATokenAddress = vm.envAddress("NEW_TEAM_A_TOKEN_ADDRESS");
        address newTeamBTokenAddress = vm.envAddress("NEW_TEAM_B_TOKEN_ADDRESS");
        
        require(bettingContractAddress != address(0), "Invalid betting contract address");
        require(newTeamATokenAddress != address(0), "Invalid new Team A token address");
        require(newTeamBTokenAddress != address(0), "Invalid new Team B token address");

        vm.startBroadcast(privateKey);

        // 게임 토큰 주소 업데이트
        WinningTeamVoting bettingContract = WinningTeamVoting(bettingContractAddress);
        bettingContract.updateGameTokenAddresses(gameId, newTeamATokenAddress, newTeamBTokenAddress);
        
        console.log("Game token addresses updated:");
        console.log("- Game ID:", gameId);
        console.log("- New Team A Token:", newTeamATokenAddress);
        console.log("- New Team B Token:", newTeamBTokenAddress);

        vm.stopBroadcast();
    }
}

// 게임 베터 목록 조회 스크립트
contract GetGameBettorsScript is Script {
    function run() external view {
        address bettingContractAddress = vm.envAddress("BETTING_CONTRACT_ADDRESS");
        uint256 gameId = vm.envUint("GAME_ID");
        
        require(bettingContractAddress != address(0), "Invalid betting contract address");

        // 컨트랙트 인스턴스 생성
        WinningTeamVoting bettingContract = WinningTeamVoting(bettingContractAddress);
        
        // 전체 베터 목록 조회
        address[] memory allBettors = bettingContract.getAllGameBettors(gameId);
        console.log("All bettors for Game ID:", gameId);
        console.log("Total bettors count:", allBettors.length);
        for (uint i = 0; i < allBettors.length && i < 10; i++) { // 최대 10명까지만 출력
            console.log(i+1, ":", allBettors[i]);
        }
        if (allBettors.length > 10) {
            console.log("... and", allBettors.length - 10, "more bettors");
        }
        
        // 게임이 종료된 경우 승리팀/패배팀 베터 목록 조회
        (,,,,,,bool finalized,,,) = bettingContract.games(gameId);
        if (finalized) {
            try bettingContract.getWinningTeamBettors(gameId) returns (address[] memory winningBettors) {
                console.log("Winning team bettors count:", winningBettors.length);
                for (uint i = 0; i < winningBettors.length && i < 10; i++) {
                    console.log(i+1, ":", winningBettors[i]);
                }
                if (winningBettors.length > 10) {
                    console.log("... and", winningBettors.length - 10, "more winning bettors");
                }
            } catch {
                console.log("Failed to get winning team bettors");
            }
            
            try bettingContract.getLosingTeamBettors(gameId) returns (address[] memory losingBettors) {
                console.log("Losing team bettors count:", losingBettors.length);
                for (uint i = 0; i < losingBettors.length && i < 10; i++) {
                    console.log(i+1, ":", losingBettors[i]);
                }
                if (losingBettors.length > 10) {
                    console.log("... and", losingBettors.length - 10, "more losing bettors");
                }
            } catch {
                console.log("Failed to get losing team bettors");
            }
        } else {
            console.log("Game not finalized yet. Cannot get winning/losing team bettors.");
        }
    }
}