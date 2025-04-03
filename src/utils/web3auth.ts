import { Web3AuthNoModal } from "@web3auth/no-modal";
import { WALLET_ADAPTERS, WEB3AUTH_NETWORK, IWeb3AuthCoreOptions, CHAIN_NAMESPACES } from "@web3auth/base";
import { EthereumPrivateKeyProvider } from "@web3auth/ethereum-provider";
import { AuthAdapter } from "@web3auth/auth-adapter";

// 환경 변수에서 값 가져오기
const clientId = import.meta.env.VITE_WEB3AUTH_CLIENT_ID;
const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
const chainId = parseInt(import.meta.env.VITE_CHAIN_ID);

// 설정값 유효성 검사
if (!clientId || !googleClientId || !chainId) {
  throw new Error("필수 환경 변수가 설정되지 않았습니다");
}

const toHexChainId = (decimalId: number) => '0x' + Number(decimalId).toString(16);

//Web3Auth 설정
const chainConfig = { 
  chainNamespace: CHAIN_NAMESPACES.EIP155, 
  chainId: toHexChainId(chainId), 
  rpcTarget: import.meta.env.VITE_RPC_URL, 
  displayName: "StadiumChain",  
  ticker: "BTC",
  tickerName: "Betty",
};

const privateKeyProvider = new EthereumPrivateKeyProvider({ config: { chainConfig } });

const web3AuthOptions: IWeb3AuthCoreOptions = {
  clientId,
  web3AuthNetwork: WEB3AUTH_NETWORK.SAPPHIRE_DEVNET,
  privateKeyProvider,
};

// Web3Auth 인스턴스 생성
export const web3auth = new Web3AuthNoModal(web3AuthOptions);

// 어댑터 설정
const authadapter = new AuthAdapter({
  adapterSettings: {
    uxMode: "popup",
    loginConfig: {
      google: {
        verifier: "google-login-jwt",
        typeOfLogin: "google",
        clientId: googleClientId,
      },
    },
  },
});

web3auth.configureAdapter(authadapter);

// Web3Auth 초기화
export const initWeb3Auth = async (): Promise<void> => {
  try {
    await web3auth.init();
    console.log('Web3Auth 초기화 완료');
  } catch (error) {
    console.error('Web3Auth 초기화 실패:', error);
    throw error;
  }
}; 