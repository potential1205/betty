// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract DisplayAccess is Ownable {
    // 사용자의 액세스 권한을 저장하는 구조체
    struct AccessKey {
        uint256 teamId;
        uint256 gameId;
        address userAddress;
    }
    
    // 액세스 권한 매핑 (teamId_gameId_userAddress => hasAccess)
    mapping(bytes32 => bool) public accessRights;
    
    // 보상 풀 주소
    address public rewardPool;
    
    // 이벤트 정의
    event AccessGranted(address indexed user, uint256 teamId, uint256 gameId);
    event RewardPoolUpdated(address oldRewardPool, address newRewardPool);
    
    constructor() Ownable(msg.sender) {}
    
    // 보상 풀 설정 (관리자 전용)
    function setRewardPool(address _rewardPool) external onlyOwner {
        address oldRewardPool = rewardPool;
        rewardPool = _rewardPool;
        emit RewardPoolUpdated(oldRewardPool, _rewardPool);
    }
    
    // 액세스 키 생성 함수
    function createAccessKey(uint256 teamId, uint256 gameId, address userAddress) internal pure returns (bytes32) {
        return keccak256(abi.encodePacked(teamId, gameId, userAddress));
    }
    
    // 액세스 권한 구매
    function purchaseAccess(address tokenAddress, uint256 teamId, uint256 gameId) external {
        require(rewardPool != address(0), "Reward pool not set");
        bytes32 accessKey = createAccessKey(teamId, gameId, msg.sender);
        require(!accessRights[accessKey], "has access");
        
        IERC20 token = IERC20(tokenAddress);
        
        // 1 팬토큰 전송 (18 decimals 고려)
        uint256 amount = 1 * 10**18;
        require(token.transferFrom(msg.sender, rewardPool, amount), "transfer to reward pool failed");
        
        // 액세스 권한 부여
        accessRights[accessKey] = true;
        
        // 이벤트 발생
        emit AccessGranted(msg.sender, teamId, gameId);
    }
    
    // 액세스 권한 확인
    function checkAccess(uint256 teamId, uint256 gameId, address userAddress) external view returns (bool) {
        bytes32 accessKey = createAccessKey(teamId, gameId, userAddress);
        return accessRights[accessKey];
    }
}