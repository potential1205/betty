// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "@openzeppelin/contracts/token/ERC20/extensions/ERC20Burnable.sol";

// 토큰 컨트랙트: 베티코인(BTC) 및 팬토큰 발행
contract Token is ERC20Burnable {
    constructor(string memory token_name, uint256 initialSupply)
        ERC20(token_name, token_name)
    {
        _mint(msg.sender, initialSupply * 10 ** decimals());
    }

}
