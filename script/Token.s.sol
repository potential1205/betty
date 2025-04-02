// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.25;

import "forge-std/Script.sol";
import "../src/Token.sol";

contract TokenScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        vm.startBroadcast(privateKey);

        // BTC (BETTY 역할)
        Token btc = new Token("BTC", 10_000);
        console.log("BTC Token deployed at:", address(btc));

        // 팬토큰 목록
        string[10] memory tokenNames = ["DSB", "LTG", "LGT", "KWH", "NCD", "KTW", "SSG", "KIA", "SSL", "HWE"];
        Token[10] memory fanTokens;

        for (uint i = 0; i < tokenNames.length; i++) {
            fanTokens[i] = new Token(tokenNames[i], 1_000);
            console.log(string.concat(tokenNames[i], " Token deployed at:"), address(fanTokens[i]));
        }

        vm.stopBroadcast();
    }
}
