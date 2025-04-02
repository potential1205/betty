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
public class LiquidityPool extends Contract {
    public static final String BINARY = "癤�0x608060405234801561000f575f5ffd5b50604051610cb6380380610cb683398181016040528101906100319190610115565b815f5f6101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508060015f6101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505050610153565b5f5ffd5b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f6100e4826100bb565b9050919050565b6100f4816100da565b81146100fe575f5ffd5b50565b5f8151905061010f816100eb565b92915050565b5f5f6040838503121561012b5761012a6100b7565b5b5f61013885828601610101565b925050602061014985828601610101565b9150509250929050565b610b56806101605f395ff3fe608060405234801561000f575f5ffd5b506004361061007b575f3560e01c80633bce1c75116100595780633bce1c75146100eb578063663c2b991461011b57806375daa3de14610137578063a42c6b00146101555761007b565b806301b424e51461007f578063074bc1011461009d5780632cf5dbda146100bb575b5f5ffd5b610087610173565b60405161009491906105b0565b60405180910390f35b6100a5610179565b6040516100b29190610643565b60405180910390f35b6100d560048036038101906100d091906106c5565b61019d565b6040516100e291906105b0565b60405180910390f35b610105600480360381019061010091906106c5565b610354565b60405161011291906105b0565b60405180910390f35b61013560048036038101906101309190610703565b61050a565b005b61013f61056d565b60405161014c9190610643565b60405180910390f35b61015d610592565b60405161016a91906105b0565b60405180910390f35b60025481565b5f5f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b5f5f83116101e0576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016101d79061079b565b60405180910390fd5b5f836002546101ef91906107e6565b90505f816003546002546102039190610819565b61020d9190610887565b90508060035411610253576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161024a90610901565b60405180910390fd5b80600354610261919061091f565b9250816002819055508060038190555060015f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb85856040518363ffffffff1660e01b81526004016102cd929190610961565b6020604051808303815f875af11580156102e9573d5f5f3e3d5ffd5b505050506040513d601f19601f8201168201806040525081019061030d91906109bd565b61034c576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161034390610a32565b60405180910390fd5b505092915050565b5f5f8311610397576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161038e9061079b565b60405180910390fd5b5f836003546103a691906107e6565b90505f816003546002546103ba9190610819565b6103c49190610887565b9050806002541161040a576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161040190610901565b60405180910390fd5b80600254610418919061091f565b925081600381905550806002819055505f5f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb85856040518363ffffffff1660e01b8152600401610483929190610961565b6020604051808303815f875af115801561049f573d5f5f3e3d5ffd5b505050506040513d601f19601f820116820180604052508101906104c391906109bd565b610502576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016104f990610a9a565b60405180910390fd5b505092915050565b5f60025414801561051c57505f600354145b61055b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161055290610b02565b60405180910390fd5b81600281905550806003819055505050565b60015f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60035481565b5f819050919050565b6105aa81610598565b82525050565b5f6020820190506105c35f8301846105a1565b92915050565b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f819050919050565b5f61060b610606610601846105c9565b6105e8565b6105c9565b9050919050565b5f61061c826105f1565b9050919050565b5f61062d82610612565b9050919050565b61063d81610623565b82525050565b5f6020820190506106565f830184610634565b92915050565b5f5ffd5b61066981610598565b8114610673575f5ffd5b50565b5f8135905061068481610660565b92915050565b5f610694826105c9565b9050919050565b6106a48161068a565b81146106ae575f5ffd5b50565b5f813590506106bf8161069b565b92915050565b5f5f604083850312156106db576106da61065c565b5b5f6106e885828601610676565b92505060206106f9858286016106b1565b9150509250929050565b5f5f604083850312156107195761071861065c565b5b5f61072685828601610676565b925050602061073785828601610676565b9150509250929050565b5f82825260208201905092915050565b7f416d6f756e74206d7573742062652067726561746572207468616e20300000005f82015250565b5f610785601d83610741565b915061079082610751565b602082019050919050565b5f6020820190508181035f8301526107b281610779565b9050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52601160045260245ffd5b5f6107f082610598565b91506107fb83610598565b9250828201905080821115610813576108126107b9565b5b92915050565b5f61082382610598565b915061082e83610598565b925082820261083c81610598565b91508282048414831517610853576108526107b9565b5b5092915050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52601260045260245ffd5b5f61089182610598565b915061089c83610598565b9250826108ac576108ab61085a565b5b828204905092915050565b7f496e76616c6964206f75747075742063616c63756c6174696f6e0000000000005f82015250565b5f6108eb601a83610741565b91506108f6826108b7565b602082019050919050565b5f6020820190508181035f830152610918816108df565b9050919050565b5f61092982610598565b915061093483610598565b925082820390508181111561094c5761094b6107b9565b5b92915050565b61095b8161068a565b82525050565b5f6040820190506109745f830185610952565b61098160208301846105a1565b9392505050565b5f8115159050919050565b61099c81610988565b81146109a6575f5ffd5b50565b5f815190506109b781610993565b92915050565b5f602082840312156109d2576109d161065c565b5b5f6109df848285016109a9565b91505092915050565b7f46616e20746f6b656e207472616e73666572206661696c6564000000000000005f82015250565b5f610a1c601983610741565b9150610a27826109e8565b602082019050919050565b5f6020820190508181035f830152610a4981610a10565b9050919050565b7f425443207472616e73666572206661696c6564000000000000000000000000005f82015250565b5f610a84601383610741565b9150610a8f82610a50565b602082019050919050565b5f6020820190508181035f830152610ab181610a78565b9050919050565b7f416c726561647920696e697469616c697a6564000000000000000000000000005f82015250565b5f610aec601383610741565b9150610af782610ab8565b602082019050919050565b5f6020820190508181035f830152610b1981610ae0565b905091905056fea264697066735822122073549941332d0196aebd06d3d4e5ca1ba8b8d0572b2bb50849a7fb235e07eadc64736f6c634300081c0033\r\n";

