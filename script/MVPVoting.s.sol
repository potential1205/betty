// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/MVPVoting.sol";

contract MVPVotingScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");

        vm.startBroadcast(privateKey);

        MVPVoting mvpContract = new MVPVoting();
        console.log("MVPVoting deployed at:", address(mvpContract));

        vm.stopBroadcast();
    }
}

// 게임 등록 스크립트
contract RegisterMVPGameScript is Script {
    MVPVoting mvpContract;
    uint256 gameId;
    uint256 startTime;
    uint256 playerCount;
    uint256 teamAPlayerCount;
    address teamATokenAddress;
    address teamBTokenAddress;
    uint256[] playerIds;
    address[] tokenAddresses;

    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        address mvpContractAddress = vm.envAddress("MVP_CONTRACT_ADDRESS");
        gameId = vm.envUint("GAME_ID");
        startTime = vm.envUint("START_TIME");
        playerCount = vm.envOr("PLAYER_COUNT", uint256(20)); // 기본값 20
        
        require(mvpContractAddress != address(0), "Invalid MVP contract address");
        require(startTime > block.timestamp, "Start time must be in the future");

        // 선수 ID 배열과 해당 팬토큰 주소 배열 생성
        playerIds = new uint256[](playerCount);
        tokenAddresses = new address[](playerCount);
        
        // 환경변수에서 팀 A/B 토큰 주소 가져오기
        teamATokenAddress = vm.envAddress("TEAM_A_TOKEN_ADDRESS");
        teamBTokenAddress = vm.envAddress("TEAM_B_TOKEN_ADDRESS");
        
        require(teamATokenAddress != address(0), "Invalid Team A token address");
        require(teamBTokenAddress != address(0), "Invalid Team B token address");
        
        // 환경변수에서 팀 A의 선수 수 가져오기 (기본값 10)
        teamAPlayerCount = vm.envOr("TEAM_A_PLAYER_COUNT", uint256(10));
        
        // 선수 ID와 토큰 주소 설정
        setupPlayerIds();

        vm.startBroadcast(privateKey);

        // MVP 게임 등록
        mvpContract = MVPVoting(mvpContractAddress);
        mvpContract.createMVPGame(gameId, playerIds, tokenAddresses, startTime);
        
        // 상세 로그 출력
        logGameDetails();

        vm.stopBroadcast();
    }
    
    // 선수 ID와 토큰 주소 설정 함수로 분리
    function setupPlayerIds() internal {
        // 선수 ID 환경변수가 설정되어 있는지 확인
        bool hasPlayerIdEnv = false;
        try vm.envUint("PLAYER_1_ID") { hasPlayerIdEnv = true; } catch {}
        
        if (hasPlayerIdEnv) {
            setupCustomPlayerIds();
        } else {
            setupDefaultPlayerIds();
        }
    }
    
    // 환경변수로부터 커스텀 선수 ID 설정
    function setupCustomPlayerIds() internal {
        for (uint i = 0; i < playerCount; i++) {
            string memory playerIdKey = string(abi.encodePacked("PLAYER_", vm.toString(i + 1), "_ID"));
            string memory playerTokenKey = string(abi.encodePacked("PLAYER_", vm.toString(i + 1), "_TOKEN"));
            
            try vm.envUint(playerIdKey) returns (uint256 id) {
                playerIds[i] = id;
            } catch {
                playerIds[i] = i + 1; // 기본값 - 인덱스 + 1
            }
            
            try vm.envAddress(playerTokenKey) returns (address tokenAddr) {
                tokenAddresses[i] = tokenAddr;
            } catch {
                // 기본값 - A팀 선수는 teamATokenAddress, B팀 선수는 teamBTokenAddress
                tokenAddresses[i] = i < teamAPlayerCount ? teamATokenAddress : teamBTokenAddress;
            }
        }
    }
    
    // 기본 선수 ID 설정
    function setupDefaultPlayerIds() internal {
        for (uint i = 0; i < playerCount; i++) {
            // 선수 ID는 1부터 시작
            playerIds[i] = i + 1;
            
            // 팀 A 선수는 팀 A 토큰, 팀 B 선수는 팀 B 토큰 사용
            if (i < teamAPlayerCount) {
                tokenAddresses[i] = teamATokenAddress;
            } else {
                tokenAddresses[i] = teamBTokenAddress;
            }
        }
    }
    
    // 게임 등록 상세 로그 출력
    function logGameDetails() internal view {
        console.log("MVP Game registered with ID:", gameId);
        console.log("Start time:", startTime);
        console.log("Player count:", playerCount);
        console.log("Team A token:", teamATokenAddress);
        console.log("Team B token:", teamBTokenAddress);
        
        // 선수별 팬토큰 매핑 정보 출력 (최대 5명만)
        console.log("Player-Token mapping sample:");
        uint256 logCount = playerCount < 5 ? playerCount : 5;
        for (uint i = 0; i < logCount; i++) {
            console.log("- Player", playerIds[i], "->", tokenAddresses[i]);
        }
        if (playerCount > 5) {
            console.log("... and", playerCount - 5, "more players");
        }
    }
}

