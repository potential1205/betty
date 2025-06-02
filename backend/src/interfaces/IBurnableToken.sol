// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

/// @title 소각 기능을 가진 ERC20 인터페이스
interface IBurnableToken {
    function burnFrom(address account, uint256 amount) external;
}