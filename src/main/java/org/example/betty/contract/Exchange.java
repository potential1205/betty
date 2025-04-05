package org.example.betty.contract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/hyperledger-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.3.
 */
@SuppressWarnings("rawtypes")
public class Exchange extends Contract {
    public static final String BINARY = "癤�0x608060405234801561001057600080fd5b5060405161146c38038061146c833981810160405281019061003291906100db565b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050610108565b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006100a88261007d565b9050919050565b6100b88161009d565b81146100c357600080fd5b50565b6000815190506100d5816100af565b92915050565b6000602082840312156100f1576100f0610078565b5b60006100ff848285016100c6565b91505092915050565b611355806101176000396000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c80636028e667116100665780636028e667146101455780639f65632114610175578063a451f602146101a5578063b1164794146101d5578063e90ca51a146102055761009e565b8063074bc101146100a35780630ca5c504146100c15780631003e2d2146100f157806322cb8f171461010d5780634cc8221514610129575b600080fd5b6100ab610221565b6040516100b89190610c62565b60405180910390f35b6100db60048036038101906100d69190610e0d565b610245565b6040516100e89190610e78565b60405180910390f35b61010b60048036038101906101069190610e93565b610409565b005b61012760048036038101906101229190610e0d565b6104ad565b005b610143600480360381019061013e9190610e93565b610562565b005b61015f600480360381019061015a9190610ec0565b610604565b60405161016c9190610f2a565b60405180910390f35b61018f600480360381019061018a9190610e0d565b61064d565b60405161019c9190610e78565b60405180910390f35b6101bf60048036038101906101ba9190610f45565b61082f565b6040516101cc9190610e78565b60405180910390f35b6101ef60048036038101906101ea9190610ec0565b610adb565b6040516101fc9190610c62565b60405180910390f35b61021f600480360381019061021a919061100e565b610b24565b005b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008060028460405161025891906110ee565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3383866040518463ffffffff1660e01b81526004016102e693929190611114565b6020604051808303816000875af1158015610305573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906103299190611183565b5060008173ffffffffffffffffffffffffffffffffffffffff16632cf5dbda85336040518363ffffffff1660e01b81526004016103679291906111b0565b6020604051808303816000875af1158015610386573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906103aa91906111ee565b90503373ffffffffffffffffffffffffffffffffffffffff167f2ffcd8ea21e3668481ddff339b825e8ca9278c9ee226175e1ab589019a960b6b8686846040516103f693929190611265565b60405180910390a2809250505092915050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3330846040518463ffffffff1660e01b815260040161046693929190611114565b6020604051808303816000875af1158015610485573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104a99190611183565b5050565b60006001836040516104bf91906110ee565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508073ffffffffffffffffffffffffffffffffffffffff166379cc679033846040518363ffffffff1660e01b815260040161052b9291906112a3565b600060405180830381600087803b15801561054557600080fd5b505af1158015610559573d6000803e3d6000fd5b50505050505050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb33836040518363ffffffff1660e01b81526004016105bd9291906112a3565b6020604051808303816000875af11580156105dc573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906106009190611183565b5050565b6002818051602081018201805184825260208301602085012081835280955050505050506000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008060028460405161066091906110ee565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506001846040516106a191906110ee565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3383866040518463ffffffff1660e01b815260040161070c93929190611114565b6020604051808303816000875af115801561072b573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061074f9190611183565b5060008173ffffffffffffffffffffffffffffffffffffffff16633bce1c7585336040518363ffffffff1660e01b815260040161078d9291906111b0565b6020604051808303816000875af11580156107ac573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906107d091906111ee565b90503373ffffffffffffffffffffffffffffffffffffffff167fd81d3d3fdda16e753fb787fb7e2c57fbbedf1c8c4aab4d998fba081f2d4f220186868460405161081c93929190611265565b60405180910390a2809250505092915050565b60008060028560405161084291906110ee565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600060028560405161088591906110ee565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506001866040516108c691906110ee565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3384876040518463ffffffff1660e01b815260040161093193929190611114565b6020604051808303816000875af1158015610950573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906109749190611183565b5060008273ffffffffffffffffffffffffffffffffffffffff16633bce1c7586846040518363ffffffff1660e01b81526004016109b29291906111b0565b6020604051808303816000875af11580156109d1573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906109f591906111ee565b905060008273ffffffffffffffffffffffffffffffffffffffff16632cf5dbda83336040518363ffffffff1660e01b8152600401610a349291906111b0565b6020604051808303816000875af1158015610a53573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610a7791906111ee565b90503373ffffffffffffffffffffffffffffffffffffffff167fbe28245e56c141dd9dddce73731b090cb4ca241a4d2a22a77ed6425dc5e1b1c689898985604051610ac594939291906112cc565b60405180910390a2809450505050509392505050565b6001818051602081018201805184825260208301602085012081835280955050505050506000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b81600184604051610b3591906110ee565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600284604051610b9291906110ee565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b6000610c28610c23610c1e84610be3565b610c03565b610be3565b9050919050565b6000610c3a82610c0d565b9050919050565b6000610c4c82610c2f565b9050919050565b610c5c81610c41565b82525050565b6000602082019050610c776000830184610c53565b92915050565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b610ce482610c9b565b810181811067ffffffffffffffff82111715610d0357610d02610cac565b5b80604052505050565b6000610d16610c7d565b9050610d228282610cdb565b919050565b600067ffffffffffffffff821115610d4257610d41610cac565b5b610d4b82610c9b565b9050602081019050919050565b82818337600083830152505050565b6000610d7a610d7584610d27565b610d0c565b905082815260208101848484011115610d9657610d95610c96565b5b610da1848285610d58565b509392505050565b600082601f830112610dbe57610dbd610c91565b5b8135610dce848260208601610d67565b91505092915050565b6000819050919050565b610dea81610dd7565b8114610df557600080fd5b50565b600081359050610e0781610de1565b92915050565b60008060408385031215610e2457610e23610c87565b5b600083013567ffffffffffffffff811115610e4257610e41610c8c565b5b610e4e85828601610da9565b9250506020610e5f85828601610df8565b9150509250929050565b610e7281610dd7565b82525050565b6000602082019050610e8d6000830184610e69565b92915050565b600060208284031215610ea957610ea8610c87565b5b6000610eb784828501610df8565b91505092915050565b600060208284031215610ed657610ed5610c87565b5b600082013567ffffffffffffffff811115610ef457610ef3610c8c565b5b610f0084828501610da9565b91505092915050565b6000610f1482610c2f565b9050919050565b610f2481610f09565b82525050565b6000602082019050610f3f6000830184610f1b565b92915050565b600080600060608486031215610f5e57610f5d610c87565b5b600084013567ffffffffffffffff811115610f7c57610f7b610c8c565b5b610f8886828701610da9565b935050602084013567ffffffffffffffff811115610fa957610fa8610c8c565b5b610fb586828701610da9565b9250506040610fc686828701610df8565b9150509250925092565b6000610fdb82610be3565b9050919050565b610feb81610fd0565b8114610ff657600080fd5b50565b60008135905061100881610fe2565b92915050565b60008060006060848603121561102757611026610c87565b5b600084013567ffffffffffffffff81111561104557611044610c8c565b5b61105186828701610da9565b935050602061106286828701610ff9565b925050604061107386828701610ff9565b9150509250925092565b600081519050919050565b600081905092915050565b60005b838110156110b1578082015181840152602081019050611096565b60008484015250505050565b60006110c88261107d565b6110d28185611088565b93506110e2818560208601611093565b80840191505092915050565b60006110fa82846110bd565b915081905092915050565b61110e81610fd0565b82525050565b60006060820190506111296000830186611105565b6111366020830185611105565b6111436040830184610e69565b949350505050565b60008115159050919050565b6111608161114b565b811461116b57600080fd5b50565b60008151905061117d81611157565b92915050565b60006020828403121561119957611198610c87565b5b60006111a78482850161116e565b91505092915050565b60006040820190506111c56000830185610e69565b6111d26020830184611105565b9392505050565b6000815190506111e881610de1565b92915050565b60006020828403121561120457611203610c87565b5b6000611212848285016111d9565b91505092915050565b600082825260208201905092915050565b60006112378261107d565b611241818561121b565b9350611251818560208601611093565b61125a81610c9b565b840191505092915050565b6000606082019050818103600083015261127f818661122c565b905061128e6020830185610e69565b61129b6040830184610e69565b949350505050565b60006040820190506112b86000830185611105565b6112c56020830184610e69565b9392505050565b600060808201905081810360008301526112e6818761122c565b905081810360208301526112fa818661122c565b90506113096040830185610e69565b6113166060830184610e69565b9594505050505056fea2646970667358221220c071791cc00b47f0fb79564ae390d23fa4f6634d33bfacae34bc04df6d5afb9d64736f6c634300081c0033\r\n";

