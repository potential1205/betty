import { ethers } from "ethers";
import MVPVotingABI from "../../abi/MVPVoting.json";
import { getTeamTokenAddress } from "../apis/tokenApi";
import { useWalletStore } from "../stores/walletStore";

// 환경 변수
const RPC_URL = import.meta.env.VITE_RPC_URL;
const CONTRACT_ADDRESS = import.meta.env.VITE_MVP_VOTING_CONTRACT_ADDRESS || '';

/**
 * MVP 게임 정보 타입 정의
 */
export interface MVPGameInfo {
  gameId: number;
  finalized: boolean;
  winningPlayerId: number;
  startTime: number;
  totalVoteAmount: bigint;
  totalWinnerVoteAmount: bigint;
}

/**
 * MVP 투표 정보 타입 정의
 */
export interface MVPVoteInfo {
  amount: string;
  playerId: number;
  tokenAddress: string;
  claimed: boolean;
}

/**
 * 기본 컨트랙트 주소를 반환합니다.
 * @returns MVPVoting 컨트랙트 주소
 */
export const getDefaultMVPContractAddress = (): string => {
  return CONTRACT_ADDRESS;
};

/**
 * 개인키로 MVPVoting 컨트랙트와 상호작용하는 인스턴스를 생성합니다.
 * @param privateKey 트랜잭션에 서명하기 위한 개인키
 * @param contractAddress MVPVoting 컨트랙트 주소
 * @returns 서명자로 연결된 컨트랙트 인스턴스
 */
