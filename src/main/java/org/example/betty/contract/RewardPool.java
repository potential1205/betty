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
    public static final String BINARY = "癤�0x608060405234602257600e60a5565b60146026565b6108486100b1823961084890f35b602c565b60405190565b600080fd5b60001b90565b90604660018060a01b03916031565b9181191691161790565b60018060a01b031690565b90565b606d60696071926050565b605b565b6050565b90565b607b90605e565b90565b6085906074565b90565b90565b90609b609760a192607e565b6088565b82546037565b9055565b60ae336000608b565b56fe60806040526004361015610013575b61029f565b61001e60003561005d565b8063135488d61461005857806313af4035146100535780638da5cb5b1461004e5763f8b2cb4f0361000e5761026a565b610212565b610169565b610116565b60e01c90565b60405190565b600080fd5b600080fd5b60018060a01b031690565b61008790610073565b90565b6100938161007e565b0361009a57565b600080fd5b905035906100ac8261008a565b565b90565b6100ba816100ae565b036100c157565b600080fd5b905035906100d3826100b1565b565b909160608284031261010b576101086100f1846000850161009f565b936100ff816020860161009f565b936040016100c6565b90565b61006e565b60000190565b346101455761012f6101293660046100d5565b916105be565b610137610063565b8061014181610110565b0390f35b610069565b90602082820312610164576101619160000161009f565b90565b61006e565b346101975761018161017c36600461014a565b61072d565b610189610063565b8061019381610110565b0390f35b610069565b60009103126101a757565b61006e565b1c90565b60018060a01b031690565b6101cb9060086101d093026101ac565b6101b0565b90565b906101de91546101bb565b90565b6101ec6000806101d3565b90565b6101f89061007e565b9052565b9190610210906000602085019401906101ef565b565b346102425761022236600461019c565b61023e61022d6101e1565b610235610063565b918291826101fc565b0390f35b610069565b610250906100ae565b9052565b919061026890600060208501940190610247565b565b3461029a5761029661028561028036600461014a565b610777565b61028d610063565b91829182610254565b0390f35b610069565b600080fd5b60001c90565b6102b66102bb916102a4565b6101b0565b90565b6102c890546102aa565b90565b60209181520190565b60007f4e6f74206f776e65720000000000000000000000000000000000000000000000910152565b61030960096020926102cb565b610312816102d4565b0190565b61032c90602081019060008183039101526102fc565b90565b1561033657565b61033e610063565b62461bcd60e51b81528061035460048201610316565b0390fd5b9061038892916103833361037d61037761037260006102be565b61007e565b9161007e565b1461032f565b610529565b565b90565b6103a161039c6103a692610073565b61038a565b610073565b90565b6103b29061038d565b90565b6103be906103a9565b90565b6103ca9061038d565b90565b6103d6906103c1565b90565b601f801991011690565b634e487b7160e01b600052604160045260246000fd5b90610403906103d9565b810190811067ffffffffffffffff82111761041d57604052565b6103e3565b60e01b90565b151590565b61043681610428565b0361043d57565b600080fd5b9050519061044f8261042d565b565b9060208282031261046b5761046891600001610442565b90565b61006e565b91602061049292949361048b604082019660008301906101ef565b0190610247565b565b61049c610063565b3d6000823e3d90fd5b60007f5472616e73666572206661696c65640000000000000000000000000000000000910152565b6104da600f6020926102cb565b6104e3816104a5565b0190565b6104fd90602081019060008183039101526104cd565b90565b1561050757565b61050f610063565b62461bcd60e51b815280610525600482016104e7565b0390fd5b9161053e610539602093946103b5565b6103cd565b610562600063a9059cbb95939561056d610556610063565b97889687958694610422565b845260048401610470565b03925af180156105b9576105899160009161058b575b50610500565b565b6105ac915060203d81116105b2575b6105a481836103f9565b810190610451565b38610583565b503d61059a565b610494565b906105c99291610358565b565b6105f9906105f4336105ee6105e86105e360006102be565b61007e565b9161007e565b1461032f565b6106fa565b565b90565b61061261060d610617926105fb565b61038a565b610073565b90565b610623906105fe565b90565b60007f5a65726f20616464726573730000000000000000000000000000000000000000910152565b61065b600c6020926102cb565b61066481610626565b0190565b61067e906020810190600081830391015261064e565b90565b1561068857565b610690610063565b62461bcd60e51b8152806106a660048201610668565b0390fd5b60001b90565b906106c160018060a01b03916106aa565b9181191691161790565b6106d4906103c1565b90565b90565b906106ef6106ea6106f6926106cb565b6106d7565b82546106b0565b9055565b61072b906107248161071d610717610712600061061a565b61007e565b9161007e565b1415610681565b60006106da565b565b610736906105cb565b565b600090565b610746906103c1565b90565b90505190610756826100b1565b565b906020828203126107725761076f91600001610749565b90565b61006e565b60206107966107916107c89361078b610738565b506103b5565b6103cd565b6370a08231906107bd6107a83061073d565b926107b1610063565b95869485938493610422565b8352600483016101fc565b03915afa90811561080d576000916107df575b5090565b610800915060203d8111610806575b6107f881836103f9565b810190610758565b386107db565b503d6107ee565b61049456fea26469706673582212201f0d28099e5d870ce179bd52bcaa7a86c31d852150b57f027686580a2b42e9fa64736f6c634300081c0033\r\n";

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
