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
    public static final String BINARY = "癤�0x6080604052346100305761001a61001461010a565b906102ef565b610022610035565b610bfd6103648239610bfd90f35b61003b565b60405190565b600080fd5b601f801991011690565b634e487b7160e01b600052604160045260246000fd5b9061006a90610040565b810190811060018060401b0382111761008257604052565b61004a565b9061009a610093610035565b9283610060565b565b600080fd5b60018060a01b031690565b6100b5906100a1565b90565b6100c1816100ac565b036100c857565b600080fd5b905051906100da826100b8565b565b919060408382031261010557806100f961010292600086016100cd565b936020016100cd565b90565b61009c565b610128610f618038038061011d81610087565b9283398101906100dc565b9091565b90565b90565b61014661014161014b9261012c565b61012f565b6100a1565b90565b61015790610132565b90565b60209181520190565b60007f42455420746f6b656e2061646472657373206973207a65726f00000000000000910152565b610198601960209261015a565b6101a181610163565b0190565b6101bb906020810190600081830391015261018b565b90565b156101c557565b6101cd610035565b62461bcd60e51b8152806101e3600482016101a5565b0390fd5b60007f46616e20746f6b656e2061646472657373206973207a65726f00000000000000910152565b61021c601960209261015a565b610225816101e7565b0190565b61023f906020810190600081830391015261020f565b90565b1561024957565b610251610035565b62461bcd60e51b81528061026760048201610229565b0390fd5b61027f61027a610284926100a1565b61012f565b6100a1565b90565b6102909061026b565b90565b61029c90610287565b90565b60001b90565b906102b660018060a01b039161029f565b9181191691161790565b6102c990610287565b90565b90565b906102e46102df6102eb926102c0565b6102cc565b82546102a5565b9055565b6103619161035561034e61035a936103238161031c610316610311600061014e565b6100ac565b916100ac565b14156101be565b6103498461034261033c610337600061014e565b6100ac565b916100ac565b1415610242565b610293565b60006102cf565b610293565b60016102cf565b56fe60806040526004361015610013575b6104db565b61001e6000356100ad565b80630902f1ac146100a85780631b1c539b146100a35780632cf5dbda1461009e578063392e53cd146100995780633bce1c7514610094578063663c2b991461008f57806375daa3de1461008a57806378691f16146100855763a42c6b000361000e576104a6565b610461565b61041e565b61034f565b6102e5565b6102b0565b610252565b610190565b610107565b60e01c90565b60405190565b600080fd5b600080fd5b60009103126100ce57565b6100be565b90565b6100df906100d3565b9052565b9160206101059294936100fe604082019660008301906100d6565b01906100d6565b565b34610138576101173660046100c3565b61011f61050c565b9061013461012b6100b3565b928392836100e3565b0390f35b6100b9565b1c90565b90565b610154906008610159930261013d565b610141565b90565b906101679154610144565b90565b610177600260009061015c565b90565b919061018e906000602085019401906100d6565b565b346101c0576101a03660046100c3565b6101bc6101ab61016a565b6101b36100b3565b9182918261017a565b0390f35b6100b9565b6101ce816100d3565b036101d557565b600080fd5b905035906101e7826101c5565b565b60018060a01b031690565b6101fd906101e9565b90565b610209816101f4565b0361021057565b600080fd5b9050359061022282610200565b565b919060408382031261024d578061024161024a92600086016101da565b93602001610215565b90565b6100be565b346102835761027f61026e610268366004610224565b90610895565b6102766100b3565b9182918261017a565b0390f35b6100b9565b151590565b61029690610288565b9052565b91906102ae9060006020850194019061028d565b565b346102e0576102c03660046100c3565b6102dc6102cb6109fa565b6102d36100b3565b9182918261029a565b0390f35b6100b9565b34610316576103126103016102fb366004610224565b90610a4f565b6103096100b3565b9182918261017a565b0390f35b6100b9565b9190604083820312610344578061033861034192600086016101da565b936020016101da565b90565b6100be565b60000190565b3461037e5761036861036236600461031b565b90610baf565b6103706100b3565b8061037a81610349565b0390f35b6100b9565b60018060a01b031690565b61039e9060086103a3930261013d565b610383565b90565b906103b1915461038e565b90565b6103c160016000906103a6565b90565b90565b6103db6103d66103e0926101e9565b6103c4565b6101e9565b90565b6103ec906103c7565b90565b6103f8906103e3565b90565b610404906103ef565b9052565b919061041c906000602085019401906103fb565b565b3461044e5761042e3660046100c3565b61044a6104396103b4565b6104416100b3565b91829182610408565b0390f35b6100b9565b61045e6000806103a6565b90565b34610491576104713660046100c3565b61048d61047c610453565b6104846100b3565b91829182610408565b0390f35b6100b9565b6104a3600360009061015c565b90565b346104d6576104b63660046100c3565b6104d26104c1610496565b6104c96100b3565b9182918261017a565b0390f35b6100b9565b600080fd5b600090565b60001c90565b6104f76104fc916104e5565b610141565b90565b61050990546104eb565b90565b6105146104e0565b5061051d6104e0565b5061052860026104ff565b9061053360036104ff565b90565b90565b61054d61054861055292610536565b6103c4565b6100d3565b90565b60209181520190565b60007f416d6f756e74206d7573742062652067726561746572207468616e2030000000910152565b610593601d602092610555565b61059c8161055e565b0190565b6105b69060208101906000818303910152610586565b90565b156105c057565b6105c86100b3565b62461bcd60e51b8152806105de600482016105a0565b0390fd5b634e487b7160e01b600052601160045260246000fd5b61060761060d919392936100d3565b926100d3565b820180921161061857565b6105e2565b61062c610632919392936100d3565b926100d3565b9161063e8382026100d3565b92818404149015171561064d57565b6105e2565b634e487b7160e01b600052601260045260246000fd5b61067461067a916100d3565b916100d3565b908115610685570490565b610652565b60007f496e76616c6964206f75747075742063616c63756c6174696f6e000000000000910152565b6106bf601a602092610555565b6106c88161068a565b0190565b6106e290602081019060008183039101526106b2565b90565b156106ec57565b6106f46100b3565b62461bcd60e51b81528061070a600482016106cc565b0390fd5b61071d610723919392936100d3565b926100d3565b820391821161072e57565b6105e2565b60001b90565b9061074660001991610733565b9181191691161790565b61076461075f610769926100d3565b6103c4565b6100d3565b90565b90565b9061078461077f61078b92610750565b61076c565b8254610739565b9055565b61079b6107a0916104e5565b610383565b90565b6107ad905461078f565b90565b6107b9906103c7565b90565b6107c5906107b0565b90565b6107d1906103e3565b90565b6107dd906103e3565b90565b600080fd5b601f801991011690565b634e487b7160e01b600052604160045260246000fd5b9061080f906107e5565b810190811067ffffffffffffffff82111761082957604052565b6107ef565b60e01b90565b600091031261083f57565b6100be565b61084d906101f4565b9052565b60409061087b610882949695939661087160608401986000850190610844565b6020830190610844565b01906100d6565b565b61088c6100b3565b3d6000823e3d90fd5b6108ce906108a16104e0565b506108bf816108b96108b36000610539565b916100d3565b116105b9565b6108c960026104ff565b6105f8565b906109446108f96108f26108e260026104ff565b6108ec60036104ff565b9061061d565b8490610668565b61091e61090660036104ff565b610918610912846100d3565b916100d3565b116106e5565b61093d61093561092e60036104ff565b839061070e565b94600261076f565b600361076f565b61096661096161095c61095760016107a3565b6103ef565b6107bc565b6107c8565b9063da72c1e890610976306107d4565b90928492813b156109f05760006109a0916109ab82966109946100b3565b9889978896879561082e565b855260048501610851565b03925af180156109eb576109be575b5090565b6109de9060003d81116109e4575b6109d68183610805565b810190610834565b386109ba565b503d6109cc565b610884565b6107e0565b600090565b610a026109f5565b50610a0d60026104ff565b610a20610a1a6000610539565b916100d3565b118015610a2b575b90565b50610a3660036104ff565b610a49610a436000610539565b916100d3565b11610a28565b610a8890610a5b6104e0565b50610a7981610a73610a6d6000610539565b916100d3565b116105b9565b610a8360036104ff565b6105f8565b90610afe610ab3610aac610a9c60026104ff565b610aa660036104ff565b9061061d565b8490610668565b610ad8610ac060026104ff565b610ad2610acc846100d3565b916100d3565b116106e5565b610af7610aef610ae860026104ff565b839061070e565b94600361076f565b600261076f565b610b20610b1b610b16610b1160006107a3565b6103ef565b6107bc565b6107c8565b9063da72c1e890610b30306107d4565b90928492813b15610baa576000610b5a91610b658296610b4e6100b3565b9889978896879561082e565b855260048501610851565b03925af18015610ba557610b78575b5090565b610b989060003d8111610b9e575b610b908183610805565b810190610834565b38610b74565b503d610b86565b610884565b6107e0565b90610bbe610bc592600261076f565b600361076f565b56fea2646970667358221220d2aaade3c94e414c029c62ff8507f02691ac4c78dfd3de5d06cbcebbfb7fd4fa64736f6c634300081c0033\r\n";

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
//java 수정