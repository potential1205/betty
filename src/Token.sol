// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

// ✅ 토큰 컨트랙트: 베티코인(BTC) 및 팬토큰 발행 (예: DSB, LGT)
contract Token is ERC20 {
    constructor(string memory name, string memory token_name, uint256 initialSupply) ERC20(name, token_name) {
        _mint(msg.sender, initialSupply * 10 ** decimals());
    }
}
