// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

// ✅ 유동성 풀 컨트랙트: BTC와 팬토큰의 유동성 관리
contract LiquidityPool {
    IERC20 public btcToken;
    IERC20 public fanToken;

    uint256 public btcReserve;
    uint256 public fanTokenReserve;

    constructor(address _btcToken, address _fanToken) {
        btcToken = IERC20(_btcToken);
        fanToken = IERC20(_fanToken);
    }

    function addLiquidity(uint256 btcAmount, uint256 fanTokenAmount) external {
        require(btcAmount > 0 && fanTokenAmount > 0, "Invalid amounts");

        btcToken.transferFrom(msg.sender, address(this), btcAmount);
        fanToken.transferFrom(msg.sender, address(this), fanTokenAmount);

        btcReserve += btcAmount;
        fanTokenReserve += fanTokenAmount;
    }

    function removeLiquidity(uint256 btcAmount, uint256 fanTokenAmount) external {
        require(btcAmount <= btcReserve && fanTokenAmount <= fanTokenReserve, "Insufficient reserve");

        btcToken.transfer(msg.sender, btcAmount);
        fanToken.transfer(msg.sender, fanTokenAmount);

        btcReserve -= btcAmount;
        fanTokenReserve -= fanTokenAmount;
    }

    function getReserves() external view returns (uint256, uint256) {
        return (btcReserve, fanTokenReserve);
    }
}
