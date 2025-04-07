// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

// 유동성 풀 컨트랙트 (수수료 없음, 고정 비율 기반 AMM 방식)
contract LiquidityPool {
    IERC20 public betToken;
    IERC20 public fanToken;

    uint256 public betReserve;
    uint256 public fanTokenReserve;

    constructor(address _betToken, address _fanToken) {
        require(_betToken != address(0), "BET token address is zero");
    require(_fanToken != address(0), "Fan token address is zero");
        betToken = IERC20(_betToken);
        fanToken = IERC20(_fanToken);
    }

    // 초기 유동성 설정 (1회만 호출 가능)
    function setInitialLiquidity(uint256 _betAmount, uint256 _fanTokenAmount) external {
        require(betReserve == 0 && fanTokenReserve == 0, "Already initialized");
        betReserve = _betAmount;
        fanTokenReserve = _fanTokenAmount;
    }

    // 초기화 여부 확인
    function isInitialized() external view returns (bool) {
        return (betReserve > 0 || fanTokenReserve > 0);
    }


    // 가격 계산 - 현재 유동성 풀 상태 반환
    function getReserves() external view returns (uint256 betAmount, uint256 fanTokenAmount) {
    return (betReserve, fanTokenReserve);
    }


    // BETTY → 팬토큰 구매
    function buyFanToken(uint256 amountBETIn, address to) external returns (uint256 fanTokenOut) {
        require(amountBETIn > 0, "Amount must be greater than 0");

        uint256 newBetReserve = betReserve + amountBETIn;
        uint256 newFanTokenReserve = (betReserve * fanTokenReserve) / newBetReserve;

        require(fanTokenReserve > newFanTokenReserve, "Invalid output calculation");

        fanTokenOut = fanTokenReserve - newFanTokenReserve;

        betReserve = newBetReserve;
        fanTokenReserve = newFanTokenReserve;

        require(fanToken.transfer(to, fanTokenOut), "Fan token transfer failed");
        return fanTokenOut;
    }

    // 팬토큰 → BETTY 판매
    function sellFanToken(uint256 amountFanTokenIn, address to) external returns (uint256 betOut) {
        require(amountFanTokenIn > 0, "Amount must be greater than 0");

        uint256 newFanTokenReserve = fanTokenReserve + amountFanTokenIn;
        uint256 newBetReserve = (betReserve * fanTokenReserve) / newFanTokenReserve;

        require(betReserve > newBetReserve, "Invalid output calculation");

        betOut = betReserve - newBetReserve;

        fanTokenReserve = newFanTokenReserve;
        betReserve = newBetReserve;

        require(betToken.transfer(to, betOut), "BET transfer failed");
        return betOut;
    }
}
