// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.25;

import "forge-std/Script.sol";
import "../src/LiquidityPool.sol";
import "../src/Token.sol"; // FanToken 컨트랙트

contract InitLiquidity is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        vm.startBroadcast(privateKey);

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

        for (uint i = 0; i < 10; i++) {
            // 풀 주소 재생성 (테스트넷에선 재배포 필요할 경우 대비)
            LiquidityPool pool = new LiquidityPool(btcAddress, fanTokenAddresses[i]);
            console.log(string.concat(tokenNames[i], " Pool deployed at:"), address(pool));

            // 팬토큰, BTC 모두 mint
            Token(fanTokenAddresses[i]).mint(deployer, 1_000 * 1e18);
            Token(btcAddress).mint(deployer, 10_000 * 1e18);

            // approve
            IERC20(fanTokenAddresses[i]).approve(address(pool), 1_000 * 1e18);
            IERC20(btcAddress).approve(address(pool), 10_000 * 1e18);

            // setInitialLiquidity
            pool.setInitialLiquidity(10_000 * 1e18, 1_000 * 1e18);
        }

        vm.stopBroadcast();
    }
}
