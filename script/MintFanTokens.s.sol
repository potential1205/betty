// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/Token.sol";

contract MintFanTokensScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        address owner = vm.addr(privateKey);

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

        string[10] memory tokenNames = [
            "DSB", "LTG", "LGT", "KWH", "NCD",
            "KTW", "SSG", "KIA", "SSL", "HWE"
        ];

        vm.startBroadcast(privateKey);

        for (uint i = 0; i < fanTokenAddresses.length; i++) {
            address tokenAddr = fanTokenAddresses[i];
            console.log("Checking token:", tokenNames[i], tokenAddr);

            uint32 size;
            assembly {
                size := extcodesize(tokenAddr)
            }

            if (size == 0) {
                console.log(string.concat(tokenNames[i], " is not a deployed contract. Skipped."));
                continue;
            }

            Token token = Token(tokenAddr);

            try token.decimals() returns (uint8 dec) {
                token.mint(owner, 1_000_000 * 10 ** dec);
                console.log(string.concat(tokenNames[i], " minted to "), owner);
            } catch {
                console.log(string.concat(tokenNames[i], " mint failed. Possibly not a valid token."));
            }
        }

        vm.stopBroadcast();
    }
}
