// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

import "forge-std/Script.sol";
import "../src/Token.sol";
import "../src/Exchange.sol";

contract DeployExchange is Script {
    function run() external {
        vm.startBroadcast();

        Token token = new Token();
        console.log("Token deployed at:", address(token));

        Exchange exchange = new Exchange(address(token));
        console.log("Exchange deployed at:", address(exchange));

        vm.stopBroadcast();
    }
}
