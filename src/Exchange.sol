// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "./LiquidityPool.sol";

contract Exchange {
    IERC20 public btcToken;
    mapping(string => IERC20) public fanTokens;
    mapping(string => LiquidityPool) public liquidityPools;

    constructor(address _btcToken) {
        btcToken = IERC20(_btcToken);
    }

    function addFanToken(string memory token_name, address tokenAddress, address liquidityPool) external {
        fanTokens[token_name] = IERC20(tokenAddress);
        liquidityPools[token_name] = LiquidityPool(liquidityPool);
    }

    function add(uint256 amountBTC) external {
        btcToken.transferFrom(msg.sender, address(this), amountBTC);
    }

    function remove(uint256 amountBTC) external {
        btcToken.transfer(msg.sender, amountBTC);
    }

    function buy(string memory token_name, uint256 amountBTC) external {
        LiquidityPool pool = liquidityPools[token_name];
        btcToken.transferFrom(msg.sender, address(pool), amountBTC);
        pool.buyFanToken(amountBTC, msg.sender);
    }

    function sell(string memory token_name, uint256 amountFanToken) external {
        LiquidityPool pool = liquidityPools[token_name];
        fanTokens[token_name].transferFrom(msg.sender, address(pool), amountFanToken);
        pool.sellFanToken(amountFanToken, msg.sender);
    }

    function swap(string memory token_from, string memory token_to, uint256 amountFanToken) external {
        LiquidityPool fromPool = liquidityPools[token_from];
        LiquidityPool toPool = liquidityPools[token_to];

        fanTokens[token_from].transferFrom(msg.sender, address(fromPool), amountFanToken);

        uint256 btcAmount = fromPool.sellFanToken(amountFanToken, address(toPool));
        toPool.buyFanToken(btcAmount, msg.sender);
    }
}