// MVP 게임 종료 스크립트
contract FinalizeMVPGameScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        address mvpContractAddress = vm.envAddress("MVP_CONTRACT_ADDRESS");
        uint256 gameId = vm.envUint("GAME_ID");
        uint256 winningPlayerId = vm.envUint("WINNING_PLAYER_ID");
        
        require(mvpContractAddress != address(0), "Invalid MVP contract address");

        vm.startBroadcast(privateKey);

        // 게임 종료 및 MVP 선수 설정
        MVPVoting mvpContract = MVPVoting(mvpContractAddress);
        mvpContract.finalizeMVP(gameId, winningPlayerId);
        
        console.log("MVP Game finalized:");
        console.log("- Game ID:", gameId);
        console.log("- Winning Player ID:", winningPlayerId);

        vm.stopBroadcast();
    }
}

// 사용자 대신 보상 청구 스크립트
contract ClaimMVPRewardForUserScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        address mvpContractAddress = vm.envAddress("MVP_CONTRACT_ADDRESS");
        uint256 gameId = vm.envUint("GAME_ID");
        address userAddress = vm.envAddress("USER_ADDRESS");
        
        require(mvpContractAddress != address(0), "Invalid MVP contract address");
        require(userAddress != address(0), "Invalid user address");

        vm.startBroadcast(privateKey);

        // 사용자 대신 보상 청구
        MVPVoting mvpContract = MVPVoting(mvpContractAddress);
        mvpContract.claimMVPRewardForUser(gameId, userAddress);
        
        console.log("MVP Reward claimed for user:");
        console.log("- Game ID:", gameId);
        console.log("- User Address:", userAddress);

        vm.stopBroadcast();
    }
}

// 게임 투표자 목록 조회 스크립트
contract GetMVPGameVotersScript is Script {
    MVPVoting mvpContract;
    uint256 gameId;
    uint256 maxDisplayVoters;
    bool showFullAddresses;

    function run() external {
        address mvpContractAddress = vm.envAddress("MVP_CONTRACT_ADDRESS");
        gameId = vm.envUint("GAME_ID");
        maxDisplayVoters = vm.envOr("MAX_DISPLAY_VOTERS", uint256(10));
        showFullAddresses = vm.envOr("SHOW_FULL_ADDRESSES", false);
        
        require(mvpContractAddress != address(0), "Invalid MVP contract address");

        // 컨트랙트 인스턴스 생성
        mvpContract = MVPVoting(mvpContractAddress);
        
        // 게임 정보 확인
        (uint256 gameIdRes, bool finalized, uint256 winningPlayerId, uint256 startTime, uint256 totalVoteAmount, uint256 totalWinnerVoteAmount) = mvpContract.games(gameId);
        require(gameIdRes != 0, "Game does not exist");
        
        console.log("=== MVP Game Voters Report ===");
        console.log("Game ID:", gameId);
        console.log("Game Status:", finalized ? "Finalized" : "Active");
        if (finalized) {
            console.log("Winning Player ID:", winningPlayerId);
            console.log("Total Winner Vote Amount:", totalWinnerVoteAmount);
        }
        console.log("Total Vote Amount:", totalVoteAmount);
        console.log("Voting Deadline:", startTime);
        if (block.timestamp < startTime) {
            console.log("Time Remaining:", startTime - block.timestamp, "seconds");
        } else {
            console.log("Voting Closed:", block.timestamp - startTime, "seconds ago");
        }
        
        // 전체 투표자 목록 처리
        processAllVoters();
        
        // 게임이 종료된 경우 정답자/오답자 목록 조회
        if (finalized) {
            processWinningVoters();
            processLosingVoters();
        } else {
            console.log("\nGame not finalized yet. Cannot get winning/losing voters analysis.");
        }
    }
    
    // 전체 투표자 목록 처리
    function processAllVoters() internal {
        address[] memory allVoters = mvpContract.getAllGameVoters(gameId);
        console.log("\n=== All Voters ===");
        console.log("Total Count:", allVoters.length);
        
        uint256 displayCount = allVoters.length < maxDisplayVoters ? allVoters.length : maxDisplayVoters;
        for (uint i = 0; i < displayCount; i++) {
            displayVoterInfo(i + 1, allVoters[i]);
        }
        
        if (allVoters.length > maxDisplayVoters) {
            console.log("... and", allVoters.length - maxDisplayVoters, "more voters");
        }
    }
    
    // 정답자 목록 처리
    function processWinningVoters() internal {
        console.log("\n=== Winner Analysis ===");
        
        try mvpContract.getWinningVoters(gameId) returns (address[] memory winningVoters) {
            console.log("\nWinning Voters Count:", winningVoters.length);
            
            if (winningVoters.length > 0) {
                address[] memory allVoters = mvpContract.getAllGameVoters(gameId);
                console.log("Win Rate:", (winningVoters.length * 100) / allVoters.length, "%");
                
                uint256 displayCount = winningVoters.length < maxDisplayVoters ? winningVoters.length : maxDisplayVoters;
                for (uint i = 0; i < displayCount; i++) {
                    displayVoterInfo(i + 1, winningVoters[i]);
                }
                
                if (winningVoters.length > maxDisplayVoters) {
                    console.log("... and", winningVoters.length - maxDisplayVoters, "more winning voters");
                }
            }
        } catch {
            console.log("Failed to get winning voters");
        }
    }
    
    // 오답자 목록 처리
    function processLosingVoters() internal {
        console.log("\n=== Loser Analysis ===");
        
        try mvpContract.getLosingVoters(gameId) returns (address[] memory losingVoters) {
            console.log("\nLosing Voters Count:", losingVoters.length);
            
            if (losingVoters.length > 0) {
                address[] memory allVoters = mvpContract.getAllGameVoters(gameId);
                console.log("Loss Rate:", (losingVoters.length * 100) / allVoters.length, "%");
                
                uint256 displayCount = losingVoters.length < maxDisplayVoters ? losingVoters.length : maxDisplayVoters;
                for (uint i = 0; i < displayCount; i++) {
                    displayVoterInfo(i + 1, losingVoters[i]);
                }
                
                if (losingVoters.length > maxDisplayVoters) {
                    console.log("... and", losingVoters.length - maxDisplayVoters, "more losing voters");
                }
            }
        } catch {
            console.log("Failed to get losing voters");
        }
    }
    
    // 투표자 정보 표시 함수
    function displayVoterInfo(uint256 index, address voter) internal {
        (uint256 amount, uint256 playerId, address tokenAddress, bool claimed) = mvpContract.getUserVote(gameId, voter);
        
        if (showFullAddresses) {
            console.log(index, ":", voter);
        } else {
            // 주소 요약해서 보여주기 (처음 6자와 마지막 4자)
            string memory shortAddr = string(abi.encodePacked(
                vm.toString(uint96(uint160(voter) >> 96)), 
                "...", 
                vm.toString(uint32(uint160(voter)))
            ));
            console.log(index, ":", shortAddr);
        }
        
        console.log("   - Player ID:", playerId);
        console.log("   - Vote Amount:", amount);
        console.log("   - Token:", tokenAddress);
        console.log("   - Claimed:", claimed);
    }
}

