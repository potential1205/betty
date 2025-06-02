import { ethers } from 'ethers';
import Exchange from '../../abi/Exchange.json';
import Token from '../../abi/Token.json';
import { useWalletStore } from '../stores/walletStore';

// 환경 변수
const RPC_URL = import.meta.env.VITE_RPC_URL;
const CONTRACT_ADDRESS = import.meta.env.VITE_EXCHANGE_CONTRACT_ADDRESS;

/**
 * 개인키로 Exchange 컨트랙트와 상호작용하는 인스턴스를 생성합니다.
 * @param privateKey 트랜잭션에 서명하기 위한 개인키
 * @param contractAddress Exchange 컨트랙트 주소
 * @returns 서명자로 연결된 컨트랙트 인스턴스
 */
export const getExchangeContract = (privateKey: string, contractAddress: string = CONTRACT_ADDRESS) => {
  try {
    if (!privateKey || !contractAddress) {
      throw new Error("개인키와 컨트랙트 주소가 필요합니다");
    }

    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const wallet = new ethers.Wallet(privateKey, provider);
    
    return new ethers.Contract(contractAddress, Exchange.abi, wallet);
  } catch (error: any) {
    console.error("Exchange 컨트랙트 연결 실패:", error);
    throw new Error(`컨트랙트 연결 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 읽기 전용 Exchange 컨트랙트 인스턴스를 생성합니다.
 * @param contractAddress Exchange 컨트랙트 주소
 * @returns 읽기 전용 컨트랙트 인스턴스
 */
export const getExchangeContractReadOnly = (contractAddress: string = CONTRACT_ADDRESS) => {
  try {
    if (!contractAddress) {
      throw new Error("컨트랙트 주소가 필요합니다");
    }

    const provider = new ethers.JsonRpcProvider(RPC_URL);
    return new ethers.Contract(contractAddress, Exchange.abi, provider);
  } catch (error: any) {
    console.error("Exchange 컨트랙트 연결 실패:", error);
    throw new Error(`컨트랙트 연결 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * BET 토큰을 충전합니다 (add 함수)
 * @param contract 컨트랙트 인스턴스
 * @param amountBET 충전할 BET 토큰 양
 * @returns 트랜잭션 영수증
 */
export const addBetToken = async (
  contract: ethers.Contract,
  amountBET: string
) => {
  try {
    console.log(`BET 토큰 충전 시작: ${amountBET} BET`);

    // BET 토큰 양을 Wei 단위로 변환
    const amountWei = ethers.parseEther(amountBET);

    // 지갑 정보 가져오기
    const walletStore = useWalletStore.getState();
    const walletAddress = await walletStore.getAccounts();
    
    if (!walletAddress || !ethers.isAddress(walletAddress)) {
      throw new Error("유효한 지갑 주소를 가져올 수 없습니다");
    }

    // BET 토큰 컨트랙트 주소 가져오기
    const betTokenAddress = await contract.betToken();
    
    // BET 토큰 승인 확인 및 처리
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const privateKey = await walletStore.exportPrivateKey();
    if (!privateKey) {
      throw new Error("개인키를 가져올 수 없습니다");
    }
    
    const wallet = new ethers.Wallet(privateKey, provider);
    const betTokenContract = new ethers.Contract(betTokenAddress, Token.abi, wallet);

    // 승인 상태 확인
    const allowance = await betTokenContract.allowance(walletAddress, contract.target);
    console.log(`현재 승인량: ${ethers.formatEther(allowance)}, 필요한 승인량: ${amountBET}`);

    // 승인량이 부족하면 승인 요청
    if (allowance < amountWei) {
      console.log('BET 토큰 승인이 필요합니다...');
      const approveTx = await betTokenContract.approve(contract.target, amountWei, { gasPrice: ethers.parseUnits('5', 'gwei') });
      await approveTx.wait();
      console.log('BET 토큰 승인 완료!');
    }

    // BET 토큰 충전 실행
    console.log(`BET 토큰 충전 실행: ${amountBET} BET`);
    const tx = await contract.add(amountWei, { gasPrice: ethers.parseUnits('5', 'gwei') });
    const receipt = await tx.wait();
    
    console.log('BET 토큰 충전 완료!', receipt);
    return receipt;
  } catch (error: any) {
    console.error("BET 토큰 충전 실패:", error);
    throw new Error(`BET 토큰 충전 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * BET 토큰을 출금합니다 (remove 함수)
 * @param contract 컨트랙트 인스턴스
 * @param amountBET 출금할 BET 토큰 양
 * @returns 트랜잭션 영수증
 */
export const removeBetToken = async (
  contract: ethers.Contract,
  amountBET: string
) => {
  try {
    console.log(`BET 토큰 출금 시작: ${amountBET} BET`);

    // BET 토큰 양을 Wei 단위로 변환
    const amountWei = ethers.parseEther(amountBET);
    
    // BET 토큰 출금 실행
    console.log(`BET 토큰 출금 실행: ${amountBET} BET`);
    const tx = await contract.remove(amountWei, { gasPrice: ethers.parseUnits('5', 'gwei') });
    const receipt = await tx.wait();
    
    console.log('BET 토큰 출금 완료!', receipt);
    return receipt;
  } catch (error: any) {
    console.error("BET 토큰 출금 실패:", error);
    throw new Error(`BET 토큰 출금 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 팬토큰을 구매합니다.
 * @param contract 컨트랙트 인스턴스
 * @param tokenName 팬토큰 이름
 * @param amountBET 구매할 BET 토큰 양
 * @returns 트랜잭션 영수증
 */
export const buyFanToken = async (
  contract: ethers.Contract,
  tokenName: string,
  amountBET: string
) => {
  try {
    console.log(`팬토큰 구매 시작: ${tokenName}, ${amountBET} BET`);

    // BET 토큰 양을 Wei 단위로 변환
    const amountWei = ethers.parseEther(amountBET);

    // 지갑 정보 가져오기
    const walletStore = useWalletStore.getState();
    const walletAddress = await walletStore.getAccounts();
    
    if (!walletAddress || !ethers.isAddress(walletAddress)) {
      throw new Error("유효한 지갑 주소를 가져올 수 없습니다");
    }

    // BET 토큰 컨트랙트 주소 가져오기
    const betTokenAddress = await contract.betToken();
    
    // BET 토큰 컨트랙트 인스턴스 생성
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const privateKey = await walletStore.exportPrivateKey();
    if (!privateKey) {
      throw new Error("개인키를 가져올 수 없습니다");
    }
    
    const wallet = new ethers.Wallet(privateKey, provider);
    const betTokenContract = new ethers.Contract(betTokenAddress, Token.abi, wallet);

    // 승인 상태 확인
    const allowance = await betTokenContract.allowance(walletAddress, contract.target);
    console.log(`현재 승인량: ${ethers.formatEther(allowance)}, 필요한 승인량: ${amountBET}`);

    // 승인량이 부족하면 승인 요청
    if (allowance < amountWei) {
      console.log('BET 토큰 승인이 필요합니다...');
      const approveTx = await betTokenContract.approve(contract.target, amountWei, { gasPrice: ethers.parseUnits('5', 'gwei') });
      await approveTx.wait();
      console.log('BET 토큰 승인 완료!');
    }

    // 팬토큰 구매 실행
    console.log(`팬토큰 구매 실행: ${tokenName}, ${amountBET} BET`);
    const tx = await contract.buy(tokenName, amountWei, { gasPrice: ethers.parseUnits('5', 'gwei') });
    const receipt = await tx.wait();
    
    console.log('팬토큰 구매 완료!', receipt);
    return receipt;
  } catch (error: any) {
    console.error("팬토큰 구매 실패:", error);
    if (error.message?.includes("missing revert data")) {
      throw new Error("컨트랙트 실행 중 오류가 발생했습니다. 잔액을 확인하거나 네트워크 상태를 확인해주세요.");
    }
    throw new Error(`팬토큰 구매 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 현재 팬토큰의 가격을 조회합니다.
 * @param contract 컨트랙트 인스턴스
 * @param tokenName 팬토큰 이름
 * @returns 팬토큰 가격 (BET 기준)
 */
export const getFanTokenPrice = async (
  contract: ethers.Contract,
  tokenName: string
): Promise<string> => {
  try {
    const price = await contract.getPrice(tokenName);
    return ethers.formatEther(price);
  } catch (error: any) {
    console.error(`팬토큰 ${tokenName} 가격 조회 실패:`, error);
    throw new Error(`가격 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 팬토큰을 판매합니다.
 * @param contract 컨트랙트 인스턴스
 * @param tokenName 판매할 팬토큰 이름
 * @param amountFanToken 판매할 팬토큰 양
 * @returns 트랜잭션 영수증
 */
export const sellFanToken = async (
  contract: ethers.Contract,
  tokenName: string,
  amountFanToken: string
) => {
  try {
    console.log(`팬토큰 판매 시작: ${tokenName}, ${amountFanToken} ${tokenName}`);

    // 팬토큰 양을 Wei 단위로 변환
    const amountWei = ethers.parseEther(amountFanToken);

    // 지갑 정보 가져오기
    const walletStore = useWalletStore.getState();
    const walletAddress = await walletStore.getAccounts();
    
    if (!walletAddress || !ethers.isAddress(walletAddress)) {
      throw new Error("유효한 지갑 주소를 가져올 수 없습니다");
    }

    // 팬토큰 컨트랙트 주소 가져오기
    const fanTokenAddress = await contract.fanTokens(tokenName);
    
    // 팬토큰 컨트랙트 인스턴스 생성
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const privateKey = await walletStore.exportPrivateKey();
    if (!privateKey) {
      throw new Error("개인키를 가져올 수 없습니다");
    }
    
    const wallet = new ethers.Wallet(privateKey, provider);
    const fanTokenContract = new ethers.Contract(fanTokenAddress, Token.abi, wallet);

    // 승인 상태 확인
    const allowance = await fanTokenContract.allowance(walletAddress, contract.target);
    console.log(`현재 승인량: ${ethers.formatEther(allowance)}, 필요한 승인량: ${amountFanToken}`);

    // 승인량이 부족하면 승인 요청
    if (allowance < amountWei) {
      console.log('팬토큰 승인이 필요합니다...');
      const approveTx = await fanTokenContract.approve(contract.target, amountWei, { gasPrice: ethers.parseUnits('5', 'gwei') });
      await approveTx.wait();
      console.log('팬토큰 승인 완료!');
    }

    // 팬토큰 판매 실행
    console.log(`팬토큰 판매 실행: ${tokenName}, ${amountFanToken}`);
    const tx = await contract.sell(tokenName, amountWei, { gasPrice: ethers.parseUnits('5', 'gwei') });
    const receipt = await tx.wait();
    
    console.log('팬토큰 판매 완료!', receipt);
    return receipt;
  } catch (error: any) {
    console.error("팬토큰 판매 실패:", error);
    if (error.message?.includes("missing revert data")) {
      throw new Error("컨트랙트 실행 중 오류가 발생했습니다. 잔액을 확인하거나 네트워크 상태를 확인해주세요.");
    }
    throw new Error(`팬토큰 판매 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 팬토큰을 다른 팬토큰으로 스왑합니다.
 * @param contract 컨트랙트 인스턴스
 * @param tokenFrom 스왑할 토큰 이름
 * @param tokenTo 스왑 받을 토큰 이름
 * @param amountFanToken 스왑할 팬토큰 양
 * @returns 트랜잭션 영수증
 */
export const swapFanToken = async (
  contract: ethers.Contract,
  tokenFrom: string,
  tokenTo: string,
  amountFanToken: string
) => {
  try {
    console.log(`팬토큰 스왑 시작: ${tokenFrom} -> ${tokenTo}, ${amountFanToken} ${tokenFrom}`);

    // 팬토큰 양을 Wei 단위로 변환
    const amountWei = ethers.parseEther(amountFanToken);

    // 지갑 정보 가져오기
    const walletStore = useWalletStore.getState();
    const walletAddress = await walletStore.getAccounts();
    
    if (!walletAddress || !ethers.isAddress(walletAddress)) {
      throw new Error("유효한 지갑 주소를 가져올 수 없습니다");
    }

    // 소스 팬토큰 컨트랙트 주소 가져오기
    const fromTokenAddress = await contract.fanTokens(tokenFrom);
    
    // 소스 팬토큰 컨트랙트 인스턴스 생성
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const privateKey = await walletStore.exportPrivateKey();
    if (!privateKey) {
      throw new Error("개인키를 가져올 수 없습니다");
    }
    
    const wallet = new ethers.Wallet(privateKey, provider);
    const fromTokenContract = new ethers.Contract(fromTokenAddress, Token.abi, wallet);

    // 승인 상태 확인
    const allowance = await fromTokenContract.allowance(walletAddress, contract.target);
    console.log(`현재 승인량: ${ethers.formatEther(allowance)}, 필요한 승인량: ${amountFanToken}`);

    // 승인량이 부족하면 승인 요청
    if (allowance < amountWei) {
      console.log('팬토큰 승인이 필요합니다...');
      const approveTx = await fromTokenContract.approve(contract.target, amountWei, { gasPrice: ethers.parseUnits('5', 'gwei') });
      await approveTx.wait();
      console.log('팬토큰 승인 완료!');
    }

    // 팬토큰 스왑 실행
    console.log(`팬토큰 스왑 실행: ${tokenFrom} -> ${tokenTo}, ${amountFanToken}`);
    const tx = await contract.swap(tokenFrom, tokenTo, amountWei, { gasPrice: ethers.parseUnits('5', 'gwei') });
    const receipt = await tx.wait();
    
    console.log('팬토큰 스왑 완료!', receipt);
    return receipt;
  } catch (error: any) {
    console.error("팬토큰 스왑 실패:", error);
    if (error.message?.includes("missing revert data")) {
      throw new Error("컨트랙트 실행 중 오류가 발생했습니다. 잔액을 확인하거나 네트워크 상태를 확인해주세요.");
    }
    throw new Error(`팬토큰 스왑 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 팬토큰을 사용(소각)합니다.
 * @param contract 컨트랙트 인스턴스
 * @param tokenName 소각할 토큰 이름
 * @param amount 소각할 양
 * @returns 트랜잭션 영수증
 */
export const useFanToken = async (
  contract: ethers.Contract,
  tokenName: string,
  amount: string
) => {
  try {
    console.log(`팬토큰 사용(소각) 시작: ${tokenName}, ${amount}`);

    // 팬토큰 양을 Wei 단위로 변환
    const amountWei = ethers.parseEther(amount);

    // 지갑 정보 가져오기
    const walletStore = useWalletStore.getState();
    const walletAddress = await walletStore.getAccounts();
    
    if (!walletAddress || !ethers.isAddress(walletAddress)) {
      throw new Error("유효한 지갑 주소를 가져올 수 없습니다");
    }

    // 팬토큰 컨트랙트 주소 가져오기
    const fanTokenAddress = await contract.fanTokens(tokenName);
    
    // 팬토큰 컨트랙트 인스턴스 생성
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const privateKey = await walletStore.exportPrivateKey();
    if (!privateKey) {
      throw new Error("개인키를 가져올 수 없습니다");
    }
    
    const wallet = new ethers.Wallet(privateKey, provider);
    const fanTokenContract = new ethers.Contract(fanTokenAddress, Token.abi, wallet);

    // 승인 상태 확인
    const allowance = await fanTokenContract.allowance(walletAddress, contract.target);
    console.log(`현재 승인량: ${ethers.formatEther(allowance)}, 필요한 승인량: ${amount}`);

    // 승인량이 부족하면 승인 요청
    if (allowance < amountWei) {
      console.log('팬토큰 승인이 필요합니다...');
      const approveTx = await fanTokenContract.approve(contract.target, amountWei, { gasPrice: ethers.parseUnits('5', 'gwei') });
      await approveTx.wait();
      console.log('팬토큰 승인 완료!');
    }

    // 팬토큰 사용(소각) 실행
    console.log(`팬토큰 사용(소각) 실행: ${tokenName}, ${amount}`);
    const tx = await contract.use(tokenName, amountWei, { gasPrice: ethers.parseUnits('5', 'gwei') });
    const receipt = await tx.wait();
    
    console.log('팬토큰 사용(소각) 완료!', receipt);
    return receipt;
  } catch (error: any) {
    console.error("팬토큰 사용(소각) 실패:", error);
    if (error.message?.includes("missing revert data")) {
      throw new Error("컨트랙트 실행 중 오류가 발생했습니다. 잔액을 확인하거나 네트워크 상태를 확인해주세요.");
    }
    throw new Error(`팬토큰 사용(소각) 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 모든 팬토큰 가격을 조회합니다.
 * @param contract 컨트랙트 인스턴스
 * @param tokenNames 토큰 이름 목록
 * @returns 토큰 가격 맵
 */
export const getAllFanTokenPrices = async (
  contract: ethers.Contract,
  tokenNames: string[]
): Promise<{ [tokenName: string]: string }> => {
  try {
    const pricePromises = tokenNames.map(async (tokenName) => {
      try {
        const price = await getFanTokenPrice(contract, tokenName);
        return { tokenName, price };
      } catch (error) {
        console.error(`${tokenName} 가격 조회 실패:`, error);
        return { tokenName, price: '0' };
      }
    });

    const prices = await Promise.all(pricePromises);
    const priceMap: { [tokenName: string]: string } = {};
    
    prices.forEach(({ tokenName, price }) => {
      priceMap[tokenName] = price;
    });

    return priceMap;
  } catch (error: any) {
    console.error("모든 팬토큰 가격 조회 실패:", error);
    throw new Error(`모든 팬토큰 가격 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
}; 