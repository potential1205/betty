// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

// 보상풀 컨트랙트
contract RewardPool {
    address public owner;

    modifier onlyOwner() {
        require(msg.sender == owner, "Not owner");
        _;
    }

    constructor() {
        owner = msg.sender;
    }

    // 보상 전송 함수
    function reward(address token, address to, uint256 amount) external onlyOwner {
        require(IERC20(token).transfer(to, amount), "Transfer failed");
    }

    // 특정 토큰 잔액 조회
    function getBalance(address token) external view returns (uint256) {
        return IERC20(token).balanceOf(address(this));
    }

    // owner 변경
    function setOwner(address newOwner) external onlyOwner {
        require(newOwner != address(0), "Zero address");
        owner = newOwner;
    }
}
