// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "../src/Token.sol";

contract FanTokenDAO is Ownable {
    // 안건 구조체
    struct Proposal {
        bytes32 contentHash;  // 안건 해시 (백엔드에서 생성)
        uint256 teamId;       // 팀 ID
        uint256 voteCount;    // 투표 수 (반대 투표 없음)
        bool executed;        // 실행 여부
        uint256 deadline;     // 투표 마감 시간
        address proposer;     // 제안자
        mapping(address => bool) hasVoted; // 투표 여부
    }

    // 팀별 팬토큰 주소
    mapping(uint256 => address) public teamTokens;
    
    // 안건 ID => 안건 정보
    mapping(uint256 => Proposal) public proposals;
    
    // 안건 통과에 필요한 투표 수 (기본값 100)
    uint256 public requiredVotes = 100;
    
    // 안건 제안에 필요한 토큰 수량
    uint256 public constant PROPOSAL_FEE = 10 * 10**18; // 10 tokens with 18 decimals

    // 보상 풀 주소
    address public rewardPool;
    
    // 이벤트 정의
    event ProposalRegistered(uint256 indexed proposalId, bytes32 contentHash, uint256 teamId, address proposer, uint256 deadline);
    event Voted(uint256 indexed proposalId, address indexed voter, uint256 teamId);
    event ProposalExecuted(uint256 indexed proposalId, bool passed);
    event RequiredVotesChanged(uint256 oldValue, uint256 newValue);
    event RewardPoolUpdated(address oldRewardPool, address newRewardPool);
    
    constructor() Ownable(msg.sender) {}
    
    // 팀 토큰 설정 (관리자 전용)
    function setTeamToken(uint256 teamId, address tokenAddress) external onlyOwner {
        teamTokens[teamId] = tokenAddress;
    }
    
    // 보상 풀 설정 (관리자 전용)
    function setRewardPool(address _rewardPool) external onlyOwner {
        address oldRewardPool = rewardPool;
        rewardPool = _rewardPool;
        emit RewardPoolUpdated(oldRewardPool, _rewardPool);
    }
    
    // 필요 투표 수 설정 (관리자 전용)
    function setRequiredVotes(uint256 newRequiredVotes) external onlyOwner {
        uint256 oldValue = requiredVotes;
        requiredVotes = newRequiredVotes;
        emit RequiredVotesChanged(oldValue, newRequiredVotes);
    }
    
    // 안건 등록 (백엔드가 생성한 contentHash 사용)
    function registerProposal(uint256 proposalId, bytes32 contentHash, uint256 teamId, uint256 deadline) external {
        require(teamTokens[teamId] != address(0), "Team token not set");
        require(proposals[proposalId].contentHash == bytes32(0), "Proposal already exists");
        require(deadline > block.timestamp, "Deadline must be in the future");
        require(rewardPool != address(0), "Reward pool not set");
        
        // 토큰 주소 가져오기
        address tokenAddress = teamTokens[teamId];
        IERC20 token = IERC20(tokenAddress);
        
        // 10 팬토큰을 보상 풀로 전송
        require(token.transferFrom(msg.sender, rewardPool, PROPOSAL_FEE), "Token transfer to reward pool failed");
        
        // 안건 등록
        Proposal storage newProposal = proposals[proposalId];
        newProposal.contentHash = contentHash;
        newProposal.teamId = teamId;
        newProposal.deadline = deadline;
        newProposal.proposer = msg.sender;
        
        emit ProposalRegistered(proposalId, contentHash, teamId, msg.sender, deadline);
    }
    
    // 투표 기능 (찬성만 있음)
    function vote(uint256 proposalId) external {
        Proposal storage proposal = proposals[proposalId];
        
        require(proposal.contentHash != bytes32(0), "Proposal does not exist");
        require(block.timestamp <= proposal.deadline, "Voting period ended");
        require(!proposal.executed, "Proposal already executed");
        require(!proposal.hasVoted[msg.sender], "Already voted");
        
        // 해당 팀의 팬토큰 잔액 확인
        address tokenAddress = teamTokens[proposal.teamId];
        uint256 balance = IERC20(tokenAddress).balanceOf(msg.sender);
        require(balance > 0, "Must hold team token to vote");
        
        // 투표 기록
        proposal.hasVoted[msg.sender] = true;
        proposal.voteCount += 1;
        
        emit Voted(proposalId, msg.sender, proposal.teamId);
        
        // 투표수가 필요 수량을 넘으면 자동으로 실행
        if (proposal.voteCount >= requiredVotes) {
            _executeProposal(proposalId);
        }
    }
    
    // 안건 실행 (내부 함수)
    function _executeProposal(uint256 proposalId) internal {
        Proposal storage proposal = proposals[proposalId];
        
        if (!proposal.executed) {
            proposal.executed = true;
            
            // 투표 수가 필요 수량 이상이면 통과
            bool passed = proposal.voteCount >= requiredVotes;
            
            emit ProposalExecuted(proposalId, passed);
        }
    }
    
    // 안건 실행 (외부 함수, 기간 종료 후 수동 실행)
    function executeProposal(uint256 proposalId) external {
        Proposal storage proposal = proposals[proposalId];
        
        require(proposal.contentHash != bytes32(0), "Proposal does not exist");
        require(block.timestamp > proposal.deadline, "Voting period not ended");
        require(!proposal.executed, "Proposal already executed");
        
        _executeProposal(proposalId);
    }
    
    // 안건 정보 조회
    function getProposalDetails(uint256 proposalId) external view returns (
        bytes32 contentHash,
        uint256 teamId,
        uint256 voteCount,
        bool executed,
        uint256 deadline,
        address proposer
    ) {
        Proposal storage proposal = proposals[proposalId];
        return (
            proposal.contentHash,
            proposal.teamId,
            proposal.voteCount,
            proposal.executed,
            proposal.deadline,
            proposal.proposer
        );
    }
    
    // 사용자의 투표 여부 확인
    function hasVoted(uint256 proposalId, address voter) external view returns (bool) {
        return proposals[proposalId].hasVoted[voter];
    }
}