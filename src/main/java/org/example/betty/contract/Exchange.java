package org.example.betty.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
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
    public static final String BINARY = "癤�0x608060405234801561001057600080fd5b50604051611224380380611224833981810160405281019061003291906100db565b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050610108565b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006100a88261007d565b9050919050565b6100b88161009d565b81146100c357600080fd5b50565b6000815190506100d5816100af565b92915050565b6000602082840312156100f1576100f0610078565b5b60006100ff848285016100c6565b91505092915050565b61110d806101176000396000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c80636028e667116100665780636028e667146101315780639f65632114610161578063a451f6021461017d578063b116479414610199578063e90ca51a146101c95761009e565b8063074bc101146100a35780630ca5c504146100c15780631003e2d2146100dd57806322cb8f17146100f95780634cc8221514610115575b600080fd5b6100ab6101e5565b6040516100b89190610b10565b60405180910390f35b6100db60048036038101906100d69190610cbb565b610209565b005b6100f760048036038101906100f29190610d17565b610371565b005b610113600480360381019061010e9190610cbb565b610415565b005b61012f600480360381019061012a9190610d17565b6104ca565b005b61014b60048036038101906101469190610d44565b61056c565b6040516101589190610dae565b60405180910390f35b61017b60048036038101906101769190610cbb565b6105b5565b005b61019760048036038101906101929190610dc9565b61073b565b005b6101b360048036038101906101ae9190610d44565b610989565b6040516101c09190610b10565b60405180910390f35b6101e360048036038101906101de9190610e92565b6109d2565b005b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600060028360405161021b9190610f72565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3383856040518463ffffffff1660e01b81526004016102a993929190610fa7565b6020604051808303816000875af11580156102c8573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906102ec9190611016565b508073ffffffffffffffffffffffffffffffffffffffff16632cf5dbda83336040518363ffffffff1660e01b8152600401610328929190611043565b6020604051808303816000875af1158015610347573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061036b9190611081565b50505050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3330846040518463ffffffff1660e01b81526004016103ce93929190610fa7565b6020604051808303816000875af11580156103ed573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104119190611016565b5050565b60006001836040516104279190610f72565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508073ffffffffffffffffffffffffffffffffffffffff166379cc679033846040518363ffffffff1660e01b81526004016104939291906110ae565b600060405180830381600087803b1580156104ad57600080fd5b505af11580156104c1573d6000803e3d6000fd5b50505050505050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb33836040518363ffffffff1660e01b81526004016105259291906110ae565b6020604051808303816000875af1158015610544573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906105689190611016565b5050565b6002818051602081018201805184825260208301602085012081835280955050505050506000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60006002836040516105c79190610f72565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506001836040516106089190610f72565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3383856040518463ffffffff1660e01b815260040161067393929190610fa7565b6020604051808303816000875af1158015610692573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906106b69190611016565b508073ffffffffffffffffffffffffffffffffffffffff16633bce1c7583336040518363ffffffff1660e01b81526004016106f2929190611043565b6020604051808303816000875af1158015610711573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906107359190611081565b50505050565b600060028460405161074d9190610f72565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060006002846040516107909190610f72565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506001856040516107d19190610f72565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3384866040518463ffffffff1660e01b815260040161083c93929190610fa7565b6020604051808303816000875af115801561085b573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061087f9190611016565b5060008273ffffffffffffffffffffffffffffffffffffffff16633bce1c7585846040518363ffffffff1660e01b81526004016108bd929190611043565b6020604051808303816000875af11580156108dc573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906109009190611081565b90508173ffffffffffffffffffffffffffffffffffffffff16632cf5dbda82336040518363ffffffff1660e01b815260040161093d929190611043565b6020604051808303816000875af115801561095c573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906109809190611081565b50505050505050565b6001818051602081018201805184825260208301602085012081835280955050505050506000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b816001846040516109e39190610f72565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600284604051610a409190610f72565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b6000610ad6610ad1610acc84610a91565b610ab1565b610a91565b9050919050565b6000610ae882610abb565b9050919050565b6000610afa82610add565b9050919050565b610b0a81610aef565b82525050565b6000602082019050610b256000830184610b01565b92915050565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b610b9282610b49565b810181811067ffffffffffffffff82111715610bb157610bb0610b5a565b5b80604052505050565b6000610bc4610b2b565b9050610bd08282610b89565b919050565b600067ffffffffffffffff821115610bf057610bef610b5a565b5b610bf982610b49565b9050602081019050919050565b82818337600083830152505050565b6000610c28610c2384610bd5565b610bba565b905082815260208101848484011115610c4457610c43610b44565b5b610c4f848285610c06565b509392505050565b600082601f830112610c6c57610c6b610b3f565b5b8135610c7c848260208601610c15565b91505092915050565b6000819050919050565b610c9881610c85565b8114610ca357600080fd5b50565b600081359050610cb581610c8f565b92915050565b60008060408385031215610cd257610cd1610b35565b5b600083013567ffffffffffffffff811115610cf057610cef610b3a565b5b610cfc85828601610c57565b9250506020610d0d85828601610ca6565b9150509250929050565b600060208284031215610d2d57610d2c610b35565b5b6000610d3b84828501610ca6565b91505092915050565b600060208284031215610d5a57610d59610b35565b5b600082013567ffffffffffffffff811115610d7857610d77610b3a565b5b610d8484828501610c57565b91505092915050565b6000610d9882610add565b9050919050565b610da881610d8d565b82525050565b6000602082019050610dc36000830184610d9f565b92915050565b600080600060608486031215610de257610de1610b35565b5b600084013567ffffffffffffffff811115610e0057610dff610b3a565b5b610e0c86828701610c57565b935050602084013567ffffffffffffffff811115610e2d57610e2c610b3a565b5b610e3986828701610c57565b9250506040610e4a86828701610ca6565b9150509250925092565b6000610e5f82610a91565b9050919050565b610e6f81610e54565b8114610e7a57600080fd5b50565b600081359050610e8c81610e66565b92915050565b600080600060608486031215610eab57610eaa610b35565b5b600084013567ffffffffffffffff811115610ec957610ec8610b3a565b5b610ed586828701610c57565b9350506020610ee686828701610e7d565b9250506040610ef786828701610e7d565b9150509250925092565b600081519050919050565b600081905092915050565b60005b83811015610f35578082015181840152602081019050610f1a565b60008484015250505050565b6000610f4c82610f01565b610f568185610f0c565b9350610f66818560208601610f17565b80840191505092915050565b6000610f7e8284610f41565b915081905092915050565b610f9281610e54565b82525050565b610fa181610c85565b82525050565b6000606082019050610fbc6000830186610f89565b610fc96020830185610f89565b610fd66040830184610f98565b949350505050565b60008115159050919050565b610ff381610fde565b8114610ffe57600080fd5b50565b60008151905061101081610fea565b92915050565b60006020828403121561102c5761102b610b35565b5b600061103a84828501611001565b91505092915050565b60006040820190506110586000830185610f98565b6110656020830184610f89565b9392505050565b60008151905061107b81610c8f565b92915050565b60006020828403121561109757611096610b35565b5b60006110a58482850161106c565b91505092915050565b60006040820190506110c36000830185610f89565b6110d06020830184610f98565b939250505056fea2646970667358221220ee228f2b56c1f17d007612f10cdd2ab18514aa329afea6de4a4197064dfb11d064736f6c634300081c0033\r\n";

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
}
