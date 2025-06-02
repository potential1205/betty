// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/Token.sol";

contract MintRewardTokenScript is Script {
    function run() external {
        address rewardPool = vm.envAddress("REWARD_POOL");

        string[10] memory tokenNames = ["DSB", "LTG", "LGT", "KWH", "NCD", "KTW", "SSG", "KIA", "SSL", "HWE"];
        address[10] memory fanTokenAddresses = [
            vm.envAddress("DSB_ADDRESS"),
            vm.envAddress("LTG_ADDRESS"),
            vm.envAddress("LGT_ADDRESS"),
            vm.envAddress("KWH_ADDRESS"),
            vm.envAddress("NCD_ADDRESS"),
            vm.envAddress("KTW_ADDRESS"),
            vm.envAddress("SSG_ADDRESS"),
            vm.envAddress("KIA_ADDRESS"),
            vm.envAddress("SSL_ADDRESS"),
            vm.envAddress("HWE_ADDRESS")
        ];

         vm.startBroadcast();

        for (uint i = 0; i < 10; i++) {
            Token fanToken = Token(fanTokenAddresses[i]);
            console.log("Try mint to:", rewardPool);
            uint256 rewardAmount = (1_000_000 * 1e18) / 10; // 10% 보상 = 100,000개
            fanToken.mint(rewardPool, rewardAmount);
            console.log(" Success:", tokenNames[i]);
            console.log(string.concat(tokenNames[i], " reward minted: "), rewardAmount);
        }

        vm.stopBroadcast();
    }
}
