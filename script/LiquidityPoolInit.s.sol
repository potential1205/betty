// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/LiquidityPool.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

contract LiquidityPoolInitScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        address deployer = vm.addr(privateKey);
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

        for (uint i = 0; i < 10; i++) {
            console.log("Initializing pool for", tokenNames[i]);
            IERC20 btc = IERC20(btcAddress);
            IERC20 fan = IERC20(fanTokenAddresses[i]);
            LiquidityPool pool = LiquidityPool(poolAddresses[i]);

            uint256 btcAmount = 10_000 * 1e18;
            uint256 fanAmount = 1_000 * 1e18;

            require(btc.transferFrom(deployer, address(pool), btcAmount), "BTC transfer failed");
            require(fan.transferFrom(deployer, address(pool), fanAmount), "Fan token transfer failed");

            pool.setInitialLiquidity(btcAmount, fanAmount);
            console.log("Initialized:", tokenNames[i]);
        }

        vm.stopBroadcast();
    }
}
