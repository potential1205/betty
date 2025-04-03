// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/DisplayAccess.sol";

contract DisplayAccessScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        vm.startBroadcast(privateKey);

        // DisplayAccess 컨트랙트 배포
        DisplayAccess displayAccess = new DisplayAccess();
        console.log("DisplayAccess contract deployed at:", address(displayAccess));

        vm.stopBroadcast();
    }
}