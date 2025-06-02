// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/Token.sol";

contract TokenScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        vm.startBroadcast(privateKey);

        // BET (BETTY 역할)
        Token bet = new Token("BET", 100_000_000);
        console.log("BET Token deployed at:", address(bet));
        
        // 팬토큰 목록
        string[10] memory tokenNames = ["DSB", "LTG", "LGT", "KWH", "NCD", "KTW", "SSG", "KIA", "SSL", "HWE"];
        Token[10] memory fanTokens;

        for (uint i = 0; i < tokenNames.length; i++) {
	    
	    string memory name = tokenNames[i];
        fanTokens[i] = new Token(tokenNames[i], 11_000_000);
        console.log(string.concat(tokenNames[i], " Token deployed at:"), address(fanTokens[i]));
        }

        vm.stopBroadcast();
    }

    
}
