// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "forge-std/Script.sol";
import "../src/FanTokenDAO.sol";

contract FanTokenDAOScript is Script {
    function run() external {
        uint256 privateKey = vm.envUint("PRIVATE_KEY");
        vm.startBroadcast(privateKey);

        // FanTokenDAO 컨트랙트 배포
        FanTokenDAO dao = new FanTokenDAO();
        console.log("FanTokenDAO deployed at:", address(dao));
        
        // 보상풀 주소 설정
        address rewardPool = vm.envAddress("REWARD_POOL");
        dao.setRewardPool(rewardPool);
        console.log("Reward pool set to:", rewardPool);
        
        // 팬토큰 주소 목록
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

        // 팀 토큰 설정 (teamId는 1부터 시작)
        for (uint i = 0; i < tokenNames.length; i++) {
            dao.setTeamToken(i + 1, fanTokenAddresses[i]);
            console.log(string.concat("Team ", vm.toString(i + 1), " (", tokenNames[i], ") token set to: ", vm.toString(fanTokenAddresses[i])));
        }

        vm.stopBroadcast();
    }
}