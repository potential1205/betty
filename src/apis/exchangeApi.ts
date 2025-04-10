import axiosInstance from "./axios";

export interface TransactionRequest {
    tokenId: number;
    amountIn: number;
}

export interface SignedBuyRequest {
    tokenId: number;
    amountIn: number;
    signedRawTransaction: string;
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

export interface Transaction {
    id: number;
    walletId: number;
    tokenFrom?: Token;
    tokenTo?: Token;
    amountIn: number;
    amountOut?: number;
    transactionStatus: "PENDING" | "SUCCESS" | "FAIL";
}

export interface Token {
    id: number;
    tokenName: string;
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

export const buyFanTokenSigned = async (
    signedRawTransaction: string,
    tokenId: number,
    amountIn: number
): Promise<TransactionResponse> => {
    return (
        await axiosInstance.post("/exchange/buy/signed", {
            signedRawTransaction,
            tokenId,
            amountIn,
        })
    ).data;
};

// 팬토큰 판매
export const sellFanToken = async (request: TransactionRequest): Promise<TransactionResponse> => {
    return (await axiosInstance.post("/exchange/sell", request)).data;
}

// 팬토큰 스왑
export const swapFanToken = async (request: SwapRequest): Promise<TransactionResponse> => {
    return (await axiosInstance.post("/exchange/swap", request)).data;
}

// 트랜잭션 조회
export const getTransactions = async (): Promise<Transaction[]> => {
    return (await axiosInstance.get("/exchange/transactions")).data;
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