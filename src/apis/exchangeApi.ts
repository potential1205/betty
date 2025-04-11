import axiosInstance from "./axios";

export interface TransactionRequest {
    tokenId: number;
    amountIn: number;
}

export interface SwapRequest {
    tokenFromId: number;
    tokenToId: number;
    amountIn: number;
}

export interface TransactionResponse {
    success: boolean;
    message: string;
    transactionId: number;
}

export interface Wallet {
    id: number;
    nickname: string;
    walletAddress: string;
    createdAt: string;
    updatedAt: string;
}

export interface Token {
    id: number;
    tokenName: string;
    tokenAddress?: string;
    description?: string;
    createdAt?: string;
}

export interface Transaction {
    id: number;
    wallet?: Wallet;
    tokenFrom?: Token;
    tokenTo?: Token;
    amountIn: number;
    amountOut?: number;
    transactionStatus: "PENDING" | "SUCCESS" | "FAIL";
    createdAt: string;
}

// 토큰 가격 정보 인터페이스
export interface TokenPrice {
    id: number;
    token: {
        id: number;
        tokenName: string;
        tokenAddress: string;
        description: string;
        createdAt: string;
    };
    tokenName: string;
    price: number;
    updatedAt: string;
}

// BET 입금
export const addBettyCoin = async (amountIn: number): Promise<TransactionResponse> => {
    return (await axiosInstance.post("/exchange/add", { tokenId: 1, amountIn })).data;
}

// BET 출금
export const removeBettyCoin = async (amountIn: number) : Promise<TransactionResponse> => {
    return (await axiosInstance.post("/exchange/remove", { amountIn })).data;
}

// 팬토큰 구매
export const buyFanToken = async (request: TransactionRequest): Promise<TransactionResponse> => {
    return (await axiosInstance.post("/exchange/buy", request)).data;
}

// 팬토큰 판매
export const sellFanToken = async (request: TransactionRequest): Promise<TransactionResponse> => {
    return (await axiosInstance.post("/exchange/sell", request)).data;
}

// 팬토큰 스왑
export const swapFanToken = async (request: SwapRequest): Promise<TransactionResponse> => {
    return (await axiosInstance.post("/exchange/swap", request)).data;
}

/**
 * 현재 로그인된 사용자의 전체 트랜잭션 기록을 조회합니다.
 * @returns 트랜잭션 배열
 */
export const getTransactions = async (): Promise<Transaction[]> => {
    try {
        const response = await axiosInstance.get<Transaction[]>("/exchange/transactions");
        console.log('[ExchangeAPI] 트랜잭션 조회 성공:', response.data);
        return response.data;
    } catch (error) {
        console.error('[ExchangeAPI] 트랜잭션 조회 실패:', error);
        throw error;
    }
}

// 스왑 예상 금액 계산
export const getSwapEstimate = async (
    fromToken: string,
    toToken: string,
    amountIn: number
): Promise<{ expectedAmount: number; rate: number }> => {
    const response = await axiosInstance.get("/exchange/estimate", {
        params: { fromToken, toToken, amountIn },
    });
    return response.data;
}

/**
 * 모든 토큰의 현재 가격 정보를 조회합니다.
 * @returns 토큰 가격 정보 배열
 */
export const getAllTokenPrices = async (): Promise<TokenPrice[]> => {
    try {
        const response = await axiosInstance.get<TokenPrice[]>('/prices/all');
        console.log('[ExchangeAPI] 토큰 가격 조회 성공:', response.data);
        return response.data;
    } catch (error) {
        console.error('[ExchangeAPI] 토큰 가격 조회 실패:', error);
        throw error;
    }
};