    private static String librariesLinkedBinary;

    public static final String FUNC_BTCRESERVE = "btcReserve";

    public static final String FUNC_BTCTOKEN = "btcToken";

    public static final String FUNC_BUYFANTOKEN = "buyFanToken";

    public static final String FUNC_FANTOKEN = "fanToken";

    public static final String FUNC_FANTOKENRESERVE = "fanTokenReserve";

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

    public RemoteFunctionCall<BigInteger> btcReserve() {
        final Function function = new Function(FUNC_BTCRESERVE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> btcToken() {
        final Function function = new Function(FUNC_BTCTOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> buyFanToken(BigInteger amountBTCIn, String to) {
        final Function function = new Function(
                FUNC_BUYFANTOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountBTCIn), 
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

    public RemoteFunctionCall<TransactionReceipt> sellFanToken(BigInteger amountFanTokenIn,
            String to) {
        final Function function = new Function(
                FUNC_SELLFANTOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountFanTokenIn), 
                new org.web3j.abi.datatypes.Address(160, to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setInitialLiquidity(BigInteger _btcAmount,
            BigInteger _fanTokenAmount) {
        final Function function = new Function(
                FUNC_SETINITIALLIQUIDITY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_btcAmount), 
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
            ContractGasProvider contractGasProvider, String _btcToken, String _fanToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _btcToken), 
                new org.web3j.abi.datatypes.Address(160, _fanToken)));
        return deployRemoteCall(LiquidityPool.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    public static RemoteCall<LiquidityPool> deploy(Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider,
            String _btcToken, String _fanToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _btcToken), 
                new org.web3j.abi.datatypes.Address(160, _fanToken)));
        return deployRemoteCall(LiquidityPool.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<LiquidityPool> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit, String _btcToken, String _fanToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _btcToken), 
                new org.web3j.abi.datatypes.Address(160, _fanToken)));
        return deployRemoteCall(LiquidityPool.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<LiquidityPool> deploy(Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit,
            String _btcToken, String _fanToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _btcToken), 
                new org.web3j.abi.datatypes.Address(160, _fanToken)));
        return deployRemoteCall(LiquidityPool.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }


    private static String getDeploymentBinary() {
        return BINARY;
    }
}
