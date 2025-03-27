// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "./LiquidityPool.sol";

// ✅ 거래소 컨트랙트: 모든 거래 기능 구현 (칼럼명 반영)
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

    // ✅ WON -> BTC 충전 (add)
    function add(uint256 amount) external payable {
        require(msg.value == amount, "Incorrect WON amount sent");
        btcToken.transfer(msg.sender, amount);
    }

    // ✅ BTC -> WON 출금 (remove)
    function remove(uint256 amount) external {
        require(btcToken.balanceOf(msg.sender) >= amount, "Insufficient BTC balance");
        btcToken.transferFrom(msg.sender, address(this), amount);
        payable(msg.sender).transfer(amount);
    }

    // ✅ BTC -> 팬토큰 매수 (buy)
    function buy(string memory token_name, uint256 amountIn) external {
        LiquidityPool pool = liquidityPools[token_name];
        IERC20 fanToken = fanTokens[token_name];
        
        require(address(pool) != address(0), "Invalid token_name");

        btcToken.transferFrom(msg.sender, address(pool), amountIn);
        pool.addLiquidity(amountIn, amountIn);

        fanToken.transfer(msg.sender, amountIn);
    }

    // ✅ 팬토큰 -> BTC 매도 (sell)
    function sell(string memory token_name, uint256 amountIn) external {
        LiquidityPool pool = liquidityPools[token_name];
        IERC20 fanToken = fanTokens[token_name];

        require(address(pool) != address(0), "Invalid token_name");

        fanToken.transferFrom(msg.sender, address(pool), amountIn);
        pool.removeLiquidity(amountIn, amountIn);

        btcToken.transfer(msg.sender, amountIn);
    }

    // ✅ 팬토큰 간 스왑 (swap)
    function swap(string memory token_from, string memory token_to, uint256 amountIn) external {
        LiquidityPool fromPool = liquidityPools[token_from];
        LiquidityPool toPool = liquidityPools[token_to];
        IERC20 fromToken = fanTokens[token_from];
        IERC20 toToken = fanTokens[token_to];

        require(address(fromPool) != address(0), "Invalid source token_name");
        require(address(toPool) != address(0), "Invalid destination token_name");

        fromToken.transferFrom(msg.sender, address(fromPool), amountIn);
        fromPool.removeLiquidity(amountIn, amountIn);

        toPool.addLiquidity(amountIn, amountIn);
        toToken.transfer(msg.sender, amountIn);
    }
}
