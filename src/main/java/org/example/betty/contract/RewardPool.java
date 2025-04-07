package org.example.betty.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
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
public class RewardPool extends Contract {
    public static final String BINARY = "癤�0x6080604052348015600f57600080fd5b50336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506107d68061005f6000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c8063135488d61461005157806313af40351461006d5780638da5cb5b14610089578063f8b2cb4f146100a7575b600080fd5b61006b600480360381019061006691906104a7565b6100d7565b005b610087600480360381019061008291906104fa565b610227565b005b610091610367565b60405161009e9190610536565b60405180910390f35b6100c160048036038101906100bc91906104fa565b61038b565b6040516100ce9190610560565b60405180910390f35b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610165576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161015c906105d8565b60405180910390fd5b8273ffffffffffffffffffffffffffffffffffffffff1663a9059cbb83836040518363ffffffff1660e01b81526004016101a09291906105f8565b6020604051808303816000875af11580156101bf573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906101e39190610659565b610222576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610219906106d2565b60405180910390fd5b505050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146102b5576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102ac906105d8565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1603610324576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161031b9061073e565b60405180910390fd5b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008173ffffffffffffffffffffffffffffffffffffffff166370a08231306040518263ffffffff1660e01b81526004016103c69190610536565b602060405180830381865afa1580156103e3573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104079190610773565b9050919050565b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b600061043e82610413565b9050919050565b61044e81610433565b811461045957600080fd5b50565b60008135905061046b81610445565b92915050565b6000819050919050565b61048481610471565b811461048f57600080fd5b50565b6000813590506104a18161047b565b92915050565b6000806000606084860312156104c0576104bf61040e565b5b60006104ce8682870161045c565b93505060206104df8682870161045c565b92505060406104f086828701610492565b9150509250925092565b6000602082840312156105105761050f61040e565b5b600061051e8482850161045c565b91505092915050565b61053081610433565b82525050565b600060208201905061054b6000830184610527565b92915050565b61055a81610471565b82525050565b60006020820190506105756000830184610551565b92915050565b600082825260208201905092915050565b7f4e6f74206f776e65720000000000000000000000000000000000000000000000600082015250565b60006105c260098361057b565b91506105cd8261058c565b602082019050919050565b600060208201905081810360008301526105f1816105b5565b9050919050565b600060408201905061060d6000830185610527565b61061a6020830184610551565b9392505050565b60008115159050919050565b61063681610621565b811461064157600080fd5b50565b6000815190506106538161062d565b92915050565b60006020828403121561066f5761066e61040e565b5b600061067d84828501610644565b91505092915050565b7f5472616e73666572206661696c65640000000000000000000000000000000000600082015250565b60006106bc600f8361057b565b91506106c782610686565b602082019050919050565b600060208201905081810360008301526106eb816106af565b9050919050565b7f5a65726f20616464726573730000000000000000000000000000000000000000600082015250565b6000610728600c8361057b565b9150610733826106f2565b602082019050919050565b600060208201905081810360008301526107578161071b565b9050919050565b60008151905061076d8161047b565b92915050565b6000602082840312156107895761078861040e565b5b60006107978482850161075e565b9150509291505056fea26469706673582212208d528ff67ed39c4054e362adf6b5f39b3ded289990e43604372e73921f0f951564736f6c634300081c0033\r\n";

    private static String librariesLinkedBinary;

    public static final String FUNC_GETBALANCE = "getBalance";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_REWARD = "reward";

    public static final String FUNC_SETOWNER = "setOwner";

    @Deprecated
    protected RewardPool(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected RewardPool(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected RewardPool(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected RewardPool(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> getBalance(String token) {
        final Function function = new Function(FUNC_GETBALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> reward(String token, String to,
            BigInteger amount) {
        final Function function = new Function(
                FUNC_REWARD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setOwner(String newOwner) {
        final Function function = new Function(
                FUNC_SETOWNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static RewardPool load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new RewardPool(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static RewardPool load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new RewardPool(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static RewardPool load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new RewardPool(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static RewardPool load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new RewardPool(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<RewardPool> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(RewardPool.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    public static RemoteCall<RewardPool> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(RewardPool.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<RewardPool> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(RewardPool.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<RewardPool> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(RewardPool.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }
}
