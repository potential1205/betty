// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.25;

import "forge-std/Script.sol";
import "../src/Exchange.sol";

contract ExchangeScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        vm.startBroadcast(privateKey);

        address btcAddress = vm.envAddress("BTC_ADDRESS");
        Exchange exchange = new Exchange(btcAddress);
        console.log("Exchange deployed at:", address(exchange));

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

        for (uint i = 0; i < 10; i++) {
            exchange.addFanToken(tokenNames[i], fanTokenAddresses[i], poolAddresses[i]);
            console.log(string.concat(tokenNames[i], " registered to Exchange"));
        }

        vm.stopBroadcast();
    }
}
