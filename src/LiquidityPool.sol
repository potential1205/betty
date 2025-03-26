// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

contract LiquidityPool {
    IERC20 public tokenA;
    IERC20 public tokenB;
    
    uint256 public reserveA;
    uint256 public reserveB;

    mapping(address => uint256) public liquidity;

    constructor(address _tokenA, address _tokenB) {
        tokenA = IERC20(_tokenA);
        tokenB = IERC20(_tokenB);
    }

    function deposit(uint256 amountA, uint256 amountB) external {
        require(amountA > 0 && amountB > 0, "Invalid amounts");

        tokenA.transferFrom(msg.sender, address(this), amountA);
        tokenB.transferFrom(msg.sender, address(this), amountB);

        reserveA += amountA;
        reserveB += amountB;
        liquidity[msg.sender] += (amountA + amountB) / 2;
    }

    function withdraw(uint256 amount) external {
        require(liquidity[msg.sender] >= amount, "Insufficient liquidity");

        uint256 amountA = (amount * reserveA) / totalLiquidity();
        uint256 amountB = (amount * reserveB) / totalLiquidity();

        liquidity[msg.sender] -= amount;
        reserveA -= amountA;
        reserveB -= amountB;

        tokenA.transfer(msg.sender, amountA);
        tokenB.transfer(msg.sender, amountB);
    }

    function swap(address fromToken, uint256 amountIn) external returns (uint256 amountOut) {
        require(fromToken == address(tokenA) || fromToken == address(tokenB), "Invalid token");

        bool isTokenA = fromToken == address(tokenA);
        IERC20 inputToken = isTokenA ? tokenA : tokenB;
        IERC20 outputToken = isTokenA ? tokenB : tokenA;
        uint256 inputReserve = isTokenA ? reserveA : reserveB;
        uint256 outputReserve = isTokenA ? reserveB : reserveA;

        require(amountIn > 0, "Invalid swap amount");
        require(inputReserve > 0 && outputReserve > 0, "Insufficient liquidity");

        uint256 amountInWithFee = (amountIn * 997) / 1000; // 0.3% 수수료
        amountOut = (amountInWithFee * outputReserve) / (inputReserve + amountInWithFee);

        inputToken.transferFrom(msg.sender, address(this), amountIn);
        outputToken.transfer(msg.sender, amountOut);

        if (isTokenA) {
            reserveA += amountIn;
            reserveB -= amountOut;
        } else {
            reserveB += amountIn;
            reserveA -= amountOut;
        }
    }

    function totalLiquidity() public view returns (uint256) {
        return reserveA + reserveB;
    }
}
