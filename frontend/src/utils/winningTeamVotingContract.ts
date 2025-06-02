import { ethers } from "ethers";
import WinningTeamVotingABI from "../../abi/WinningTeamVoting.json";
import { getTeamTokenAddress } from "../apis/tokenApi";
import { useWalletStore } from "../stores/walletStore";

// 환경 변수
const RPC_URL = import.meta.env.VITE_RPC_URL;
const CONTRACT_ADDRESS = import.meta.env.VITE_WINNING_TEAM_VOTING_CONTRACT_ADDRESS || '';
const TOKEN_ADDRESS = import.meta.env.VITE_TOKEN_ADDRESS || '';

/**
 * 게임 정보 타입 정의
 */
export interface GameInfo {
  gameId: number;
  teamAId: number;
  teamBId: number;
  startTime: number;
  totalTeamABets: bigint;
  totalTeamBBets: bigint;
  finalized: boolean;
  winningTeamId: number;
  teamATokenAddress: string;
  teamBTokenAddress: string;
  teamAVoters: number;
  teamBVoters: number;
}

/**
 * 팀 ID를 기준으로 토큰 주소를 가져옵니다.
 * @param teamId 팀 ID
 * @returns 팀 토큰 주소
 */
export const getTokenAddressByTeamId = async (teamId: number): Promise<string> => {
  try {
    if (!teamId) {
      throw new Error("팀 ID가 필요합니다");
    }
    
    // 토큰 API를 호출하여 팀 토큰 주소를 가져옵니다
    const tokenAddress = await getTeamTokenAddress(teamId);
    
    // 토큰 주소가 없으면 기본 토큰 주소를 반환합니다
    if (!tokenAddress) {
      console.warn(`팀 ${teamId}의 토큰 주소를 찾을 수 없어 기본 토큰 주소를 사용합니다.`);
      return TOKEN_ADDRESS;
    }
    
    return tokenAddress;
  } catch (error: any) {
    console.error(`팀 ${teamId}의 토큰 주소 조회 실패:`, error);
    // 오류 발생 시 기본 토큰 주소를 반환합니다
    return TOKEN_ADDRESS;
  }
};

/**
 * 기본 컨트랙트 주소를 반환합니다.
 * @returns WinningTeamVoting 컨트랙트 주소
 */
export const getDefaultContractAddress = (): string => {
  return CONTRACT_ADDRESS;
};

/**
 * 개인키로 WinningTeamVoting 컨트랙트와 상호작용하는 인스턴스를 생성합니다.
 * @param privateKey 트랜잭션에 서명하기 위한 개인키
 * @param contractAddress WinningTeamVoting 컨트랙트 주소
 * @returns 서명자로 연결된 컨트랙트 인스턴스
 */
