package org.example.betty.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
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
public class LiquidityPool extends Contract {
    public static final String BINARY = "癤�0x608060405234801561001057600080fd5b50604051610fa4380380610fa4833981810160405281019061003291906101fb565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16036100a1576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161009890610298565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1603610110576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161010790610304565b60405180910390fd5b816000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505050610324565b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006101c88261019d565b9050919050565b6101d8816101bd565b81146101e357600080fd5b50565b6000815190506101f5816101cf565b92915050565b6000806040838503121561021257610211610198565b5b6000610220858286016101e6565b9250506020610231858286016101e6565b9150509250929050565b600082825260208201905092915050565b7f42455420746f6b656e2061646472657373206973207a65726f00000000000000600082015250565b600061028260198361023b565b915061028d8261024c565b602082019050919050565b600060208201905081810360008301526102b181610275565b9050919050565b7f46616e20746f6b656e2061646472657373206973207a65726f00000000000000600082015250565b60006102ee60198361023b565b91506102f9826102b8565b602082019050919050565b6000602082019050818103600083015261031d816102e1565b9050919050565b610c71806103336000396000f3fe608060405234801561001057600080fd5b50600436106100935760003560e01c80633bce1c75116100665780633bce1c7514610123578063663c2b991461015357806375daa3de1461016f57806378691f161461018d578063a42c6b00146101ab57610093565b80630902f1ac146100985780631b1c539b146100b75780632cf5dbda146100d5578063392e53cd14610105575b600080fd5b6100a06101c9565b6040516100ae929190610641565b60405180910390f35b6100bf6101da565b6040516100cc919061066a565b60405180910390f35b6100ef60048036038101906100ea9190610714565b6101e0565b6040516100fc919061066a565b60405180910390f35b61010d61039e565b60405161011a919061076f565b60405180910390f35b61013d60048036038101906101389190610714565b6103b7565b60405161014a919061066a565b60405180910390f35b61016d6004803603810190610168919061078a565b610573565b005b6101776105d8565b6040516101849190610829565b60405180910390f35b6101956105fe565b6040516101a29190610829565b60405180910390f35b6101b3610622565b6040516101c0919061066a565b60405180910390f35b600080600254600354915091509091565b60025481565b6000808311610224576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161021b906108a1565b60405180910390fd5b60008360025461023491906108f0565b90506000816003546002546102499190610924565b6102539190610995565b90508060035411610299576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161029090610a12565b60405180910390fd5b806003546102a79190610a32565b92508160028190555080600381905550600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb85856040518363ffffffff1660e01b8152600401610314929190610a75565b6020604051808303816000875af1158015610333573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906103579190610aca565b610396576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161038d90610b43565b60405180910390fd5b505092915050565b60008060025411806103b257506000600354115b905090565b60008083116103fb576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016103f2906108a1565b60405180910390fd5b60008360035461040b91906108f0565b90506000816003546002546104209190610924565b61042a9190610995565b90508060025411610470576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161046790610a12565b60405180910390fd5b8060025461047e9190610a32565b9250816003819055508060028190555060008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb85856040518363ffffffff1660e01b81526004016104e9929190610a75565b6020604051808303816000875af1158015610508573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061052c9190610aca565b61056b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161056290610baf565b60405180910390fd5b505092915050565b600060025414801561058757506000600354145b6105c6576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016105bd90610c1b565b60405180910390fd5b81600281905550806003819055505050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60035481565b6000819050919050565b61063b81610628565b82525050565b60006040820190506106566000830185610632565b6106636020830184610632565b9392505050565b600060208201905061067f6000830184610632565b92915050565b600080fd5b61069381610628565b811461069e57600080fd5b50565b6000813590506106b08161068a565b92915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006106e1826106b6565b9050919050565b6106f1816106d6565b81146106fc57600080fd5b50565b60008135905061070e816106e8565b92915050565b6000806040838503121561072b5761072a610685565b5b6000610739858286016106a1565b925050602061074a858286016106ff565b9150509250929050565b60008115159050919050565b61076981610754565b82525050565b60006020820190506107846000830184610760565b92915050565b600080604083850312156107a1576107a0610685565b5b60006107af858286016106a1565b92505060206107c0858286016106a1565b9150509250929050565b6000819050919050565b60006107ef6107ea6107e5846106b6565b6107ca565b6106b6565b9050919050565b6000610801826107d4565b9050919050565b6000610813826107f6565b9050919050565b61082381610808565b82525050565b600060208201905061083e600083018461081a565b92915050565b600082825260208201905092915050565b7f416d6f756e74206d7573742062652067726561746572207468616e2030000000600082015250565b600061088b601d83610844565b915061089682610855565b602082019050919050565b600060208201905081810360008301526108ba8161087e565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b60006108fb82610628565b915061090683610628565b925082820190508082111561091e5761091d6108c1565b5b92915050565b600061092f82610628565b915061093a83610628565b925082820261094881610628565b9150828204841483151761095f5761095e6108c1565b5b5092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601260045260246000fd5b60006109a082610628565b91506109ab83610628565b9250826109bb576109ba610966565b5b828204905092915050565b7f496e76616c6964206f75747075742063616c63756c6174696f6e000000000000600082015250565b60006109fc601a83610844565b9150610a07826109c6565b602082019050919050565b60006020820190508181036000830152610a2b816109ef565b9050919050565b6000610a3d82610628565b9150610a4883610628565b9250828203905081811115610a6057610a5f6108c1565b5b92915050565b610a6f816106d6565b82525050565b6000604082019050610a8a6000830185610a66565b610a976020830184610632565b9392505050565b610aa781610754565b8114610ab257600080fd5b50565b600081519050610ac481610a9e565b92915050565b600060208284031215610ae057610adf610685565b5b6000610aee84828501610ab5565b91505092915050565b7f46616e20746f6b656e207472616e73666572206661696c656400000000000000600082015250565b6000610b2d601983610844565b9150610b3882610af7565b602082019050919050565b60006020820190508181036000830152610b5c81610b20565b9050919050565b7f424554207472616e73666572206661696c656400000000000000000000000000600082015250565b6000610b99601383610844565b9150610ba482610b63565b602082019050919050565b60006020820190508181036000830152610bc881610b8c565b9050919050565b7f416c726561647920696e697469616c697a656400000000000000000000000000600082015250565b6000610c05601383610844565b9150610c1082610bcf565b602082019050919050565b60006020820190508181036000830152610c3481610bf8565b905091905056fea264697066735822122002aee40dad49df23193f37fcc804236c0871ec09539ab847818202850a4f307c64736f6c634300081c0033\r\n";