    private static String librariesLinkedBinary;

    public static final String FUNC_ADD = "add";

    public static final String FUNC_ADDFANTOKEN = "addFanToken";

    public static final String FUNC_BTCTOKEN = "btcToken";

    public static final String FUNC_BUY = "buy";

    public static final String FUNC_FANTOKENS = "fanTokens";

    public static final String FUNC_LIQUIDITYPOOLS = "liquidityPools";

    public static final String FUNC_REMOVE = "remove";

    public static final String FUNC_SELL = "sell";

    public static final String FUNC_SWAP = "swap";

    public static final String FUNC_USE = "use";

    public static final Event BUYEXECUTED_EVENT = new Event("BuyExecuted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SELLEXECUTED_EVENT = new Event("SellExecuted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SWAPEXECUTED_EVENT = new Event("SwapExecuted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Exchange(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Exchange(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Exchange(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Exchange(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> add(BigInteger amountBTC) {
        final Function function = new Function(
                FUNC_ADD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountBTC)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addFanToken(String token_name,
            String tokenAddress, String liquidityPool) {
        final Function function = new Function(
                FUNC_ADDFANTOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.Address(160, tokenAddress), 
                new org.web3j.abi.datatypes.Address(160, liquidityPool)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> btcToken() {
        final Function function = new Function(FUNC_BTCTOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> buy(String token_name, BigInteger amountBTC) {
        final Function function = new Function(
                FUNC_BUY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.generated.Uint256(amountBTC)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> fanTokens(String param0) {
        final Function function = new Function(FUNC_FANTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> liquidityPools(String param0) {
        final Function function = new Function(FUNC_LIQUIDITYPOOLS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> remove(BigInteger amountBTC) {
        final Function function = new Function(
                FUNC_REMOVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountBTC)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> sell(String token_name,
            BigInteger amountFanToken) {
        final Function function = new Function(
                FUNC_SELL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.generated.Uint256(amountFanToken)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> swap(String token_from, String token_to,
            BigInteger amountFanToken) {
        final Function function = new Function(
                FUNC_SWAP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_from), 
                new org.web3j.abi.datatypes.Utf8String(token_to), 
                new org.web3j.abi.datatypes.generated.Uint256(amountFanToken)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> use(String token_name, BigInteger amount) {
        final Function function = new Function(
                FUNC_USE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static List<BuyExecutedEventResponse> getBuyExecutedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BUYEXECUTED_EVENT, transactionReceipt);
        ArrayList<BuyExecutedEventResponse> responses = new ArrayList<BuyExecutedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BuyExecutedEventResponse typedResponse = new BuyExecutedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.tokenName = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BuyExecutedEventResponse getBuyExecutedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BUYEXECUTED_EVENT, log);
        BuyExecutedEventResponse typedResponse = new BuyExecutedEventResponse();
        typedResponse.log = log;
        typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.tokenName = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<BuyExecutedEventResponse> buyExecutedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBuyExecutedEventFromLog(log));
    }

    public Flowable<BuyExecutedEventResponse> buyExecutedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BUYEXECUTED_EVENT));
        return buyExecutedEventFlowable(filter);
    }

    public static List<SellExecutedEventResponse> getSellExecutedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(SELLEXECUTED_EVENT, transactionReceipt);
        ArrayList<SellExecutedEventResponse> responses = new ArrayList<SellExecutedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SellExecutedEventResponse typedResponse = new SellExecutedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.seller = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.tokenName = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static SellExecutedEventResponse getSellExecutedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(SELLEXECUTED_EVENT, log);
        SellExecutedEventResponse typedResponse = new SellExecutedEventResponse();
        typedResponse.log = log;
        typedResponse.seller = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.tokenName = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<SellExecutedEventResponse> sellExecutedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getSellExecutedEventFromLog(log));
    }

    public Flowable<SellExecutedEventResponse> sellExecutedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SELLEXECUTED_EVENT));
        return sellExecutedEventFlowable(filter);
    }

    public static List<SwapExecutedEventResponse> getSwapExecutedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(SWAPEXECUTED_EVENT, transactionReceipt);
        ArrayList<SwapExecutedEventResponse> responses = new ArrayList<SwapExecutedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SwapExecutedEventResponse typedResponse = new SwapExecutedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.tokenFrom = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.tokenTo = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static SwapExecutedEventResponse getSwapExecutedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(SWAPEXECUTED_EVENT, log);
        SwapExecutedEventResponse typedResponse = new SwapExecutedEventResponse();
        typedResponse.log = log;
        typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.tokenFrom = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.tokenTo = (String) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
        return typedResponse;
    }

    public Flowable<SwapExecutedEventResponse> swapExecutedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getSwapExecutedEventFromLog(log));
    }

    public Flowable<SwapExecutedEventResponse> swapExecutedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SWAPEXECUTED_EVENT));
        return swapExecutedEventFlowable(filter);
    }

    @Deprecated
    public static Exchange load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new Exchange(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Exchange load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Exchange(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Exchange load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new Exchange(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Exchange load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Exchange(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Exchange> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider, String _btcToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _btcToken)));
        return deployRemoteCall(Exchange.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    public static RemoteCall<Exchange> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider, String _btcToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _btcToken)));
        return deployRemoteCall(Exchange.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Exchange> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit, String _btcToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _btcToken)));
        return deployRemoteCall(Exchange.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Exchange> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit, String _btcToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _btcToken)));
        return deployRemoteCall(Exchange.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }


    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class BuyExecutedEventResponse extends BaseEventResponse {
        public String buyer;

        public String tokenName;

        public BigInteger amountIn;

        public BigInteger amountOut;
    }

    public static class SellExecutedEventResponse extends BaseEventResponse {
        public String seller;

        public String tokenName;

        public BigInteger amountIn;

        public BigInteger amountOut;
    }

    public static class SwapExecutedEventResponse extends BaseEventResponse {
        public String user;

        public String tokenFrom;

        public String tokenTo;

        public BigInteger amountIn;

        public BigInteger amountOut;
    }
}
