// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.25;

import "forge-std/Script.sol";
import "../src/LiquidityPool.sol";

contract LiquidityPoolScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        vm.startBroadcast(privateKey);

        address btcAddress = vm.envAddress("BTC_ADDRESS");
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

        for (uint i = 0; i < 10; i++) {
            LiquidityPool pool = new LiquidityPool(btcAddress, fanTokenAddresses[i]);
            console.log(string.concat(tokenNames[i], " Pool deployed at:"), address(pool));
        }

        vm.stopBroadcast();
    }
}
