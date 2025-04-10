import { ethers } from "ethers";
import ExchangeABI from "../../abi/Exchange.json";
import { web3auth } from "./web3auth";

const EXCHANGE_ADDRESS = import.meta.env.VITE_EXCHANGE_ADDRESS;
const RPC_URL = import.meta.env.VITE_RPC_URL;

export const getExchangeContract = async (): Promise<ethers.Contract> => {
  try {
    if (!web3auth.provider) {
      throw new Error("Web3Auth 인증이 필요합니다");
    }

    const provider = new ethers.BrowserProvider(web3auth.provider);
    const signer = await provider.getSigner();

    return new ethers.Contract(EXCHANGE_ADDRESS, ExchangeABI, signer);
  } catch (error: any) {
    console.error("Exchange 컨트랙트 연결 실패:", error);
    throw new Error(`컨트랙트 연결 실패: ${error.message || "알 수 없는 오류"}`);
  }
};