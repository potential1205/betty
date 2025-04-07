// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/RewardPool.sol";

contract RewardPoolScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");

        vm.startBroadcast(privateKey);

        RewardPool rewardPool = new RewardPool();
        console.log(" RewardPool deployed at:", address(rewardPool));

        vm.stopBroadcast();
    }
}