    private static String librariesLinkedBinary;

    public static final String FUNC_BETRESERVE = "betReserve";

    public static final String FUNC_BETTOKEN = "betToken";

    public static final String FUNC_BUYFANTOKEN = "buyFanToken";

    public static final String FUNC_FANTOKEN = "fanToken";

    public static final String FUNC_FANTOKENRESERVE = "fanTokenReserve";

    public static final String FUNC_GETRESERVES = "getReserves";

    public static final String FUNC_ISINITIALIZED = "isInitialized";

    public static final String FUNC_SELLFANTOKEN = "sellFanToken";

    public static final String FUNC_SETINITIALLIQUIDITY = "setInitialLiquidity";

    @Deprecated
    protected LiquidityPool(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected LiquidityPool(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected LiquidityPool(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected LiquidityPool(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> betReserve() {
        final Function function = new Function(FUNC_BETRESERVE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> betToken() {
        final Function function = new Function(FUNC_BETTOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> buyFanToken(BigInteger amountBETIn, String to) {
        final Function function = new Function(
                FUNC_BUYFANTOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountBETIn), 
                new org.web3j.abi.datatypes.Address(160, to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> fanToken() {
        final Function function = new Function(FUNC_FANTOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> fanTokenReserve() {
        final Function function = new Function(FUNC_FANTOKENRESERVE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> getReserves() {
        final Function function = new Function(FUNC_GETRESERVES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Boolean> isInitialized() {
        final Function function = new Function(FUNC_ISINITIALIZED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> sellFanToken(BigInteger amountFanTokenIn,
            String to) {
        final Function function = new Function(
                FUNC_SELLFANTOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountFanTokenIn), 
                new org.web3j.abi.datatypes.Address(160, to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setInitialLiquidity(BigInteger _betAmount,
            BigInteger _fanTokenAmount) {
        final Function function = new Function(
                FUNC_SETINITIALLIQUIDITY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_betAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(_fanTokenAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static LiquidityPool load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new LiquidityPool(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static LiquidityPool load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new LiquidityPool(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static LiquidityPool load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new LiquidityPool(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static LiquidityPool load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new LiquidityPool(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<LiquidityPool> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider, String _betToken, String _fanToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _betToken), 
                new org.web3j.abi.datatypes.Address(160, _fanToken)));
        return deployRemoteCall(LiquidityPool.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    public static RemoteCall<LiquidityPool> deploy(Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider,
            String _betToken, String _fanToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _betToken), 
                new org.web3j.abi.datatypes.Address(160, _fanToken)));
        return deployRemoteCall(LiquidityPool.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<LiquidityPool> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit, String _betToken, String _fanToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _betToken), 
                new org.web3j.abi.datatypes.Address(160, _fanToken)));
        return deployRemoteCall(LiquidityPool.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<LiquidityPool> deploy(Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit,
            String _betToken, String _fanToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _betToken), 
                new org.web3j.abi.datatypes.Address(160, _fanToken)));
        return deployRemoteCall(LiquidityPool.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }


    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }
}
