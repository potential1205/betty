// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "./LiquidityPool.sol";
import "./Token.sol";

// 거래소 컨트랙트: add / remove / buy / sell / swap
contract Exchange {
    IERC20 public btcToken;
    mapping(string => IERC20) public fanTokens; // token_name → ERC20
    mapping(string => LiquidityPool) public liquidityPools; // token_name → Pool

    event BuyExecuted(address indexed buyer, string tokenName, uint256 amountIn, uint256 amountOut);
    event SellExecuted(address indexed seller, string tokenName, uint256 amountIn, uint256 amountOut);
    event SwapExecuted(address indexed user, string tokenFrom, string tokenTo, uint256 amountIn, uint256 amountOut);

    constructor(address _btcToken) {
        btcToken = IERC20(_btcToken);
    }

    // 팬토큰 등록 (token_name 기준)
    function addFanToken(string memory token_name, address tokenAddress, address liquidityPool) external {
        fanTokens[token_name] = IERC20(tokenAddress);
        liquidityPools[token_name] = LiquidityPool(liquidityPool);
    }

    // 베티코인 입금 (WON → BETTY 전환 완료 후 호출)
    function add(uint256 amountBTC) external {
        btcToken.transferFrom(msg.sender, address(this), amountBTC);
    }

    // 베티코인 출금 (BETTY → WON 전환용)
    function remove(uint256 amountBTC) external {
        btcToken.transfer(msg.sender, amountBTC);
    }

    // BETTY → 팬토큰 매수
    function buy(string memory token_name, uint256 amountBTC) external returns (uint256) {
        LiquidityPool pool = liquidityPools[token_name];
        btcToken.transferFrom(msg.sender, address(pool), amountBTC);
        uint256 amountOut = pool.buyFanToken(amountBTC, msg.sender);
        // 이벤트 추가
        emit BuyExecuted(msg.sender, token_name, amountBTC, amountOut);
        return amountOut;
    }

    // 팬토큰 → BETTY 매도
    function sell(string memory token_name, uint256 amountFanToken) external returns (uint256) {
        LiquidityPool pool = liquidityPools[token_name];
        fanTokens[token_name].transferFrom(msg.sender, address(pool), amountFanToken);
        uint256 amountOut = pool.sellFanToken(amountFanToken, msg.sender);
        // 이벤트 추가
        emit SellExecuted(msg.sender, token_name, amountFanToken, amountOut);
        return amountOut;
    }

    // 팬토큰 A → 팬토큰 B 스왑
    function swap(string memory token_from, string memory token_to, uint256 amountFanToken) external returns (uint256) {
        LiquidityPool fromPool = liquidityPools[token_from];
        LiquidityPool toPool = liquidityPools[token_to];

        fanTokens[token_from].transferFrom(msg.sender, address(fromPool), amountFanToken);

        uint256 btcAmount = fromPool.sellFanToken(amountFanToken, address(toPool));
        uint256 fanTokenBOut = toPool.buyFanToken(btcAmount, msg.sender);

        // 이벤트 추가
        emit SwapExecuted(msg.sender, token_from, token_to, amountFanToken, fanTokenBOut);

        return fanTokenBOut;
    }

    // 팬토큰 사용(소각)
    function use(string memory token_name, uint256 amount) external {
    Token token = Token(address(fanTokens[token_name]));
    token.burnFrom(msg.sender, amount);
    }
}
