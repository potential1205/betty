// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/Token.sol";

contract MintBETToPoolScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        uint256 amount = 10_000_000 * 1e18;

        address betAddress = vm.envAddress("BET_ADDRESS");

        address[10] memory poolAddresses = [
            vm.envAddress("DSB_POOL"),
            vm.envAddress("LTG_POOL"),
            vm.envAddress("LGT_POOL"),
            vm.envAddress("KWH_POOL"),
            vm.envAddress("NCD_POOL"),
            vm.envAddress("KTW_POOL"),
            vm.envAddress("SSG_POOL"),
            vm.envAddress("KIA_POOL"),
            vm.envAddress("SSL_POOL"),
            vm.envAddress("HWE_POOL")
        ];

        vm.startBroadcast(privateKey);

        Token bet = Token(betAddress);

        for (uint i = 0; i < poolAddresses.length; i++) {
            bet.mint(poolAddresses[i], amount);
            console.log("Minted 10,000,000 BET to pool: ", poolAddresses[i]);
        }

        vm.stopBroadcast();
    }
}
