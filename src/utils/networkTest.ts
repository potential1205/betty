import { ethers } from 'ethers';

interface NetworkTestResult {
  success: boolean;
  network?: {
    chainId: bigint;
    name: string;
  };
  blockNumber?: number;
  gasPrice?: {
    gasPrice: string;
    maxFeePerGas: string;
  };
  error?: string;
}

export async function testNetworkConnection(): Promise<NetworkTestResult> {
  try {
    // .env에서 RPC URL 가져오기
    const rpcUrl = import.meta.env.VITE_RPC_URL;
    if (!rpcUrl) {
      throw new Error('RPC URL이 설정되지 않았습니다.');
    }

    // 프로바이더 생성
    const provider = new ethers.JsonRpcProvider(rpcUrl);

    // 네트워크 연결 테스트
    console.log('네트워크 연결 테스트 중...');
    
    // 1. 네트워크 정보 가져오기
    const network = await provider.getNetwork();
    
    console.log('네트워크 정보:', {
      chainId: network.chainId,
      name: network.name
    });

    // 2. 블록 번호 가져오기
    const blockNumber = await provider.getBlockNumber();
    console.log('현재 블록 번호:', blockNumber);

    // 3. 가스 가격 조회
    const gasPrice = await provider.getFeeData();
    console.log('가스 가격:', {
      gasPrice: ethers.formatUnits(gasPrice.gasPrice || 0, 'gwei'),
      maxFeePerGas: gasPrice.maxFeePerGas ? ethers.formatUnits(gasPrice.maxFeePerGas, 'gwei') : 'N/A'
    });

    return {
      success: true,
      network,
      blockNumber,
      gasPrice: {
        gasPrice: ethers.formatUnits(gasPrice.gasPrice || 0, 'gwei'),
        maxFeePerGas: gasPrice.maxFeePerGas ? ethers.formatUnits(gasPrice.maxFeePerGas, 'gwei') : 'N/A'
      }
    };

  } catch (error) {
    console.error('네트워크 연결 실패:', error);
    return {
      success: false,
      error: error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다'
    };
  }
}