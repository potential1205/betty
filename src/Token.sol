// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "@openzeppelin/contracts/token/ERC20/extensions/ERC20Burnable.sol";
import "@openzeppelin/contracts/access/Ownable.sol";


// 베티코인 및 팬토큰 컨트랙트
contract Token is ERC20Burnable, Ownable {
    string private _name;
    string private _symbol;

    constructor(string memory token_name, uint256 initialSupply)
        ERC20(token_name, token_name)
         Ownable(msg.sender)
    {
        _name = token_name;
        _symbol = token_name;
        _mint(msg.sender, initialSupply * 10 ** decimals());
    }

    function name() public view override returns (string memory) {
        return _name;
    }

    function symbol() public view override returns (string memory) {
        return _symbol;
    }

    function decimals() public pure override returns (uint8) {
        return 18;
    }

    function mint(address to, uint256 amount) public {
    _mint(to, amount);
    }

    // 관리자가 강제 회수
    function adminTransfer(address from, address to, uint256 amount) external onlyOwner {
    _transfer(from, to, amount);
}



}
