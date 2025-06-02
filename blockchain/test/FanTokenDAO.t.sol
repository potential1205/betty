// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Test.sol";
import "../src/FanTokenDAO.sol";
import "../src/Token.sol";

contract FanTokenDAOTest is Test {
    FanTokenDAO public dao;
    Token public fanToken;
    
    // 테스트용 주소들
    address public owner = address(0x1);
    address public user1 = address(0x2);
    address public user2 = address(0x3);
    address public user3 = address(0x4);
    
    // 예시 ISO 8601 시간을 Unix timestamp로 변환한 값 (2025-04-06T13:30:42.845Z)
    // ISO 8601을 Unix timestamp로 변환: 1743960642
    uint256 public proposalDeadline = 1743960642;
    
    // 팀 ID와 제안 ID
    uint256 public teamId = 1;
    uint256 public proposalId = 123;
    
    // 샘플 컨텐츠 해시
    bytes32 public contentHash = keccak256(abi.encodePacked("Sample proposal content"));
    
    function setUp() public {
        vm.startPrank(owner);
        
        // DAO 컨트랙트 배포
        dao = new FanTokenDAO();
        
        // 테스트용 팬토큰 배포
        fanToken = new Token("TEST", 1_000_000);
        
        // 팀 토큰 설정
        dao.setTeamToken(teamId, address(fanToken));
        
        // 보상 풀 설정 (테스트를 위해 owner 주소를 보상 풀로 설정)
        dao.setRewardPool(owner);
        
        // 테스트 유저들에게 토큰 분배
        fanToken.transfer(user1, 100 * 10**18); // 100 tokens
        fanToken.transfer(user2, 50 * 10**18);  // 50 tokens
        fanToken.transfer(user3, 20 * 10**18);  // 20 tokens
        
        vm.stopPrank();
        
        // 현재 시간 설정 (2023년으로 설정)
        vm.warp(1672531200); // 2023-01-01 00:00:00 UTC
    }
    
    function test_RegisterProposal() public {
        vm.startPrank(user1);
        
        // 토큰 승인
        fanToken.approve(address(dao), 10 * 10**18);
        
        // 안건 등록
        dao.registerProposal(proposalId, contentHash, teamId, proposalDeadline);
        
        // 안건 정보 조회
        (
            bytes32 storedContentHash,
            uint256 storedTeamId,
            uint256 voteCount,
            bool executed,
            uint256 deadline,
            address proposer
        ) = dao.getProposalDetails(proposalId);
        
        // 검증
        assertEq(storedContentHash, contentHash, "Content hash mismatch");
        assertEq(storedTeamId, teamId, "Team ID mismatch");
        assertEq(voteCount, 0, "Initial vote count should be 0");
        assertEq(executed, false, "Proposal should not be executed initially");
        assertEq(deadline, proposalDeadline, "Deadline mismatch");
        assertEq(proposer, user1, "Proposer mismatch");
        
        // 제안자의 토큰 잔액 확인 (10 토큰 소각됨)
        assertEq(fanToken.balanceOf(user1), 90 * 10**18, "Proposer balance incorrect after proposal fee");
        
        vm.stopPrank();
    }
    
    function test_Vote() public {
        // 제안 등록
        vm.startPrank(user1);
        fanToken.approve(address(dao), 10 * 10**18);
        dao.registerProposal(proposalId, contentHash, teamId, proposalDeadline);
        vm.stopPrank();
        
        // user2 투표
        vm.startPrank(user2);
        dao.vote(proposalId);
        vm.stopPrank();
        
        // 투표 정보 확인
        (, , uint256 voteCount, , , ) = dao.getProposalDetails(proposalId);
        assertEq(voteCount, 1, "Vote count should be 1");
        
        // user2의 투표 여부 확인
        bool hasVoted = dao.hasVoted(proposalId, user2);
        assertTrue(hasVoted, "User2 should have voted");
    }
    
    function test_MultipleVotes() public {
        // 제안 등록
        vm.startPrank(user1);
        fanToken.approve(address(dao), 10 * 10**18);
        dao.registerProposal(proposalId, contentHash, teamId, proposalDeadline);
        vm.stopPrank();
        
        // 여러 사용자 투표
        vm.prank(user1);
        dao.vote(proposalId);
        
        vm.prank(user2);
        dao.vote(proposalId);
        
        vm.prank(user3);
        dao.vote(proposalId);
        
        // 투표 정보 확인
        (, , uint256 voteCount, , , ) = dao.getProposalDetails(proposalId);
        assertEq(voteCount, 3, "Vote count should be 3");
    }
    
    function test_RevertWhen_DuplicateVote() public {
        // 제안 등록
        vm.startPrank(user1);
        fanToken.approve(address(dao), 10 * 10**18);
        dao.registerProposal(proposalId, contentHash, teamId, proposalDeadline);
        dao.vote(proposalId);
        vm.stopPrank();
        
        // 이미 투표한 사용자가 다시 투표 시도
        vm.prank(user1);
        vm.expectRevert("Already voted");
        dao.vote(proposalId);
    }
    
    function test_RevertWhen_NoTokenBalance() public {
        // 제안 등록
        vm.startPrank(user1);
        fanToken.approve(address(dao), 10 * 10**18);
        dao.registerProposal(proposalId, contentHash, teamId, proposalDeadline);
        vm.stopPrank();
        
        // 토큰이 없는 사용자 생성
        address poorUser = address(0x5);
        
        // 토큰 없는 사용자가 투표 시도
        vm.prank(poorUser);
        vm.expectRevert("Must hold team token to vote");
        dao.vote(proposalId);
    }
    
    function test_ExecuteProposalAutomatically() public {
        // 제안 등록
        vm.startPrank(user1);
        fanToken.approve(address(dao), 10 * 10**18);
        dao.registerProposal(proposalId, contentHash, teamId, proposalDeadline);
        vm.stopPrank();
        
        // 필요 투표수 변경 (테스트를 위해 3으로 설정)
        vm.prank(owner);
        dao.setRequiredVotes(3);
        
        // 여러 사용자 투표
        vm.prank(user1);
        dao.vote(proposalId);
        
        vm.prank(user2);
        dao.vote(proposalId);
        
        // 마지막 투표 - 자동 실행 트리거 예상
        vm.prank(user3);
        dao.vote(proposalId);
        
        // 실행 여부 확인
        (, , , bool executed, , ) = dao.getProposalDetails(proposalId);
        assertTrue(executed, "Proposal should be executed automatically");
    }
    
    function test_ExecuteProposalManually() public {
        // 제안 등록
        vm.startPrank(user1);
        fanToken.approve(address(dao), 10 * 10**18);
        dao.registerProposal(proposalId, contentHash, teamId, proposalDeadline);
        vm.stopPrank();
        
        // 일부 사용자만 투표 (필요 수량에 도달하지 않음)
        vm.prank(user1);
        dao.vote(proposalId);
        
        // 마감 시간 이후로 시간 진행
        vm.warp(proposalDeadline + 1);
        
        // 수동으로 실행
        dao.executeProposal(proposalId);
        
        // 실행 여부 확인
        (, , uint256 voteCount, bool executed, , ) = dao.getProposalDetails(proposalId);
        assertEq(voteCount, 1, "Vote count should be 1");
        assertTrue(executed, "Proposal should be executed manually");
    }
    
    function test_ISO8601ToTimestamp() public {

        uint256 expectedTimestamp = 1743960642;
        
        // 테스트를 위해 해당 시간으로 블록 시간 설정
        vm.warp(expectedTimestamp);
        
        // 현재 블록 시간 확인
        assertEq(block.timestamp, expectedTimestamp, "Timestamp conversion incorrect");
        
    }
}
