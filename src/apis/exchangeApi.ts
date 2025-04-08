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

// BTC 충전 API
export const addBettyCoin = async (amountIn: number): Promise<TransactionResponse> => {
    return (await axiosInstance.post("/exchange/add", { tokenId: 1, amountIn })).data;
}

// BTC 출금 API
export const removeBettyCoin = async (amountIn: number) : Promise<TransactionResponse> => {
    return (await axiosInstance.post("/exchange/remove", { amountIn })).data;
}