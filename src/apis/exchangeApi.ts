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
    return (
        await axiosInstance.post("/exchange/add", {
            tokenFromId: 0,
            tokenToId: 1,
            amountIn,
        }))
    .data;
};