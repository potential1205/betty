// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

interface ILiquidityPool {
    function getReserves() external view returns (uint256 betReserve, uint256 fanReserve);
}

interface IExchange {
    function liquidityPools(string memory token_name) external view returns (address);
}

contract ExchangeView {
    IExchange public exchange;

    constructor(address _exchange) {
        exchange = IExchange(_exchange);
    }

    // 1 팬토큰을 팔았을 때 받을 수 있는 BETTY 수량 (AMM 기준)
    function getTokenPrice(string memory token_name) external view returns (uint256 priceInBETTY) {
        address poolAddr = exchange.liquidityPools(token_name);
        require(poolAddr != address(0), "Invalid pool");

        (uint256 betReserve, uint256 fanReserve) = ILiquidityPool(poolAddr).getReserves();
        require(betReserve > 0 && fanReserve > 0, "Pool not initialized");

        uint256 k = betReserve * fanReserve;
        uint256 newFanReserve = fanReserve + 1e18; // 팬토큰 1개 매도
        uint256 newBetReserve = k / newFanReserve;
        priceInBETTY = betReserve - newBetReserve;
    }
}
