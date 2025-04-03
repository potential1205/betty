// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/LiquidityPool.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

contract LiquidityPoolScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
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

        vm.startBroadcast(privateKey);



        for (uint i = 0; i < 10; i++) {
            address fan = fanTokenAddresses[i];

            console.log(tokenNames[i], "address:", fanTokenAddresses[i]);
            require(fanTokenAddresses[i] != address(0), string.concat(tokenNames[i], " address is zero"));

            if (btcAddress == fan) {
                console.log(string.concat(tokenNames[i], " skipped (same address as BTC)"));
                continue;
            }

        

            // 배포 시작
            console.log("Creating pool for", tokenNames[i]);
            LiquidityPool pool = new LiquidityPool(btcAddress, fan);
            console.log(string.concat(tokenNames[i], " Pool deployed at:"), address(pool));

            // 토큰 approve
            require(IERC20(btcAddress).approve(address(pool), 10_000_000 * 1e18), "BTC approve failed");
            require(IERC20(fan).approve(address(pool), 1_000_000 * 1e18), "Fan token approve failed");

            // 초기 유동성 설정
            pool.setInitialLiquidity(10_000_000 * 1e18, 1_000_000 * 1e18);
            console.log("  -> Initial liquidity set.");
        }

        vm.stopBroadcast();

        console.log("btcAddress:", btcAddress);
        for (uint i = 0; i < 10; i++) {
            console.log(string.concat(tokenNames[i], " address: "), fanTokenAddresses[i]);
        }
    }


}
