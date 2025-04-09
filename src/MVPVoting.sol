// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract MVPVoting is Ownable {
    // 게임 구조체
    struct MVPGame {
        uint256 gameId;
        bool finalized;
        uint256 winningPlayerId;
        uint256 startTime;        // 투표 마감 시점
        uint256 totalVoteAmount;  // 총 투표량
        uint256 totalWinnerVoteAmount; // 정답자 총 투표량
    }
    
    // 투표 구조체
    struct MVPVote {
        uint256 amount;       // 예치한 팀 팬토큰 수량
        uint256 playerId;     // 투표한 선수 ID
        address tokenAddress; // 해당 선수 소속 팀의 팬토큰 주소
        bool claimed;
    }
    
    // 게임 ID => 게임 정보
    mapping(uint256 => MVPGame) public games;
    
    // 게임 ID => 선수 ID => 팬토큰 주소
    mapping(uint256 => mapping(uint256 => address)) public playerToToken;
    
    // 게임 ID => 선수 ID => 총 투표량
    mapping(uint256 => mapping(uint256 => uint256)) public totalVotesPerPlayer;
    
    // 게임 ID => 사용자 주소 => 투표 정보
    mapping(uint256 => mapping(address => MVPVote)) public votes;
    
    // 게임 ID => 팬토큰 주소 => 총 투표량
    mapping(uint256 => mapping(address => uint256)) public totalVotesPerToken;
    
    // 게임 ID => 선수 ID => 선수에게 투표한 사용자들
    mapping(uint256 => mapping(uint256 => address[])) private playerVoters;
    
    // 게임 ID => 게임에 참여한 모든 사용자들
    mapping(uint256 => address[]) private gameVoters;
    
    // 게임 ID => 사용자 주소 => 이미 투표했는지 여부
    mapping(uint256 => mapping(address => bool)) private userVotedOnGame;
    
    // 이벤트 정의
    event MVPGameCreated(uint256 indexed gameId, uint256[] playerIds, uint256 startTime);
    event MVPVoted(uint256 indexed gameId, address indexed user, uint256 playerId, uint256 amount);
    event MVPGameFinalized(uint256 indexed gameId, uint256 winningPlayerId);
    event MVPRewardClaimed(uint256 indexed gameId, address indexed user, address[] tokenAddresses, uint256[] amounts);
    
    constructor() Ownable(msg.sender) {}
    
    // MVP 게임 생성
    function createMVPGame(
        uint256 gameId,
        uint256[] calldata playerIds, 
        address[] calldata tokenAddresses,
        uint256 startTime
    ) external onlyOwner {
        require(games[gameId].gameId == 0, "Game already exists");
        require(playerIds.length == tokenAddresses.length, "Arrays length mismatch");
        require(startTime > block.timestamp, "Start time must be in the future");
        
        games[gameId] = MVPGame({
            gameId: gameId,
            finalized: false,
            winningPlayerId: 0,
            startTime: startTime,
            totalVoteAmount: 0,
            totalWinnerVoteAmount: 0
        });
        
        // 각 선수의 팬토큰 주소 매핑
        for (uint256 i = 0; i < playerIds.length; i++) {
            require(tokenAddresses[i] != address(0), "Invalid token address");
            playerToToken[gameId][playerIds[i]] = tokenAddresses[i];
        }
        
        emit MVPGameCreated(gameId, playerIds, startTime);
    }
    
    // MVP 투표 함수
    function voteMVP(uint256 gameId, uint256 playerId, uint256 amount) external {
        MVPGame storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        require(!game.finalized, "Game already finalized");
        require(block.timestamp < game.startTime, "Voting closed");
        require(votes[gameId][msg.sender].amount == 0, "Already voted");
        require(amount > 0, "Amount must be greater than 0");
        
        // 해당 선수의 소속 팬토큰 가져오기
        address tokenAddress = playerToToken[gameId][playerId];
        require(tokenAddress != address(0), "Invalid player ID");
        
        // 팬토큰 전송
        IERC20 token = IERC20(tokenAddress);
        require(token.transferFrom(msg.sender, address(this), amount), "Token transfer failed");
        
        // 투표 정보 저장
        votes[gameId][msg.sender] = MVPVote({
            amount: amount,
            playerId: playerId,
            tokenAddress: tokenAddress,
            claimed: false
        });
        
        // 게임 및 선수별 투표 금액 업데이트
        totalVotesPerPlayer[gameId][playerId] += amount;
        totalVotesPerToken[gameId][tokenAddress] += amount;
        game.totalVoteAmount += amount;
        
        // 선수에게 투표한 사용자 추가
        playerVoters[gameId][playerId].push(msg.sender);
        
        // 게임 전체 투표자 목록에 추가
        if (!userVotedOnGame[gameId][msg.sender]) {
            gameVoters[gameId].push(msg.sender);
            userVotedOnGame[gameId][msg.sender] = true;
        }
        
        emit MVPVoted(gameId, msg.sender, playerId, amount);
    }
    
    // MVP 확정 함수
    function finalizeMVP(uint256 gameId, uint256 winningPlayerId) external onlyOwner {
        MVPGame storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        require(!game.finalized, "Game already finalized");
        require(playerToToken[gameId][winningPlayerId] != address(0), "Invalid player ID");
        
        game.finalized = true;
        game.winningPlayerId = winningPlayerId;
        
        // 정답자 총 투표량 계산
        game.totalWinnerVoteAmount = totalVotesPerPlayer[gameId][winningPlayerId];
        
        emit MVPGameFinalized(gameId, winningPlayerId);
    }
    
    // 유니크한 토큰 주소 수집 (내부 헬퍼 함수)
    function _collectUniqueLoserTokens(uint256 gameId, uint256 winningPlayerId) internal view returns (address[] memory, uint256) {
        address[] memory tempTokenAddresses = new address[](gameVoters[gameId].length);
        uint256 tokenCount = 0;
        
        // 오답자 토큰 주소 수집
        for (uint256 i = 0; i < gameVoters[gameId].length; i++) {
            address voter = gameVoters[gameId][i];
            MVPVote storage vote = votes[gameId][voter];
            
            // 오답자의 토큰만 수집
            if (vote.playerId != winningPlayerId) {
                address tokenAddr = vote.tokenAddress;
                bool exists = false;
                
                // 이미 추가된 토큰인지 확인
                for (uint256 j = 0; j < tokenCount; j++) {
                    if (tempTokenAddresses[j] == tokenAddr) {
                        exists = true;
                        break;
                    }
                }
                
                // 새 토큰이면 추가
                if (!exists) {
                    tempTokenAddresses[tokenCount] = tokenAddr;
                    tokenCount++;
                }
            }
        }
        
        return (tempTokenAddresses, tokenCount);
    }
    
    // 각 토큰별 오답자 총액 계산 (내부 헬퍼 함수)
    function _calculateTotalLosersAmount(uint256 gameId, uint256 winningPlayerId, address tokenAddr) internal view returns (uint256) {
        uint256 totalAmount = 0;
        
        for (uint256 j = 0; j < gameVoters[gameId].length; j++) {
            address voter = gameVoters[gameId][j];
            MVPVote storage vote = votes[gameId][voter];
            
            if (vote.playerId != winningPlayerId && vote.tokenAddress == tokenAddr) {
                totalAmount += vote.amount;
            }
        }
        
        return totalAmount;
    }
    
    // MVP 보상 계산 함수 (내부용)
    function calculateMVPReward(uint256 gameId, address user) internal view returns (address[] memory tokenAddresses, uint256[] memory rewardAmounts) {
        MVPGame storage game = games[gameId];
        MVPVote storage userVote = votes[gameId][user];
        
        require(game.finalized, "Game not finalized");
        require(userVote.amount > 0, "No vote placed");
        
        // 정답자만 보상 받음
        if (userVote.playerId != game.winningPlayerId) {
            return (new address[](0), new uint256[](0));
        }
        
        // 모든 팬토큰 주소 수집 (중복 없이)
        (address[] memory tempTokenAddresses, uint256 tokenCount) = _collectUniqueLoserTokens(gameId, game.winningPlayerId);
        
        // 최종 배열 크기 조정
        tokenAddresses = new address[](tokenCount + 1); // +1 자신의 토큰용
        rewardAmounts = new uint256[](tokenCount + 1);
        
        // 자신의 원금은 첫 번째 항목으로 추가
        tokenAddresses[0] = userVote.tokenAddress;
        rewardAmounts[0] = userVote.amount;
        
        // 실제 정답자 수 계산
        uint256 winnerCount = playerVoters[gameId][game.winningPlayerId].length;
        
        // 각 토큰별 보상 계산
        for (uint256 i = 0; i < tokenCount; i++) {
            address tokenAddr = tempTokenAddresses[i];
            
            // 해당 토큰에 대한 오답자 총액 계산
            uint256 totalLosersAmount = _calculateTotalLosersAmount(gameId, game.winningPlayerId, tokenAddr);
            
            // 보상 계산: 50% 균등 + 50% 비례
            uint256 equalShare = 0;
            if (winnerCount > 0) {
                equalShare = (totalLosersAmount * 50) / 100 / winnerCount;
            }
            
            uint256 proportionalShare = 0;
            if (game.totalWinnerVoteAmount > 0) {
                proportionalShare = (totalLosersAmount * 50) / 100 * userVote.amount / game.totalWinnerVoteAmount;
            }
            
            tokenAddresses[i + 1] = tokenAddr;
            rewardAmounts[i + 1] = equalShare + proportionalShare;
        }
        
        return (tokenAddresses, rewardAmounts);
    }
    
    // 사용자 보상 청구 함수
    function claimMVPReward(uint256 gameId) external {
        _claimMVPReward(gameId, msg.sender);
    }
    
    // 관리자가 사용자 대신 보상 청구 함수
    function claimMVPRewardForUser(uint256 gameId, address user) external onlyOwner {
        _claimMVPReward(gameId, user);
    }
    
    // 내부 보상 처리 함수
    function _claimMVPReward(uint256 gameId, address user) internal {
        MVPGame storage game = games[gameId];
        MVPVote storage userVote = votes[gameId][user];
        
        require(game.finalized, "Game not finalized");
        require(userVote.amount > 0, "No vote placed");
        require(!userVote.claimed, "Reward already claimed");
        
        userVote.claimed = true;
        
        // 정답자인 경우에만 보상 지급
        if (userVote.playerId == game.winningPlayerId) {
            (address[] memory tokenAddresses, uint256[] memory rewardAmounts) = calculateMVPReward(gameId, user);
            
            // 각 토큰별로 보상 전송
            for (uint256 i = 0; i < tokenAddresses.length; i++) {
                if (rewardAmounts[i] > 0) {
                    IERC20 token = IERC20(tokenAddresses[i]);
                    require(token.transfer(user, rewardAmounts[i]), "Reward transfer failed");
                }
            }
            
            emit MVPRewardClaimed(gameId, user, tokenAddresses, rewardAmounts);
        } else {
            // 오답자는 보상 없음, claimed만 true로 설정
            emit MVPRewardClaimed(gameId, user, new address[](0), new uint256[](0));
        }
    }
    
    // 선수별 총 투표량 조회
    function getTotalVotes(uint256 gameId, uint256 playerId) external view returns (uint256) {
        return totalVotesPerPlayer[gameId][playerId];
    }
    
    // 사용자 투표 정보 조회
    function getUserVote(uint256 gameId, address user) external view returns (uint256 amount, uint256 playerId, address tokenAddress, bool claimed) {
        MVPVote storage userVote = votes[gameId][user];
        return (userVote.amount, userVote.playerId, userVote.tokenAddress, userVote.claimed);
    }
    
    // 선수에게 투표한 사용자 목록 조회
    function getPlayerVoters(uint256 gameId, uint256 playerId) external view returns (address[] memory) {
        return playerVoters[gameId][playerId];
    }
    
    // 게임에 투표한 모든 사용자 목록 조회
    function getAllGameVoters(uint256 gameId) external view returns (address[] memory) {
        return gameVoters[gameId];
    }
    
    // 정답자(우승자) 목록 조회
    function getWinningVoters(uint256 gameId) external view returns (address[] memory) {
        MVPGame storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        require(game.finalized, "Game not finalized");
        
        return playerVoters[gameId][game.winningPlayerId];
    }
    
    // 오답자 목록 조회
    function getLosingVoters(uint256 gameId) external view returns (address[] memory) {
        MVPGame storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        require(game.finalized, "Game not finalized");
        
        // 전체 투표자 목록
        address[] memory allVoters = gameVoters[gameId];
        
        // 정답자 목록
        address[] memory winnersArray = playerVoters[gameId][game.winningPlayerId];
        
        // 오답자 수 계산
        uint256 loserCount = 0;
        for (uint256 i = 0; i < allVoters.length; i++) {
            bool isWinner = false;
            for (uint256 j = 0; j < winnersArray.length; j++) {
                if (allVoters[i] == winnersArray[j]) {
                    isWinner = true;
                    break;
                }
            }
            if (!isWinner) {
                loserCount++;
            }
        }
        
        // 오답자 배열 생성
        address[] memory losers = new address[](loserCount);
        uint256 loserIndex = 0;
        
        for (uint256 i = 0; i < allVoters.length; i++) {
            bool isWinner = false;
            for (uint256 j = 0; j < winnersArray.length; j++) {
                if (allVoters[i] == winnersArray[j]) {
                    isWinner = true;
                    break;
                }
            }
            
            if (!isWinner) {
                losers[loserIndex] = allVoters[i];
                loserIndex++;
            }
        }
        
        return losers;
    }
} 