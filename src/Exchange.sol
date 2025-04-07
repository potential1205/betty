// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "./LiquidityPool.sol";
import "./Token.sol";

// 거래소 컨트랙트: add / remove / buy / sell / swap
contract Exchange {
    IERC20 public betToken;
    mapping(string => IERC20) public fanTokens; // token_name → ERC20
    mapping(string => LiquidityPool) public liquidityPools; // token_name → Pool

    uint256 public totalBETAdded;
    event BuyExecuted(address indexed buyer, string tokenName, uint256 amountIn, uint256 amountOut);
    event SellExecuted(address indexed seller, string tokenName, uint256 amountIn, uint256 amountOut);
    event SwapExecuted(address indexed user, string tokenFrom, string tokenTo, uint256 amountIn, uint256 amountOut);
    event DirectAdd(address indexed sender, uint256 amount); 

    constructor(address _betToken) {
        betToken = IERC20(_betToken);
    }

    // 팬토큰 등록 (token_name 기준)
    function addFanToken(string memory token_name, address tokenAddress, address liquidityPool) external {
        fanTokens[token_name] = IERC20(tokenAddress);
        liquidityPools[token_name] = LiquidityPool(liquidityPool);
    }

    // 베티코인 입금 (WON → BETTY 전환 완료 후 호출)
    function add(uint256 amountBET) external {
        betToken.transferFrom(msg.sender, address(this), amountBET);
        totalBETAdded += amountBET;
    }

    // 운영용 지갑 등에서 직접 transfer 해놓고 호출
    function addDirect(uint256 amountBET) external {
        require(betToken.balanceOf(address(this)) >= totalBETAdded + amountBET, "Insufficient BET in contract");
        totalBETAdded += amountBET;
        emit DirectAdd(msg.sender, amountBET);
    }

    // 베티코인 출금 (BETTY → WON 전환용)
    function remove(uint256 amountBET) external {
        betToken.transfer(msg.sender, amountBET);
    }

    // BETTY → 팬토큰 매수
    function buy(string memory token_name, uint256 amountBET) external returns (uint256) {
        LiquidityPool pool = liquidityPools[token_name];
        betToken.transferFrom(msg.sender, address(pool), amountBET);
        uint256 amountOut = pool.buyFanToken(amountBET, msg.sender);
        // 이벤트 추가
        emit BuyExecuted(msg.sender, token_name, amountBET, amountOut);
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

        uint256 betAmount = fromPool.sellFanToken(amountFanToken, address(toPool));
        uint256 fanTokenBOut = toPool.buyFanToken(betAmount, msg.sender);

        // 이벤트 추가
        emit SwapExecuted(msg.sender, token_from, token_to, amountFanToken, fanTokenBOut);

        return fanTokenBOut;
    }

    // 팬토큰 사용(소각)
    function use(string memory token_name, uint256 amount) external {
    Token token = Token(address(fanTokens[token_name]));
    token.burnFrom(msg.sender, amount);
    }

    // 현재 유동성 풀 기준 가격 조회 (BETTY 기준으로 팬토큰 가격 반환)
function getPrice(string memory token_name) external view returns (uint256 price) {
    LiquidityPool pool = liquidityPools[token_name];
    (uint256 betReserve, uint256 fanReserve) = pool.getReserves();

    require(betReserve > 0 && fanReserve > 0, "Not initialized");

    // 가격 = fanToken / bet (고정비율)
    price = (fanReserve * 1e18) / betReserve;
}
}