export const getMVPVotingContract = (privateKey: string, contractAddress: string = CONTRACT_ADDRESS) => {
  try {
    if (!privateKey || !contractAddress) {
      throw new Error("개인키와 컨트랙트 주소가 필요합니다");
    }

    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const wallet = new ethers.Wallet(privateKey, provider);
    
    return new ethers.Contract(contractAddress, MVPVotingABI.abi, wallet);
  } catch (error: any) {
    console.error("MVPVoting 컨트랙트 연결 실패:", error);
    throw new Error(`컨트랙트 연결 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 읽기 전용 MVPVoting 컨트랙트 인스턴스를 생성합니다.
 * @param contractAddress MVPVoting 컨트랙트 주소
 * @returns 읽기 전용 컨트랙트 인스턴스
 */
export const getMVPVotingContractReadOnly = (contractAddress: string = CONTRACT_ADDRESS) => {
  try {
    if (!contractAddress) {
      throw new Error("컨트랙트 주소가 필요합니다");
    }

    const provider = new ethers.JsonRpcProvider(RPC_URL);
    return new ethers.Contract(contractAddress, MVPVotingABI.abi, provider);
  } catch (error: any) {
    console.error("MVPVoting 컨트랙트 연결 실패:", error);
    throw new Error(`컨트랙트 연결 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * MVP 게임이 존재하는지 확인합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns 게임 존재 여부
 */
export const checkMVPGameExists = async (
  contract: ethers.Contract,
  gameId: number
): Promise<boolean> => {
  try {
    const gameInfo = await contract.games(gameId);
    // gameId가 0이 아니면 게임이 존재함
    return Number(gameInfo[0]) > 0;
  } catch (error: any) {
    console.error(`게임 ${gameId} 존재 여부 확인 실패:`, error);
    return false;
  }
};

/**
 * MVP 게임 생성 함수 (오너만 가능)
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param playerIds 선수 ID 배열
 * @param tokenAddresses 각 선수 소속팀의 토큰 주소 배열
 * @param startTime 투표 마감 시간 (Unix 타임스탬프)
 * @returns 트랜잭션 영수증
 */
export const createMVPGame = async (
  contract: ethers.Contract,
  gameId: number,
  playerIds: number[],
  tokenAddresses: string[],
  startTime: number
) => {
  try {
    const tx = await contract.createMVPGame(
      gameId,
      playerIds,
      tokenAddresses,
      startTime,
      { gasPrice: 0 } // 가스 가격을 0으로 설정
    );
    return await tx.wait();
  } catch (error: any) {
    console.error("MVP 게임 생성 실패:", error);
    throw new Error(`MVP 게임 생성 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * MVP 게임 정보 조회
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns MVP 게임 정보
 */
export const getMVPGameInfo = async (contract: ethers.Contract, gameId: number): Promise<MVPGameInfo> => {
  try {
    const gameInfo = await contract.games(gameId);
    
    return {
      gameId: Number(gameInfo[0]),
      finalized: gameInfo[1],
      winningPlayerId: Number(gameInfo[2]),
      startTime: Number(gameInfo[3]),
      totalVoteAmount: BigInt(gameInfo[4].toString()),
      totalWinnerVoteAmount: BigInt(gameInfo[5].toString())
    };
  } catch (error: any) {
    console.error("MVP 게임 정보 조회 실패:", error);
    throw new Error(`MVP 게임 정보 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * MVP 투표 함수
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param playerId 선수 ID
 * @param amount 투표 금액
 * @returns 트랜잭션 영수증
 */
export const voteMVP = async (
  contract: ethers.Contract,
  gameId: number,
  playerId: number,
  amount: string
) => {
  try {
    // 먼저 게임이 존재하는지 확인
    const gameExists = await checkMVPGameExists(contract, gameId);
    if (!gameExists) {
      throw new Error(`MVP 게임 ID ${gameId}가 존재하지 않습니다. 먼저 게임을 생성해주세요.`);
    }
    
    const amountWei = ethers.parseEther(amount);
    
    // 지갑 정보 가져오기
    const walletStore = useWalletStore.getState();
    const walletAddress = await walletStore.getAccounts();
    
    console.log(`지갑 주소: ${walletAddress}`);
    
    if (!walletAddress || !ethers.isAddress(walletAddress)) {
      throw new Error("유효한 지갑 주소를 가져올 수 없습니다");
    }
    
    // 선수의 토큰 주소 확인
    const tokenAddress = await contract.playerToToken(gameId, playerId);
    if (tokenAddress === "0x0000000000000000000000000000000000000000") {
      throw new Error(`선수 ID ${playerId}에 대한 토큰 주소가 설정되지 않았습니다`);
    }
    
    console.log(`선수 소속 토큰 주소: ${tokenAddress}`);
    
    // 개인키 가져오기
    const privateKey = await walletStore.exportPrivateKey();
    if (!privateKey) {
      throw new Error("개인키를 가져올 수 없습니다");
    }
    
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const wallet = new ethers.Wallet(privateKey, provider);
    
    // 토큰 컨트랙트 인스턴스 생성
    const erc20ABI = [
      "function allowance(address owner, address spender) view returns (uint256)",
      "function approve(address spender, uint256 amount) returns (bool)",
      "function balanceOf(address owner) view returns (uint256)"
    ];
    
    const tokenContract = new ethers.Contract(tokenAddress, erc20ABI, wallet);
    
    // 토큰 잔액 확인
    const balance = await tokenContract.balanceOf(walletAddress);
    console.log(`토큰 잔액: ${ethers.formatEther(balance)}`);
    
    if (balance < amountWei) {
      throw new Error(`토큰 잔액이 부족합니다. 필요: ${amount}, 보유: ${ethers.formatEther(balance)}`);
    }
    
    // 토큰 승인 확인
    const allowance = await tokenContract.allowance(walletAddress, contract.target);
    console.log(`현재 승인량: ${ethers.formatEther(allowance)}, 필요한 승인량: ${amount}`);
    
    // 승인량이 부족하면 승인 요청
    if (allowance < amountWei) {
      console.log('토큰 승인이 필요합니다...');
      const approveTx = await tokenContract.approve(contract.target, amountWei, { gasPrice: 0 });
      
      // 승인 트랜잭션이 블록에 포함될 때까지 대기
      const approveReceipt = await approveTx.wait();
      console.log('토큰 승인 완료! 트랜잭션 해시:', approveReceipt.hash);
    }
    
    // 승인이 완료된 후 MVP 투표 진행
    console.log(`MVP 투표 시작: 게임 ID ${gameId}, 선수 ID ${playerId}, 금액 ${amount}`);
    const tx = await contract.voteMVP(gameId, playerId, amountWei, { gasPrice: 0 });
    console.log(`MVP 투표 트랜잭션 전송 완료: ${tx.hash}`);
    return await tx.wait();
  } catch (error: any) {
    console.error("MVP 투표 실패:", error);
    throw new Error(`MVP 투표 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * MVP 게임 종료 및 우승자 설정 함수 (오너만 가능)
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param winningPlayerId 우승 선수 ID
 * @returns 트랜잭션 영수증
 */
export const finalizeMVP = async (
  contract: ethers.Contract,
  gameId: number,
  winningPlayerId: number
) => {
  try {
    const tx = await contract.finalizeMVP(gameId, winningPlayerId, { gasPrice: 0 });
    return await tx.wait();
  } catch (error: any) {
    console.error("MVP 게임 종료 실패:", error);
    throw new Error(`MVP 게임 종료 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 사용자의 MVP 투표 정보 조회
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param userAddress 사용자 주소
 * @returns MVP 투표 정보
 */
export const getUserMVPVote = async (
  contract: ethers.Contract,
  gameId: number,
  userAddress: string
): Promise<MVPVoteInfo> => {
  try {
    const voteInfo = await contract.getUserVote(gameId, userAddress);
    return {
      amount: ethers.formatEther(voteInfo[0]),
      playerId: Number(voteInfo[1]),
      tokenAddress: voteInfo[2],
      claimed: voteInfo[3]
    };
  } catch (error: any) {
    console.error("MVP 투표 정보 조회 실패:", error);
    throw new Error(`MVP 투표 정보 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 선수별 총 투표량 조회
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param playerId 선수 ID
 * @returns 총 투표량
 */
export const getTotalVotes = async (
  contract: ethers.Contract,
  gameId: number,
  playerId: number
): Promise<string> => {
  try {
    const totalVotes = await contract.getTotalVotes(gameId, playerId);
    return ethers.formatEther(totalVotes);
  } catch (error: any) {
    console.error(`선수 ${playerId}의 총 투표량 조회 실패:`, error);
    throw new Error(`총 투표량 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * MVP 보상 청구 함수
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns 트랜잭션 영수증
 */
export const claimMVPReward = async (contract: ethers.Contract, gameId: number) => {
  try {
    const tx = await contract.claimMVPReward(gameId, { gasPrice: 0 });
    return await tx.wait();
  } catch (error: any) {
    console.error("MVP 보상 청구 실패:", error);
    throw new Error(`MVP 보상 청구 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 관리자가 사용자 대신 MVP 보상 청구 함수 (오너만 가능)
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param userAddress 사용자 주소
 * @returns 트랜잭션 영수증
 */
export const claimMVPRewardForUser = async (
  contract: ethers.Contract,
  gameId: number,
  userAddress: string
) => {
  try {
    const tx = await contract.claimMVPRewardForUser(gameId, userAddress, { gasPrice: 0 });
    return await tx.wait();
  } catch (error: any) {
    console.error("사용자 MVP 보상 청구 실패:", error);
    throw new Error(`사용자 MVP 보상 청구 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 특정 선수에게 투표한 사용자 주소 목록 조회
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param playerId 선수 ID
 * @returns 선수에게 투표한 사용자 주소 배열
 */
export const getPlayerVoters = async (
  contract: ethers.Contract,
  gameId: number,
  playerId: number
): Promise<string[]> => {
  try {
    const voterAddresses = await contract.getPlayerVoters(gameId, playerId);
    return voterAddresses;
  } catch (error: any) {
    console.error(`선수 ${playerId}의 투표자 목록 조회 실패:`, error);
    throw new Error(`선수 투표자 목록 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * MVP 게임에 투표한 모든 사용자 주소 목록 조회
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns 게임에 투표한 모든 사용자 주소 배열
 */
export const getAllGameVoters = async (
  contract: ethers.Contract,
  gameId: number
): Promise<string[]> => {
  try {
    const voterAddresses = await contract.getAllGameVoters(gameId);
    return voterAddresses;
  } catch (error: any) {
    console.error(`게임 ${gameId}의 모든 투표자 목록 조회 실패:`, error);
    throw new Error(`게임 투표자 목록 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 우승자(정답자)에게 투표한 사용자 주소 목록 조회
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns 우승 선수에게 투표한 사용자 주소 배열
 */
export const getWinningVoters = async (
  contract: ethers.Contract,
  gameId: number
): Promise<string[]> => {
  try {
    // 게임이 종료되었는지 확인
    const gameInfo = await getMVPGameInfo(contract, gameId);
    if (!gameInfo.finalized) {
      throw new Error("게임이 아직 종료되지 않았습니다");
    }
    
    const voterAddresses = await contract.getWinningVoters(gameId);
    return voterAddresses;
  } catch (error: any) {
    console.error(`게임 ${gameId}의 우승자 투표자 목록 조회 실패:`, error);
    throw new Error(`우승자 투표자 목록 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 패배자(오답자)에게 투표한 사용자 주소 목록 조회
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns 패배한 선수들에게 투표한 사용자 주소 배열
 */
export const getLosingVoters = async (
  contract: ethers.Contract,
  gameId: number
): Promise<string[]> => {
  try {
    // 게임이 종료되었는지 확인
    const gameInfo = await getMVPGameInfo(contract, gameId);
    if (!gameInfo.finalized) {
      throw new Error("게임이 아직 종료되지 않았습니다");
    }
    
    const voterAddresses = await contract.getLosingVoters(gameId);
    return voterAddresses;
  } catch (error: any) {
    console.error(`게임 ${gameId}의 패배자 투표자 목록 조회 실패:`, error);
    throw new Error(`패배자 투표자 목록 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};
