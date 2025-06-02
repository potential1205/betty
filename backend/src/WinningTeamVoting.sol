// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract WinningTeamVoting is Ownable {
    // 구조체 정의
    struct Game {
        uint256 gameId;
        uint256 teamAId;
        uint256 teamBId;
        uint256 startTime;        // 베팅 마감 시점
        uint256 totalTeamABets;
        uint256 totalTeamBBets;
        bool finalized;
        uint256 winningTeamId;
        address teamATokenAddress; // 팀 A의 팬토큰 주소
        address teamBTokenAddress; // 팀 B의 팬토큰 주소
    }

    struct Bet {
        uint256 amount;
        uint256 teamId;
        bool claimed;
    }

    // 매핑 정의
    mapping(uint256 => Game) public games;
    mapping(uint256 => mapping(address => Bet)) public bets;
    mapping(uint256 => mapping(uint256 => uint256)) public teamBetCounts;  // gameId => teamId => 베팅한 사용자 수
    
    // 게임별 베팅한 모든 사용자 주소 배열
    mapping(uint256 => address[]) private gameBettors;
    
    // 게임별 팀별 베팅한 사용자 주소 배열
    mapping(uint256 => mapping(uint256 => address[])) private teamBettors;
    
    // 사용자가 게임에 베팅했는지 여부 추적
    mapping(uint256 => mapping(address => bool)) private userBetOnGame;

    // 이벤트 정의
    event GameCreated(uint256 indexed gameId, uint256 teamAId, uint256 teamBId, uint256 startTime, address teamATokenAddress, address teamBTokenAddress);
    event BetPlaced(uint256 indexed gameId, address indexed user, uint256 teamId, uint256 amount);
    event GameFinalized(uint256 indexed gameId, uint256 winningTeamId);
    event RewardClaimed(uint256 indexed gameId, address indexed user, uint256 amount);
    event GameTokenAddressesUpdated(uint256 indexed gameId, address indexed newTeamATokenAddress, address indexed newTeamBTokenAddress);

    constructor() Ownable(msg.sender) {}

    // 게임 생성 함수
    function createGame(
        uint256 gameId,
        uint256 teamAId,
        uint256 teamBId,
        uint256 startTime,
        address teamATokenAddress,
        address teamBTokenAddress
    ) external onlyOwner {
        require(games[gameId].gameId == 0, "Game already exists");
        require(teamAId != teamBId, "Teams must be different");
        require(startTime > block.timestamp, "Start time must be in the future");
        require(teamATokenAddress != address(0), "Invalid team A token address");
        require(teamBTokenAddress != address(0), "Invalid team B token address");

        games[gameId] = Game({
            gameId: gameId,
            teamAId: teamAId,
            teamBId: teamBId,
            startTime: startTime,
            totalTeamABets: 0,
            totalTeamBBets: 0,
            finalized: false,
            winningTeamId: 0,
            teamATokenAddress: teamATokenAddress,
            teamBTokenAddress: teamBTokenAddress
        });

        emit GameCreated(gameId, teamAId, teamBId, startTime, teamATokenAddress, teamBTokenAddress);
    }

    // 베팅 함수
    function placeBet(uint256 gameId, uint256 teamId, uint256 amount) external {
        Game storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        require(block.timestamp < game.startTime, "Betting closed");
        require(!game.finalized, "Game already finalized");
        require(teamId == game.teamAId || teamId == game.teamBId, "Invalid team");
        require(bets[gameId][msg.sender].amount == 0, "Already bet");
        require(amount > 0, "Amount must be greater than 0");

        // 팀에 따라 다른 팬토큰 사용
        address tokenAddress;
        if (teamId == game.teamAId) {
            tokenAddress = game.teamATokenAddress;
            game.totalTeamABets += amount;
            teamBetCounts[gameId][game.teamAId]++;
            teamBettors[gameId][game.teamAId].push(msg.sender);
        } else {
            tokenAddress = game.teamBTokenAddress;
            game.totalTeamBBets += amount;
            teamBetCounts[gameId][game.teamBId]++;
            teamBettors[gameId][game.teamBId].push(msg.sender);
        }
        
        // 사용자가 이 게임에 처음 베팅하는 경우 전체 베터 목록에 추가
        if (!userBetOnGame[gameId][msg.sender]) {
            gameBettors[gameId].push(msg.sender);
            userBetOnGame[gameId][msg.sender] = true;
        }

        IERC20 fanToken = IERC20(tokenAddress);
        require(fanToken.transferFrom(msg.sender, address(this), amount), "Transfer failed");

        bets[gameId][msg.sender] = Bet({
            amount: amount,
            teamId: teamId,
            claimed: false
        });

        emit BetPlaced(gameId, msg.sender, teamId, amount);
    }

    // 게임 종료 및 승리팀 확정 함수
    function finalize(uint256 gameId, uint256 winningTeamId) external onlyOwner {
        Game storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        require(!game.finalized, "Game already finalized");
        require(winningTeamId == game.teamAId || winningTeamId == game.teamBId, "Invalid winning team");
        
        game.finalized = true;
        game.winningTeamId = winningTeamId;
        
        emit GameFinalized(gameId, winningTeamId);
    }

    // 보상 계산 함수 (내부용)
    function calculateReward(uint256 gameId, address user) internal view returns (uint256) {
        Game storage game = games[gameId];
        Bet storage userBet = bets[gameId][user];
        
        require(game.finalized, "Game not finalized");
        require(userBet.teamId == game.winningTeamId, "User did not bet on winning team");
        
        uint256 totalWinningTeamBets;
        uint256 totalLosingTeamBets;
        uint256 winningTeamBettersCount;
        
        if (game.winningTeamId == game.teamAId) {
            totalWinningTeamBets = game.totalTeamABets;
            totalLosingTeamBets = game.totalTeamBBets;
            winningTeamBettersCount = teamBetCounts[gameId][game.teamAId];
        } else {
            totalWinningTeamBets = game.totalTeamBBets;
            totalLosingTeamBets = game.totalTeamABets;
            winningTeamBettersCount = teamBetCounts[gameId][game.teamBId];
        }
        
        if (totalWinningTeamBets == 0 || winningTeamBettersCount == 0) {
            return userBet.amount; // 승리팀 베팅 없으면 원금만 반환
        }
        
        // 패배팀 토큰의 50%는 균등 분배
        uint256 equalShare = 0;
        if (winningTeamBettersCount > 0) {
            equalShare = (totalLosingTeamBets * 50) / 100 / winningTeamBettersCount;
        }
        
        // 패배팀 토큰의 50%는 비례 분배
        uint256 proportionalShare = 0;
        if (totalWinningTeamBets > 0) {
            proportionalShare = (totalLosingTeamBets * 50) / 100 * userBet.amount / totalWinningTeamBets;
        }
        
        // 원금 + 균등 배분 + 비례 배분
        return userBet.amount + equalShare + proportionalShare;
    }

    // 팀ID에 따른 토큰 주소 반환 (내부용)
    function getTokenAddress(uint256 gameId, uint256 teamId) internal view returns (address) {
        Game storage game = games[gameId];
        if (teamId == game.teamAId) {
            return game.teamATokenAddress;
        } else if (teamId == game.teamBId) {
            return game.teamBTokenAddress;
        } else {
            revert("Invalid team");
        }
    }

    // 사용자 보상 청구 함수
    function claimReward(uint256 gameId) external {
        Game storage game = games[gameId];
        Bet storage userBet = bets[gameId][msg.sender];
        
        require(game.finalized, "Game not finalized");
        require(userBet.amount > 0, "No bet placed");
        require(!userBet.claimed, "Reward already claimed");
        
        userBet.claimed = true;
        
        if (userBet.teamId == game.winningTeamId) {
            // 승리팀인 경우
            uint256 rewardAmount = calculateReward(gameId, msg.sender);
            address userTokenAddress = getTokenAddress(gameId, userBet.teamId);
            
            // 승리팀 토큰 원금 반환
            require(IERC20(userTokenAddress).transfer(msg.sender, userBet.amount), "Transfer failed");
            
            // 패배팀 토큰으로 보상 지급
            address losingTokenAddress = userBet.teamId == game.teamAId ? game.teamBTokenAddress : game.teamATokenAddress;
            uint256 bonusAmount = rewardAmount - userBet.amount;
            if (bonusAmount > 0) {
                require(IERC20(losingTokenAddress).transfer(msg.sender, bonusAmount), "Bonus transfer failed");
            }
            
            emit RewardClaimed(gameId, msg.sender, rewardAmount);
        } else {
            // 패배팀은 보상 없음, claimed만 true로 설정
            emit RewardClaimed(gameId, msg.sender, 0);
        }
    }

    // 관리자가 유저 대신 보상 청구 함수
    function claimForUser(uint256 gameId, address user) external onlyOwner {
        Game storage game = games[gameId];
        Bet storage userBet = bets[gameId][user];
        
        require(game.finalized, "Game not finalized");
        require(userBet.amount > 0, "No bet placed");
        require(!userBet.claimed, "Reward already claimed");
        
        userBet.claimed = true;
        
        if (userBet.teamId == game.winningTeamId) {
            // 승리팀인 경우
            uint256 rewardAmount = calculateReward(gameId, user);
            address userTokenAddress = getTokenAddress(gameId, userBet.teamId);
            
            // 승리팀 토큰 원금 반환
            require(IERC20(userTokenAddress).transfer(user, userBet.amount), "Transfer failed");
            
            // 패배팀 토큰으로 보상 지급
            address losingTokenAddress = userBet.teamId == game.teamAId ? game.teamBTokenAddress : game.teamATokenAddress;
            uint256 bonusAmount = rewardAmount - userBet.amount;
            if (bonusAmount > 0) {
                require(IERC20(losingTokenAddress).transfer(user, bonusAmount), "Bonus transfer failed");
            }
            
            emit RewardClaimed(gameId, user, rewardAmount);
        } else {
            // 패배팀은 보상 없음, claimed만 true로 설정
            emit RewardClaimed(gameId, user, 0);
        }
    }

    // 팀별 베팅 총액 조회 함수
    function getTeamBets(uint256 gameId, uint256 teamId) external view returns (uint256) {
        Game storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        
        if (teamId == game.teamAId) {
            return game.totalTeamABets;
        } else if (teamId == game.teamBId) {
            return game.totalTeamBBets;
        } else {
            revert("Invalid team");
        }
    }

    // 사용자 베팅 정보 조회 함수
    function getUserBet(uint256 gameId, address user) external view returns (uint256 amount, uint256 teamId, bool claimed) {
        return (bets[gameId][user].amount, bets[gameId][user].teamId, bets[gameId][user].claimed);
    }

    // 팀별 베팅한 사용자 수 조회 함수
    function getTeamBettersCount(uint256 gameId, uint256 teamId) external view returns (uint256) {
        return teamBetCounts[gameId][teamId];
    }

    function updateGameTokenAddresses(
        uint256 gameId, 
        address newTeamATokenAddress, 
        address newTeamBTokenAddress
    ) external onlyOwner {
        Game storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        require(!game.finalized, "Game already finalized");
        require(game.totalTeamABets == 0 && game.totalTeamBBets == 0, "Bets already placed");
        
        game.teamATokenAddress = newTeamATokenAddress;
        game.teamBTokenAddress = newTeamBTokenAddress;
        
        emit GameTokenAddressesUpdated(gameId, newTeamATokenAddress, newTeamBTokenAddress);
    }
    
    // 승리팀에 베팅한 사용자 목록 반환 함수
    function getWinningTeamBettors(uint256 gameId) external view returns (address[] memory) {
        Game storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        require(game.finalized, "Game not finalized");
        
        // 승리팀 ID로 베팅한 사용자 목록 반환
        return teamBettors[gameId][game.winningTeamId];
    }
    
    // 패배팀에 베팅한 사용자 목록 반환 함수
    function getLosingTeamBettors(uint256 gameId) external view returns (address[] memory) {
        Game storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        require(game.finalized, "Game not finalized");
        
        // 패배팀 ID 결정
        uint256 losingTeamId = game.winningTeamId == game.teamAId ? game.teamBId : game.teamAId;
        
        // 패배팀 ID로 베팅한 사용자 목록 반환
        return teamBettors[gameId][losingTeamId];
    }
    
    // 특정 팀에 베팅한 사용자 목록 반환 함수
    function getTeamBettors(uint256 gameId, uint256 teamId) external view returns (address[] memory) {
        Game storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        require(teamId == game.teamAId || teamId == game.teamBId, "Invalid team");
        
        return teamBettors[gameId][teamId];
    }
    
    // 게임에 베팅한 모든 사용자 목록 반환 함수
    function getAllGameBettors(uint256 gameId) external view returns (address[] memory) {
        Game storage game = games[gameId];
        require(game.gameId != 0, "Game does not exist");
        
        return gameBettors[gameId];
    }
}