// 선수별 투표 현황 조회 스크립트
contract GetPlayerVotesScript is Script {
    MVPVoting mvpContract;
    uint256 gameId;
    uint256 playerCount;
    uint256 teamAPlayerCount;
    uint256 playerStartId;
    bool showZeroVotes;
    uint256 totalVotes;
    uint256 playersWithVotes;

    function run() external {
        address mvpContractAddress = vm.envAddress("MVP_CONTRACT_ADDRESS");
        gameId = vm.envUint("GAME_ID");
        playerCount = vm.envOr("PLAYER_COUNT", uint256(20));
        teamAPlayerCount = vm.envOr("TEAM_A_PLAYER_COUNT", uint256(10));
        playerStartId = vm.envOr("PLAYER_START_ID", uint256(1));
        showZeroVotes = vm.envOr("SHOW_ZERO_VOTES", false);
        
        require(mvpContractAddress != address(0), "Invalid MVP contract address");

        // 컨트랙트 인스턴스 생성
        mvpContract = MVPVoting(mvpContractAddress);
        
        // 게임 정보 확인
        (uint256 gameIdRes, bool finalized, uint256 winningPlayerId, uint256 startTime, , ) = mvpContract.games(gameId);
        require(gameIdRes != 0, "Game does not exist");
        
        console.log("MVP Voting Status for Game ID:", gameId);
        console.log("Game Finalized:", finalized);
        if (finalized) {
            console.log("Winning Player ID:", winningPlayerId);
        }
        
        totalVotes = 0;
        playersWithVotes = 0;
        
        // 팀 A와 팀 B 선수들의 투표 현황 처리
        processTeamVotes("Team A", 0, teamAPlayerCount);
        processTeamVotes("Team B", teamAPlayerCount, playerCount);
        
        // 투표 요약
        console.log("\nVoting Summary:");
        console.log("- Total Votes:", totalVotes);
        console.log("- Players with Votes:", playersWithVotes, "/", playerCount);
        console.log("- Voting Deadline:", startTime);
        if (block.timestamp < startTime) {
            console.log("- Time Remaining:", startTime - block.timestamp, "seconds");
        } else {
            console.log("- Voting Closed:", block.timestamp - startTime, "seconds ago");
        }
    }
    
    // 팀별 선수 투표 처리 함수로 분리
    function processTeamVotes(string memory teamName, uint256 startIndex, uint256 endIndex) internal {
        console.log("\n%s Players Votes:", teamName);
        
        for (uint i = startIndex; i < endIndex; i++) {
            uint256 playerId = playerStartId + i;
            processPlayerVotes(playerId);
        }
    }
    
    // 개별 선수 투표 처리 함수로 분리
    function processPlayerVotes(uint256 playerId) internal {
        uint256 votes = mvpContract.getTotalVotes(gameId, playerId);
        totalVotes += votes;
        
        if (votes > 0 || showZeroVotes) {
            console.log("- Player ID", playerId, ":", votes);
            if (votes > 0) playersWithVotes++;
        }
    }
} 