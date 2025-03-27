// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

contract LiquidityPool {
    IERC20 public btcToken;
    IERC20 public fanToken;

    uint256 public btcReserve;
    uint256 public fanTokenReserve;

    constructor(address _btcToken, address _fanToken) {
        btcToken = IERC20(_btcToken);
        fanToken = IERC20(_fanToken);
    }

    function setInitialLiquidity(uint256 _btcAmount, uint256 _fanTokenAmount) external {
        btcReserve = _btcAmount;
        fanTokenReserve = _fanTokenAmount;
    }

    function buyFanToken(uint256 amountBTCIn, address to) external returns (uint256 fanTokenOut) {
        uint256 newBtcReserve = btcReserve + amountBTCIn;
        uint256 newFanTokenReserve = (btcReserve * fanTokenReserve) / newBtcReserve;

        fanTokenOut = fanTokenReserve - newFanTokenReserve;

        btcReserve = newBtcReserve;
        fanTokenReserve = newFanTokenReserve;

        require(fanToken.transfer(to, fanTokenOut), "Fan token transfer failed");
    }

    function sellFanToken(uint256 amountFanTokenIn, address to) external returns (uint256 btcOut) {
        uint256 newFanTokenReserve = fanTokenReserve + amountFanTokenIn;
        uint256 newBtcReserve = (btcReserve * fanTokenReserve) / newFanTokenReserve;

        btcOut = btcReserve - newBtcReserve;

        fanTokenReserve = newFanTokenReserve;
        btcReserve = newBtcReserve;

        require(btcToken.transfer(to, btcOut), "BTC transfer failed");
    }
}