export const getWinningTeamVotingContract = (privateKey: string, contractAddress: string = CONTRACT_ADDRESS) => {
  try {
    if (!privateKey || !contractAddress) {
      throw new Error("개인키와 컨트랙트 주소가 필요합니다");
    }

    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const wallet = new ethers.Wallet(privateKey, provider);
    
    return new ethers.Contract(contractAddress, WinningTeamVotingABI.abi, wallet);
  } catch (error: any) {
    console.error("WinningTeamVoting 컨트랙트 연결 실패:", error);
    throw new Error(`컨트랙트 연결 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 읽기 전용 WinningTeamVoting 컨트랙트 인스턴스를 생성합니다.
 * @param contractAddress WinningTeamVoting 컨트랙트 주소
 * @returns 읽기 전용 컨트랙트 인스턴스
 */
export const getWinningTeamVotingContractReadOnly = (contractAddress: string = CONTRACT_ADDRESS) => {
  try {
    if (!contractAddress) {
      throw new Error("컨트랙트 주소가 필요합니다");
    }

    const provider = new ethers.JsonRpcProvider(RPC_URL);
    return new ethers.Contract(contractAddress, WinningTeamVotingABI.abi, provider);
  } catch (error: any) {
    console.error("WinningTeamVoting 컨트랙트 연결 실패:", error);
    throw new Error(`컨트랙트 연결 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 게임을 생성합니다 (오너만 가능)
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param teamAId 팀 A ID
 * @param teamBId 팀 B ID
 * @param startTime 베팅 마감 시간 (Unix 타임스탬프)
 * @param teamATokenAddress 팀 A 토큰 주소
 * @param teamBTokenAddress 팀 B 토큰 주소
 * @returns 트랜잭션 영수증
 */
export const createGame = async (
  contract: ethers.Contract,
  gameId: number,
  teamAId: number,
  teamBId: number,
  startTime: number,
  teamATokenAddress: string,
  teamBTokenAddress: string
) => {
  try {
    const tx = await contract.createGame(
      gameId,
      teamAId,
      teamBId,
      startTime,
      teamATokenAddress,
      teamBTokenAddress,
      { gasPrice: 0 } // 가스 가격을 0으로 설정
    );
    return await tx.wait();
  } catch (error: any) {
    console.error("게임 생성 실패:", error);
    throw new Error(`게임 생성 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 게임 정보를 조회합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns 게임 정보
 */
export const getGameInfo = async (contract: ethers.Contract, gameId: number): Promise<GameInfo> => {
  try {
    const gameInfo = await contract.games(gameId);
    
    // 팀별 베터 수를 직접 컨트랙트에서 조회
    let teamABettersCount = 0;
    let teamBBettersCount = 0;
    
    try {
      teamABettersCount = Number(await contract.getTeamBettersCount(gameId, Number(gameInfo[1])));
    } catch (error) {
      console.warn(`팀 A 베팅자 수 조회 실패:`, error);
    }
    
    try {
      teamBBettersCount = Number(await contract.getTeamBettersCount(gameId, Number(gameInfo[2])));
    } catch (error) {
      console.warn(`팀 B 베팅자 수 조회 실패:`, error);
    }
    
    return {
      gameId: Number(gameInfo[0]),
      teamAId: Number(gameInfo[1]),
      teamBId: Number(gameInfo[2]),
      startTime: Number(gameInfo[3]),
      totalTeamABets: BigInt(gameInfo[4].toString()),
      totalTeamBBets: BigInt(gameInfo[5].toString()),
      finalized: gameInfo[6],
      winningTeamId: Number(gameInfo[7]),
      teamATokenAddress: gameInfo[8],
      teamBTokenAddress: gameInfo[9],
      teamAVoters: teamABettersCount,
      teamBVoters: teamBBettersCount
    };
  } catch (error: any) {
    console.error("게임 정보 조회 실패:", error);
    throw new Error(`게임 정보 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 게임이 존재하는지 확인합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns 게임 존재 여부
 */
export const checkGameExists = async (
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
 * 투표(베팅)를 합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param teamId 팀 ID
 * @param amount 베팅 금액
 * @returns 트랜잭션 영수증
 */
export const placeBet = async (
  contract: ethers.Contract,
  gameId: number,
  teamId: number,
  amount: string
) => {
  try {
    // 먼저 게임이 존재하는지 확인
    const gameExists = await checkGameExists(contract, gameId);
    if (!gameExists) {
      throw new Error(`게임 ID ${gameId}가 존재하지 않습니다. 먼저 게임을 생성해주세요.`);
    }
    
    const amountWei = ethers.parseEther(amount);

    const gameInfo = await getGameInfo(contract, gameId);
    
    // 게임 정보 출력
    console.log(`게임 정보:`, gameInfo);
    
    if (gameInfo.gameId === 0) {
      throw new Error(`게임 ID ${gameId}가 존재하지 않습니다. games 매핑에서 반환된 ID: ${gameInfo.gameId}`);
    }
    
    if (gameInfo.finalized) {
      throw new Error("게임이 이미 종료되었습니다");
    }
    if (Date.now() / 1000 > gameInfo.startTime) {
      throw new Error("베팅 마감 시간이 지났습니다");
    }

    if (teamId !== gameInfo.teamAId && teamId !== gameInfo.teamBId) {
      throw new Error("유효하지 않은 팀 ID입니다");
    }

    // 토큰 주소 결정: 선택한 팀의 토큰 주소 사용
    const tokenAddress = teamId === gameInfo.teamAId 
      ? gameInfo.teamATokenAddress 
      : gameInfo.teamBTokenAddress;
    
    console.log(`사용 토큰 주소: ${tokenAddress}`);
    
    // 토큰 주소 유효성 검사
    if (!tokenAddress || !ethers.isAddress(tokenAddress)) {
      throw new Error(`유효하지 않은 토큰 주소입니다: ${tokenAddress}`);
    }
    
    // 지갑 정보 가져오기
    const walletStore = useWalletStore.getState();
    const walletAddress = await walletStore.getAccounts();
    
    console.log(`지갑 주소: ${walletAddress}`);
    
    if (!walletAddress || !ethers.isAddress(walletAddress)) {
      throw new Error("유효한 지갑 주소를 가져올 수 없습니다");
    }
    
    // 개인키 가져오기
    const privateKey = await walletStore.exportPrivateKey();
    if (!privateKey) {
      throw new Error("개인키를 가져올 수 없습니다");
    }
    
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const wallet = new ethers.Wallet(privateKey, provider);
    
    console.log(`연결된 지갑 주소: ${wallet.address}`);
    
    // 토큰 컨트랙트 인스턴스 생성
    const erc20ABI = [
      "function allowance(address owner, address spender) view returns (uint256)",
      "function approve(address spender, uint256 amount) returns (bool)",
      "function balanceOf(address owner) view returns (uint256)"
    ];
    
    try {
      const tokenContract = new ethers.Contract(tokenAddress, erc20ABI, wallet);
      console.log(`토큰 컨트랙트 생성 성공: ${tokenContract.target}`);
      
      // 먼저 컨트랙트 코드가 존재하는지 확인
      const code = await provider.getCode(tokenAddress);
      if (code === '0x') {
        throw new Error(`토큰 주소 ${tokenAddress}에 컨트랙트 코드가 없습니다`);
      }
      
      console.log(`balanceOf 호출 중... 주소: ${walletAddress}`);
      
      // 잔액 확인 - 이 부분에서 오류가 발생합니다.
      try {
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
          const approveTx = await tokenContract.approve(contract.target, amountWei, { gasPrice: 0 }); // 가스 가격을 0으로 설정
          
          // 승인 트랜잭션이 블록에 포함될 때까지 대기
          const approveReceipt = await approveTx.wait();
          console.log('토큰 승인 완료! 트랜잭션 해시:', approveReceipt.hash);
        }
      } catch (balanceError: any) {
        console.error("토큰 잔액 조회 실패:", balanceError);
        
        // 일단 토큰 승인부터 시도해봅니다 - 잔액 조회에 실패해도 승인은 가능할 수 있음
        console.log('토큰 승인 시도...');
        const approveTx = await tokenContract.approve(contract.target, amountWei, { gasPrice: 0 }); // 가스 가격을 0으로 설정
        
        const approveReceipt = await approveTx.wait();
        console.log('토큰 승인 완료! 트랜잭션 해시:', approveReceipt.hash);
      }
    } catch (contractError: any) {
      console.error("토큰 컨트랙트 접근 오류:", contractError);
      throw new Error(`토큰 컨트랙트 접근 실패: ${contractError.message}`);
    }

    // 승인이 완료된 후 베팅 진행
    console.log(`베팅 시작: 게임 ID ${gameId}, 팀 ID ${teamId}, 금액 ${amount}`);
    const tx = await contract.placeBet(gameId, teamId, amountWei, { gasPrice: 0 }); // 가스 가격을 0으로 설정
    console.log(`베팅 트랜잭션 전송 완료: ${tx.hash}`);
    return await tx.wait();
  } catch (error: any) {
    console.error("투표 실패:", error);
    throw new Error(`투표 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 게임을 종료하고 승리팀을 설정합니다 (오너만 가능).
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param winningTeamId 승리팀 ID
 * @returns 트랜잭션 영수증
 */
export const finalizeGame = async (
  contract: ethers.Contract,
  gameId: number,
  winningTeamId: number
) => {
  try {
    const tx = await contract.finalize(gameId, winningTeamId, { gasPrice: 0 }); // 가스 가격을 0으로 설정
    return await tx.wait();
  } catch (error: any) {
    console.error("게임 종료 실패:", error);
    throw new Error(`게임 종료 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 사용자의 베팅 정보를 조회합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param userAddress 사용자 주소
 * @returns 베팅 정보
 */
export const getUserBet = async (
  contract: ethers.Contract,
  gameId: number,
  userAddress: string
) => {
  try {
    const betInfo = await contract.getUserBet(gameId, userAddress);
    return {
      amount: ethers.formatEther(betInfo[0]),
      teamId: Number(betInfo[1]),
      claimed: betInfo[2],
    };
  } catch (error: any) {
    console.error("베팅 정보 조회 실패:", error);
    throw new Error(`베팅 정보 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 보상을 청구합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns 트랜잭션 영수증
 */
export const claimReward = async (contract: ethers.Contract, gameId: number) => {
  try {
    const tx = await contract.claimReward(gameId, { gasPrice: 0 }); // 가스 가격을 0으로 설정
    return await tx.wait();
  } catch (error: any) {
    console.error("보상 청구 실패:", error);
    throw new Error(`보상 청구 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 특정 팀의 총 베팅액을 조회합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param teamId 팀 ID
 * @returns 팀의 총 베팅액
 */
export const getTeamBets = async (
  contract: ethers.Contract,
  gameId: number,
  teamId: number
): Promise<string> => {
  try {
    // 먼저 게임이 존재하는지 확인
    const gameExists = await checkGameExists(contract, gameId);
    if (!gameExists) {
      console.warn(`게임 ID ${gameId}가 존재하지 않습니다. 베팅액 0으로 반환합니다.`);
      return "0";
    }
    
    // 게임 정보 조회 대신 컨트랙트에서 직접 팀 ID 확인
    try {
      // games 매핑에서 게임 정보 직접 조회
      const gameInfo = await contract.games(gameId);
      const teamAId = Number(gameInfo[1]);
      const teamBId = Number(gameInfo[2]);
      
      if (teamId !== teamAId && teamId !== teamBId) {
        console.warn(`유효하지 않은 팀 ID ${teamId}입니다. 베팅액 0으로 반환합니다.`);
        return "0";
      }
      
      const teamBets = await contract.getTeamBets(gameId, teamId);
      return ethers.formatEther(teamBets);
    } catch (innerError: any) {
      console.warn(`게임 정보 조회 중 오류: ${innerError.message}`);
      
      // 컨트랙트 함수 직접 호출 시도
      const teamBets = await contract.getTeamBets(gameId, teamId);
      return ethers.formatEther(teamBets);
    }
  } catch (error: any) {
    // 오류 메시지에서 "Game does not exist" 부분을 확인하여 특별 처리
    if (error.reason === "Game does not exist") {
      console.warn(`게임 ID ${gameId}가 존재하지 않습니다. 베팅액 0으로 반환합니다.`);
      return "0";
    }
    
    console.error("팀 베팅액 조회 실패:", error);
    throw new Error(`팀 베팅액 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 특정 팀에 베팅한 사용자 수를 조회합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param teamId 팀 ID
 * @returns 팀에 베팅한 사용자 수
 */
export const getTeamBettersCount = async (
  contract: ethers.Contract,
  gameId: number,
  teamId: number
): Promise<number> => {
  try {
    // 먼저 게임이 존재하는지 확인
    const gameExists = await checkGameExists(contract, gameId);
    if (!gameExists) {
      console.warn(`게임 ID ${gameId}가 존재하지 않습니다. 베팅자 수 0으로 반환합니다.`);
      return 0;
    }
    
    // 게임 정보를 가져와 유효한 팀 ID인지 확인
    const gameInfo: GameInfo = await getGameInfo(contract, gameId);
    if (teamId !== gameInfo.teamAId && teamId !== gameInfo.teamBId) {
      console.warn(`유효하지 않은 팀 ID ${teamId}입니다. 베팅자 수 0으로 반환합니다.`);
      return 0;
    }
    
    const count: bigint = await contract.getTeamBettersCount(gameId, teamId);
    return Number(count);
  } catch (error: any) {
    // 오류 메시지에서 "Game does not exist" 부분을 확인하여 특별 처리
    if (error.reason === "Game does not exist") {
      console.warn(`게임 ID ${gameId}가 존재하지 않습니다. 베팅자 수 0으로 반환합니다.`);
      return 0;
    }
    
    console.error("팀 베팅자 수 조회 실패:", error);
    throw new Error(`팀 베팅자 수 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 토큰 승인 상태를 확인합니다.
 * @param privateKey 사용자 개인키
 * @param tokenAddress 토큰 컨트랙트 주소
 * @param spenderAddress 권한을 부여받을 컨트랙트 주소
 * @param amount 승인 필요 금액
 * @returns 승인 상태 (true: 충분한 승인됨, false: 추가 승인 필요)
 */
export const checkTokenAllowance = async (
  privateKey: string,
  tokenAddress: string,
  spenderAddress: string,
  amount: bigint
): Promise<boolean> => {
  try {
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const wallet = new ethers.Wallet(privateKey, provider);
    
    // ERC20 토큰 컨트랙트 ABI (필요한 부분만)
    const erc20ABI = [
      "function allowance(address owner, address spender) view returns (uint256)",
      "function approve(address spender, uint256 amount) returns (bool)",
    ];
    
    const tokenContract = new ethers.Contract(tokenAddress, erc20ABI, wallet);
    
    // 승인 상태 확인
    const allowance = await tokenContract.allowance(wallet.address, spenderAddress);
    
    return allowance >= amount;
  } catch (error: any) {
    console.error("토큰 승인 확인 실패:", error);
    throw new Error(`토큰 승인 확인 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 토큰 사용 권한을 부여합니다.
 * @param privateKey 사용자 개인키
 * @param tokenAddress 토큰 컨트랙트 주소
 * @param spenderAddress 권한을 부여받을 컨트랙트 주소
 * @param amount 승인할 금액
 * @returns 트랜잭션 결과
 */
export const approveToken = async (
  privateKey: string,
  tokenAddress: string,
  spenderAddress: string,
  amount: bigint
) => {
  try {
    const provider = new ethers.JsonRpcProvider(RPC_URL);
    const wallet = new ethers.Wallet(privateKey, provider);
    
    // ERC20 토큰 컨트랙트 ABI (필요한 부분만)
    const erc20ABI = [
      "function allowance(address owner, address spender) view returns (uint256)",
      "function approve(address spender, uint256 amount) returns (bool)",
    ];
    
    const tokenContract = new ethers.Contract(tokenAddress, erc20ABI, wallet);
    
    // 토큰 승인
    const tx = await tokenContract.approve(spenderAddress, amount, { gasPrice: 0 }); // 가스 가격을 0으로 설정
    
    return tx;
  } catch (error: any) {
    console.error("토큰 승인 실패:", error);
    throw new Error(`토큰 승인 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 게임 목록 전체를 조회합니다. (게임 ID를 아는 경우에만 사용 가능)
 * @param contract 컨트랙트 인스턴스
 * @param gameIds 조회할 게임 ID 배열
 * @returns 게임 정보 객체 배열
 */
export const getGames = async (
  contract: ethers.Contract, 
  gameIds: number[]
): Promise<any[]> => {
  try {
    const gamesInfo = await Promise.all(
      gameIds.map(async (gameId) => {
        try {
          return await getGameInfo(contract, gameId);
        } catch (error) {
          console.warn(`게임 ID ${gameId} 조회 실패:`, error);
          return null;
        }
      })
    );
    
    // null 값 제거 (존재하지 않는 게임)
    return gamesInfo.filter(game => game !== null);
  } catch (error: any) {
    console.error("게임 목록 조회 실패:", error);
    throw new Error(`게임 목록 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 게임 토큰 주소를 업데이트합니다 (오너만 가능).
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param newTeamATokenAddress 새 팀 A 토큰 주소
 * @param newTeamBTokenAddress 새 팀 B 토큰 주소
 * @returns 트랜잭션 영수증
 */
export const updateGameTokenAddresses = async (
  contract: ethers.Contract,
  gameId: number,
  newTeamATokenAddress: string,
  newTeamBTokenAddress: string
) => {
  try {
    // 먼저 게임이 존재하는지 확인
    const gameExists = await checkGameExists(contract, gameId);
    if (!gameExists) {
      throw new Error(`게임 ID ${gameId}가 존재하지 않습니다.`);
    }

    // 게임이 이미 베팅이 있거나 종료되었는지 확인
    const gameInfo = await getGameInfo(contract, gameId);
    if (gameInfo.finalized) {
      throw new Error("게임이 이미 종료되었습니다");
    }
    
    if (gameInfo.totalTeamABets > 0 || gameInfo.totalTeamBBets > 0) {
      throw new Error("이미 베팅이 이루어진 게임의 토큰 주소는 변경할 수 없습니다");
    }

    // 토큰 주소 유효성 검증
    if (!ethers.isAddress(newTeamATokenAddress)) {
      throw new Error(`유효하지 않은 팀 A 토큰 주소: ${newTeamATokenAddress}`);
    }
    
    if (!ethers.isAddress(newTeamBTokenAddress)) {
      throw new Error(`유효하지 않은 팀 B 토큰 주소: ${newTeamBTokenAddress}`);
    }

    console.log(`게임 ${gameId} 토큰 주소 업데이트: 팀 A(${newTeamATokenAddress}), 팀 B(${newTeamBTokenAddress})`);
    
    const tx = await contract.updateGameTokenAddresses(
      gameId,
      newTeamATokenAddress,
      newTeamBTokenAddress,
      { gasPrice: 0 } // 가스 가격을 0으로 설정
    );
    
    return await tx.wait();
  } catch (error: any) {
    console.error("게임 토큰 주소 업데이트 실패:", error);
    throw new Error(`게임 토큰 주소 업데이트 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 사용자를 대신하여 보상을 청구합니다 (오너만 가능).
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param userAddress 사용자 주소
 * @returns 트랜잭션 영수증
 */
export const claimForUser = async (
  contract: ethers.Contract,
  gameId: number,
  userAddress: string
) => {
  try {
    // 게임과 사용자 베팅 정보 확인
    const gameInfo = await getGameInfo(contract, gameId);
    if (!gameInfo.finalized) {
      throw new Error("게임이 아직 종료되지 않았습니다");
    }

    const userBet = await getUserBet(contract, gameId, userAddress);
    if (userBet.claimed) {
      throw new Error("이미 보상이 청구되었습니다");
    }

    if (Number(userBet.amount) === 0) {
      throw new Error("해당 게임에 베팅한 기록이 없습니다");
    }

    console.log(`사용자 ${userAddress}를 위한 보상 청구: 게임 ID ${gameId}`);

    const tx = await contract.claimForUser(gameId, userAddress, { gasPrice: 0 }); // 가스 가격을 0으로 설정
    return await tx.wait();
  } catch (error: any) {
    console.error("사용자 보상 청구 실패:", error);
    throw new Error(`사용자 보상 청구 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 특정 팀에 베팅한 사용자 주소 목록을 조회합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @param teamId 팀 ID
 * @returns 팀에 베팅한 사용자 주소 배열
 */
export const getTeamBettors = async (
  contract: ethers.Contract,
  gameId: number,
  teamId: number
): Promise<string[]> => {
  try {
    // 먼저 게임이 존재하는지 확인
    const gameExists = await checkGameExists(contract, gameId);
    if (!gameExists) {
      console.warn(`게임 ID ${gameId}가 존재하지 않습니다. 빈 배열을 반환합니다.`);
      return [];
    }
    
    const bettors = await contract.getTeamBettors(gameId, teamId);
    return bettors;
  } catch (error: any) {
    console.error(`팀 ID ${teamId}의 베팅자 목록 조회 실패:`, error);
    throw new Error(`팀 베팅자 목록 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 승리팀에 베팅한 사용자 주소 목록을 조회합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns 승리팀에 베팅한 사용자 주소 배열
 */
export const getWinningTeamBettors = async (
  contract: ethers.Contract,
  gameId: number
): Promise<string[]> => {
  try {
    // 먼저 게임이 존재하는지 확인
    const gameExists = await checkGameExists(contract, gameId);
    if (!gameExists) {
      console.warn(`게임 ID ${gameId}가 존재하지 않습니다. 빈 배열을 반환합니다.`);
      return [];
    }
    
    // 게임이 종료되었는지 확인
    const gameInfo = await getGameInfo(contract, gameId);
    if (!gameInfo.finalized) {
      throw new Error("게임이 아직 종료되지 않았습니다.");
    }
    
    const bettors = await contract.getWinningTeamBettors(gameId);
    return bettors;
  } catch (error: any) {
    console.error(`승리팀 베팅자 목록 조회 실패:`, error);
    throw new Error(`승리팀 베팅자 목록 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 패배팀에 베팅한 사용자 주소 목록을 조회합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns 패배팀에 베팅한 사용자 주소 배열
 */
export const getLosingTeamBettors = async (
  contract: ethers.Contract,
  gameId: number
): Promise<string[]> => {
  try {
    // 먼저 게임이 존재하는지 확인
    const gameExists = await checkGameExists(contract, gameId);
    if (!gameExists) {
      console.warn(`게임 ID ${gameId}가 존재하지 않습니다. 빈 배열을 반환합니다.`);
      return [];
    }
    
    // 게임이 종료되었는지 확인
    const gameInfo = await getGameInfo(contract, gameId);
    if (!gameInfo.finalized) {
      throw new Error("게임이 아직 종료되지 않았습니다.");
    }
    
    const bettors = await contract.getLosingTeamBettors(gameId);
    return bettors;
  } catch (error: any) {
    console.error(`패배팀 베팅자 목록 조회 실패:`, error);
    throw new Error(`패배팀 베팅자 목록 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
};

/**
 * 게임에 베팅한 모든 사용자 주소 목록을 조회합니다.
 * @param contract 컨트랙트 인스턴스
 * @param gameId 게임 ID
 * @returns 게임에 베팅한 모든 사용자 주소 배열
 */
export const getAllGameBettors = async (
  contract: ethers.Contract,
  gameId: number
): Promise<string[]> => {
  try {
    // 먼저 게임이 존재하는지 확인
    const gameExists = await checkGameExists(contract, gameId);
    if (!gameExists) {
      console.warn(`게임 ID ${gameId}가 존재하지 않습니다. 빈 배열을 반환합니다.`);
      return [];
    }
    
    const bettors = await contract.getAllGameBettors(gameId);
    return bettors;
  } catch (error: any) {
    console.error(`게임 베팅자 목록 조회 실패:`, error);
    throw new Error(`게임 베팅자 목록 조회 실패: ${error.message || "알 수 없는 오류"}`);
  }
}; 