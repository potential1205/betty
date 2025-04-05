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
    public static final String BINARY = "癤�0x608060405234801561001057600080fd5b506040516113ba3803806113ba833981810160405281019061003291906100db565b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050610108565b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006100a88261007d565b9050919050565b6100b88161009d565b81146100c357600080fd5b50565b6000815190506100d5816100af565b92915050565b6000602082840312156100f1576100f0610078565b5b60006100ff848285016100c6565b91505092915050565b6112a3806101176000396000f3fe608060405234801561001057600080fd5b50600436106100b45760003560e01c80635383130b116100715780635383130b1461018b5780636028e667146101bb5780639f656321146101eb578063a451f6021461021b578063b11647941461024b578063e90ca51a1461027b576100b4565b8063074bc101146100b95780630ca5c504146100d75780631003e2d21461010757806322cb8f17146101235780634cc822151461013f578063527986331461015b575b600080fd5b6100c1610297565b6040516100ce9190610c70565b60405180910390f35b6100f160048036038101906100ec9190610e1b565b6102bb565b6040516100fe9190610e86565b60405180910390f35b610121600480360381019061011c9190610ea1565b61042d565b005b61013d60048036038101906101389190610e1b565b6104d1565b005b61015960048036038101906101549190610ea1565b610586565b005b61017560048036038101906101709190610ece565b610628565b6040516101829190610f38565b60405180910390f35b6101a560048036038101906101a09190610ece565b610670565b6040516101b29190610f38565b60405180910390f35b6101d560048036038101906101d09190610ece565b6106b8565b6040516101e29190610f74565b60405180910390f35b61020560048036038101906102009190610e1b565b610701565b6040516102129190610e86565b60405180910390f35b61023560048036038101906102309190610f8f565b610891565b6040516102429190610e86565b60405180910390f35b61026560048036038101906102609190610ece565b610ae9565b6040516102729190610c70565b60405180910390f35b61029560048036038101906102909190611046565b610b32565b005b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000806002846040516102ce9190611126565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3383866040518463ffffffff1660e01b815260040161035c9392919061113d565b6020604051808303816000875af115801561037b573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061039f91906111ac565b5060008173ffffffffffffffffffffffffffffffffffffffff16632cf5dbda85336040518363ffffffff1660e01b81526004016103dd9291906111d9565b6020604051808303816000875af11580156103fc573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104209190611217565b9050809250505092915050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3330846040518463ffffffff1660e01b815260040161048a9392919061113d565b6020604051808303816000875af11580156104a9573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104cd91906111ac565b5050565b60006001836040516104e39190611126565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508073ffffffffffffffffffffffffffffffffffffffff166379cc679033846040518363ffffffff1660e01b815260040161054f929190611244565b600060405180830381600087803b15801561056957600080fd5b505af115801561057d573d6000803e3d6000fd5b50505050505050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb33836040518363ffffffff1660e01b81526004016105e1929190611244565b6020604051808303816000875af1158015610600573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061062491906111ac565b5050565b600060028260405161063a9190611126565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050919050565b60006001826040516106829190611126565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050919050565b6002818051602081018201805184825260208301602085012081835280955050505050506000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000806002846040516107149190611126565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506001846040516107559190611126565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3383866040518463ffffffff1660e01b81526004016107c09392919061113d565b6020604051808303816000875af11580156107df573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061080391906111ac565b5060008173ffffffffffffffffffffffffffffffffffffffff16633bce1c7585336040518363ffffffff1660e01b81526004016108419291906111d9565b6020604051808303816000875af1158015610860573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906108849190611217565b9050809250505092915050565b6000806002856040516108a49190611126565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060006002856040516108e79190611126565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506001866040516109289190611126565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3384876040518463ffffffff1660e01b81526004016109939392919061113d565b6020604051808303816000875af11580156109b2573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906109d691906111ac565b5060008273ffffffffffffffffffffffffffffffffffffffff16633bce1c7586846040518363ffffffff1660e01b8152600401610a149291906111d9565b6020604051808303816000875af1158015610a33573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610a579190611217565b905060008273ffffffffffffffffffffffffffffffffffffffff16632cf5dbda83336040518363ffffffff1660e01b8152600401610a969291906111d9565b6020604051808303816000875af1158015610ab5573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610ad99190611217565b9050809450505050509392505050565b6001818051602081018201805184825260208301602085012081835280955050505050506000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b81600184604051610b439190611126565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600284604051610ba09190611126565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b6000610c36610c31610c2c84610bf1565b610c11565b610bf1565b9050919050565b6000610c4882610c1b565b9050919050565b6000610c5a82610c3d565b9050919050565b610c6a81610c4f565b82525050565b6000602082019050610c856000830184610c61565b92915050565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b610cf282610ca9565b810181811067ffffffffffffffff82111715610d1157610d10610cba565b5b80604052505050565b6000610d24610c8b565b9050610d308282610ce9565b919050565b600067ffffffffffffffff821115610d5057610d4f610cba565b5b610d5982610ca9565b9050602081019050919050565b82818337600083830152505050565b6000610d88610d8384610d35565b610d1a565b905082815260208101848484011115610da457610da3610ca4565b5b610daf848285610d66565b509392505050565b600082601f830112610dcc57610dcb610c9f565b5b8135610ddc848260208601610d75565b91505092915050565b6000819050919050565b610df881610de5565b8114610e0357600080fd5b50565b600081359050610e1581610def565b92915050565b60008060408385031215610e3257610e31610c95565b5b600083013567ffffffffffffffff811115610e5057610e4f610c9a565b5b610e5c85828601610db7565b9250506020610e6d85828601610e06565b9150509250929050565b610e8081610de5565b82525050565b6000602082019050610e9b6000830184610e77565b92915050565b600060208284031215610eb757610eb6610c95565b5b6000610ec584828501610e06565b91505092915050565b600060208284031215610ee457610ee3610c95565b5b600082013567ffffffffffffffff811115610f0257610f01610c9a565b5b610f0e84828501610db7565b91505092915050565b6000610f2282610bf1565b9050919050565b610f3281610f17565b82525050565b6000602082019050610f4d6000830184610f29565b92915050565b6000610f5e82610c3d565b9050919050565b610f6e81610f53565b82525050565b6000602082019050610f896000830184610f65565b92915050565b600080600060608486031215610fa857610fa7610c95565b5b600084013567ffffffffffffffff811115610fc657610fc5610c9a565b5b610fd286828701610db7565b935050602084013567ffffffffffffffff811115610ff357610ff2610c9a565b5b610fff86828701610db7565b925050604061101086828701610e06565b9150509250925092565b61102381610f17565b811461102e57600080fd5b50565b6000813590506110408161101a565b92915050565b60008060006060848603121561105f5761105e610c95565b5b600084013567ffffffffffffffff81111561107d5761107c610c9a565b5b61108986828701610db7565b935050602061109a86828701611031565b92505060406110ab86828701611031565b9150509250925092565b600081519050919050565b600081905092915050565b60005b838110156110e95780820151818401526020810190506110ce565b60008484015250505050565b6000611100826110b5565b61110a81856110c0565b935061111a8185602086016110cb565b80840191505092915050565b600061113282846110f5565b915081905092915050565b60006060820190506111526000830186610f29565b61115f6020830185610f29565b61116c6040830184610e77565b949350505050565b60008115159050919050565b61118981611174565b811461119457600080fd5b50565b6000815190506111a681611180565b92915050565b6000602082840312156111c2576111c1610c95565b5b60006111d084828501611197565b91505092915050565b60006040820190506111ee6000830185610e77565b6111fb6020830184610f29565b9392505050565b60008151905061121181610def565b92915050565b60006020828403121561122d5761122c610c95565b5b600061123b84828501611202565b91505092915050565b60006040820190506112596000830185610f29565b6112666020830184610e77565b939250505056fea2646970667358221220394cc5a7bf4cf072e2436e9b821a939507b535b91d5a9880634f33c8694b5a2d64736f6c634300081c0033\r\n";

    private static String librariesLinkedBinary;

    public static final String FUNC_ADD = "add";

    public static final String FUNC_ADDFANTOKEN = "addFanToken";

    public static final String FUNC_BTCTOKEN = "btcToken";

    public static final String FUNC_BUY = "buy";

    public static final String FUNC_FANTOKENS = "fanTokens";

    public static final String FUNC_GETFANTOKENADDRESS = "getFanTokenAddress";

    public static final String FUNC_GETLIQUIDITYPOOLADDRESS = "getLiquidityPoolAddress";

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

    public RemoteFunctionCall<String> getFanTokenAddress(String token_name) {
        final Function function = new Function(FUNC_GETFANTOKENADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getLiquidityPoolAddress(String token_name) {
        final Function function = new Function(FUNC_GETLIQUIDITYPOOLADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name)), 
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
