import { ethers } from "ethers";
import type { IProvider } from "@web3auth/base";
import { web3auth } from "./web3auth";
import FanTokenDAOABI from "../../abi/FanTokenDAO.json";
import { useWalletStore } from "../stores/walletStore";

// 컨트랙트 주소 (환경 변수에서 가져오기)
const DAO_CONTRACT_ADDRESS = import.meta.env.VITE_DAO_CONTRACT_ADDRESS;
const RPC_URL = import.meta.env.VITE_RPC_URL;

/**
 * 컨트랙트 인스턴스 생성
 */
export const getDAOContract = async () => {
  try {
    if (!web3auth.provider) {
      throw new Error("Web3Auth 인증이 필요합니다");
    }

    const provider = new ethers.BrowserProvider(web3auth.provider);
    const signer = await provider.getSigner();
    
    return new ethers.Contract(DAO_CONTRACT_ADDRESS, FanTokenDAOABI.abi, signer);
  } catch (error: any) {
    console.error("DAO 컨트랙트 연결 실패:", error);
    throw new Error(`컨트랙트 연결 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 안건 등록 - 스마트 컨트랙트에 해시 등록
 * @param proposalId 안건 ID
 * @param contentHash 안건 해시 (백엔드에서 생성)
 * @param teamId 팀 ID
 * @returns 트랜잭션 해시
 */
export const registerProposalOnChain = async (
  proposalId: number,
  contentHash: string,
  teamId: number
): Promise<string> => {
  try {
    // 안건 마감일 설정 (현재 시간으로부터 7일 후)
    const deadline = Math.floor(Date.now() / 1000) + 7 * 24 * 60 * 60;
    
    // 개인키 기반 방식으로 변경
    const walletStore = useWalletStore.getState();
    const privateKey = await walletStore.exportPrivateKey();
    
    if (!privateKey) {
      throw new Error("개인키를 가져올 수 없습니다");
    }
    
    // JsonRpcProvider 사용
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const wallet = new ethers.Wallet(privateKey, provider);
    
    // 컨트랙트 인스턴스 생성
    const daoContract = new ethers.Contract(
      DAO_CONTRACT_ADDRESS, 
      FanTokenDAOABI.abi, 
      wallet
    );
    
    // contentHash가 0x로 시작하지 않으면 추가
    const formattedHash = contentHash.startsWith("0x") ? contentHash : `0x${contentHash}`;

    // 트랜잭션 실행 (가스 가격 0으로 설정)
    const tx = await daoContract.registerProposal(
      proposalId, 
      formattedHash, 
      teamId, 
      deadline, 
      { gasPrice: 0 }
    );
    
    // 트랜잭션 완료 대기
    const receipt = await tx.wait();
    
    // 트랜잭션 해시 반환
    return receipt.hash;
  } catch (error: any) {
    console.error("안건 등록 트랜잭션 실패:", error);
    throw new Error(`스마트 컨트랙트 안건 등록 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 안건 투표
 * @param proposalId 안건 ID
 * @returns 트랜잭션 해시
 */
export const voteOnProposal = async (proposalId: number): Promise<string> => {
  try {
    // 개인키 가져오기
    const walletStore = useWalletStore.getState();
    const privateKey = await walletStore.exportPrivateKey();
    
    if (!privateKey) {
      throw new Error("개인키를 가져올 수 없습니다");
    }
    
    // JsonRpcProvider 사용
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const wallet = new ethers.Wallet(privateKey, provider);
    
    // 컨트랙트 인스턴스 생성
    const daoContract = new ethers.Contract(
      DAO_CONTRACT_ADDRESS, 
      FanTokenDAOABI.abi, 
      wallet
    );
    
    // 가스 가격 0으로 설정 가능
    const tx = await daoContract.vote(proposalId, { gasPrice: 0 });
    
    const receipt = await tx.wait();
    return receipt.hash;
  } catch (error: any) {
    console.error("안건 투표 트랜잭션 실패:", error);
    throw new Error(`안건 투표 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 투표 여부 확인
 * @param proposalId 안건 ID
 * @param address 사용자 주소
 * @returns 투표 여부
 */
export const checkVoted = async (proposalId: number, address: string): Promise<boolean> => {
  try {
    // JsonRpcProvider 사용 (읽기 전용 조작이므로 개인키 필요 없음)
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    
    // 컨트랙트 인스턴스 생성 (읽기 전용)
    const daoContract = new ethers.Contract(
      DAO_CONTRACT_ADDRESS, 
      FanTokenDAOABI.abi, 
      provider
    );
    
    // 투표 여부 확인
    return await daoContract.hasVoted(proposalId, address);
  } catch (error: any) {
    console.error("투표 여부 확인 실패:", error);
    throw new Error(`투표 여부 확인 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 안건 상세 정보 조회
 * @param proposalId 안건 ID
 * @returns 안건 상세 정보
 */
export const getProposalDetailsOnChain = async (proposalId: number) => {
  try {
    // JsonRpcProvider 사용 (읽기 전용 조작이므로 개인키 필요 없음)
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    
    // 컨트랙트 인스턴스 생성 (읽기 전용)
    const daoContract = new ethers.Contract(
      DAO_CONTRACT_ADDRESS, 
      FanTokenDAOABI.abi, 
      provider
    );
    
    // 안건 상세 정보 조회
    const result = await daoContract.getProposalDetails(proposalId);
    
    return {
      contentHash: result[0],
      teamId: Number(result[1]),
      voteCount: Number(result[2]),
      executed: result[3],
      deadline: new Date(Number(result[4]) * 1000),
      proposer: result[5]
    };
  } catch (error: any) {
    console.error("안건 상세 정보 조회 실패:", error);
    throw new Error(`안건 상세 정보 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
}; 