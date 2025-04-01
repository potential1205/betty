// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

// 유동성 풀 컨트랙트 (수수료 없음, 고정 비율 기반 AMM 방식)
contract LiquidityPool {
    IERC20 public btcToken;
    IERC20 public fanToken;

    uint256 public btcReserve;
    uint256 public fanTokenReserve;

    constructor(address _btcToken, address _fanToken) {
        btcToken = IERC20(_btcToken);
        fanToken = IERC20(_fanToken);
    }

    // 초기 유동성 설정 (1회만 호출 가능)
    function setInitialLiquidity(uint256 _btcAmount, uint256 _fanTokenAmount) external {
        require(btcReserve == 0 && fanTokenReserve == 0, "Already initialized");
        btcReserve = _btcAmount;
        fanTokenReserve = _fanTokenAmount;
    }

    // BETTY → 팬토큰 구매
    function buyFanToken(uint256 amountBTCIn, address to) external returns (uint256 fanTokenOut) {
        require(amountBTCIn > 0, "Amount must be greater than 0");

        uint256 newBtcReserve = btcReserve + amountBTCIn;
        uint256 newFanTokenReserve = (btcReserve * fanTokenReserve) / newBtcReserve;

        require(fanTokenReserve > newFanTokenReserve, "Invalid output calculation");

        fanTokenOut = fanTokenReserve - newFanTokenReserve;

        btcReserve = newBtcReserve;
        fanTokenReserve = newFanTokenReserve;

        require(fanToken.transfer(to, fanTokenOut), "Fan token transfer failed");
        return fanTokenOut;
    }

    // 팬토큰 → BETTY 판매
    function sellFanToken(uint256 amountFanTokenIn, address to) external returns (uint256 btcOut) {
        require(amountFanTokenIn > 0, "Amount must be greater than 0");

        uint256 newFanTokenReserve = fanTokenReserve + amountFanTokenIn;
        uint256 newBtcReserve = (btcReserve * fanTokenReserve) / newFanTokenReserve;

        require(btcReserve > newBtcReserve, "Invalid output calculation");

        btcOut = btcReserve - newBtcReserve;

        fanTokenReserve = newFanTokenReserve;
        btcReserve = newBtcReserve;

        require(btcToken.transfer(to, btcOut), "BTC transfer failed");
        return btcOut;
    }
}
