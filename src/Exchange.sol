// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "forge-std/console.sol";

contract Exchange is ERC20 {
    address public tokenAddress;

    constructor(address token) ERC20("ETH TOKEN LP Token", "lpETHTOKEN") {
        require(token != address(0), "Token address passed is a null address");
        tokenAddress = token;
    }

    function getReserve() public view returns (uint256) {
        return ERC20(tokenAddress).balanceOf(address(this));
    }



    function addLiquidity(uint256 amountOfToken) public payable returns (uint256) {
    console.log("Starting addLiquidity...");
    console.log("msg.sender:", msg.sender);
    console.log("msg.value (ETH):", msg.value);
    console.log("amountOfToken:", amountOfToken);

    uint256 lpTokensToMint;
    uint256 ethReserveBalance = address(this).balance;
    uint256 tokenReserveBalance = getReserve();
    ERC20 token = ERC20(tokenAddress);

    console.log("ethReserveBalance (before):", ethReserveBalance);
    console.log("tokenReserveBalance (before):", tokenReserveBalance);

    if (tokenReserveBalance == 0) {
        console.log("First liquidity provider...");
        token.transferFrom(msg.sender, address(this), amountOfToken);
        lpTokensToMint = ethReserveBalance;
        _mint(msg.sender, lpTokensToMint);
        console.log("Liquidity added, minted LP tokens:", lpTokensToMint);
        return lpTokensToMint;
    }

    uint256 ethReservePriorToFunctionCall = ethReserveBalance - msg.value;
    console.log("ethReservePriorToFunctionCall:", ethReservePriorToFunctionCall);

    uint256 minTokenAmountRequired = (msg.value * tokenReserveBalance) / ethReservePriorToFunctionCall;
    console.log("minTokenAmountRequired:", minTokenAmountRequired);

    require(amountOfToken >= minTokenAmountRequired, "Insufficient token amount");
    token.transferFrom(msg.sender, address(this), minTokenAmountRequired);

    lpTokensToMint = (totalSupply() * msg.value) / ethReservePriorToFunctionCall;
    _mint(msg.sender, lpTokensToMint);

    console.log("Liquidity successfully added.");
    return lpTokensToMint;
}

    function removeLiquidity(uint256 amountOfLPTokens) public returns (uint256, uint256) {
        require(amountOfLPTokens > 0, "Amount must be greater than 0");

        uint256 ethReserveBalance = address(this).balance;
        uint256 lpTokenTotalSupply = totalSupply();
        uint256 ethToReturn = (ethReserveBalance * amountOfLPTokens) / lpTokenTotalSupply;
        uint256 tokenToReturn = (getReserve() * amountOfLPTokens) / lpTokenTotalSupply;

        _burn(msg.sender, amountOfLPTokens);
        payable(msg.sender).transfer(ethToReturn);
        ERC20(tokenAddress).transfer(msg.sender, tokenToReturn);

        return (ethToReturn, tokenToReturn);
    }

    function ethToTokenSwap(uint256 minTokensToReceive) public payable {
        uint256 tokenReserveBalance = getReserve();
        uint256 tokensToReceive = getOutputAmountFromSwap(msg.value, address(this).balance - msg.value, tokenReserveBalance);

        require(tokensToReceive >= minTokensToReceive, "Tokens received are less than minimum expected");
        ERC20(tokenAddress).transfer(msg.sender, tokensToReceive);
    }

    function tokenToEthSwap(uint256 tokensToSwap, uint256 minEthToReceive) public {
        uint256 tokenReserveBalance = getReserve();
        uint256 ethToReceive = getOutputAmountFromSwap(tokensToSwap, tokenReserveBalance, address(this).balance);

        require(ethToReceive >= minEthToReceive, "ETH received is less than minimum expected");

        ERC20(tokenAddress).transferFrom(msg.sender, address(this), tokensToSwap);
        payable(msg.sender).transfer(ethToReceive);
    }

    function getOutputAmountFromSwap(uint256 inputAmount, uint256 inputReserve, uint256 outputReserve)
        public pure returns (uint256) {
        require(inputReserve > 0 && outputReserve > 0, "Reserves must be greater than 0");

        uint256 inputAmountWithFee = inputAmount * 99;
        uint256 numerator = inputAmountWithFee * outputReserve;
        uint256 denominator = (inputReserve * 100) + inputAmountWithFee;

        return numerator / denominator;
    }
}
