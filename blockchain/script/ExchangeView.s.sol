// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "forge-std/Script.sol";

interface IExchange {
    function liquidityPools(string memory token_name) external view returns (address);
}

contract ExchangeViewScript is Script {
    function run() external {
        // SET THIS BEFORE RUNNING!
        address exchange = vm.envAddress("EXCHANGE_ADDRESS");

        vm.startBroadcast();
        ExchangeView viewContract = new ExchangeView(exchange);
        vm.stopBroadcast();

        console2.log("ExchangeView deployed at:", address(viewContract));
    }
}

interface ILiquidityPool {
    function getReserves() external view returns (uint256 betReserve, uint256 fanReserve);
}

contract ExchangeView {
    IExchange public exchange;

    constructor(address _exchange) {
        exchange = IExchange(_exchange);
    }

    function getTokenPrice(string memory token_name) external view returns (uint256 priceInBETTY) {
        address poolAddr = exchange.liquidityPools(token_name);
        require(poolAddr != address(0), "Invalid pool");

        (uint256 betReserve, uint256 fanReserve) = ILiquidityPool(poolAddr).getReserves();
        require(betReserve > 0 && fanReserve > 0, "Pool not initialized");

        uint256 k = betReserve * fanReserve;
        uint256 newFanReserve = fanReserve + 1e18;
        uint256 newBetReserve = k / newFanReserve;

        priceInBETTY = betReserve - newBetReserve;
    }
}
