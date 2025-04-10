package org.example.betty.contract;

import java.math.BigInteger;
import java.util.Arrays;
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
public class ExchangeView extends Contract {
    public static final String BINARY = "癤�0x60806040523461002f576100196100146100fa565b6101a2565b610021610034565b6108896101b8823961088990f35b61003a565b60405190565b600080fd5b601f801991011690565b634e487b7160e01b600052604160045260246000fd5b906100699061003f565b810190811060018060401b0382111761008157604052565b610049565b90610099610092610034565b928361005f565b565b600080fd5b60018060a01b031690565b6100b4906100a0565b90565b6100c0816100ab565b036100c757565b600080fd5b905051906100d9826100b7565b565b906020828203126100f5576100f2916000016100cc565b90565b61009b565b610118610a418038038061010d81610086565b9283398101906100db565b90565b90565b61013261012d610137926100a0565b61011b565b6100a0565b90565b6101439061011e565b90565b61014f9061013a565b90565b60001b90565b9061016960018060a01b0391610152565b9181191691161790565b61017c9061013a565b90565b90565b9061019761019261019e92610173565b61017f565b8254610158565b9055565b6101ae6101b591610146565b6000610182565b56fe60806040526004361015610013575b6102c6565b61001e60003561003d565b80635350758f146100385763d2f7265a0361000e57610291565b6101a4565b60e01c90565b60405190565b600080fd5b600080fd5b600080fd5b600080fd5b600080fd5b601f801991011690565b634e487b7160e01b600052604160045260246000fd5b9061008c90610062565b810190811067ffffffffffffffff8211176100a657604052565b61006c565b906100be6100b7610043565b9283610082565b565b67ffffffffffffffff81116100de576100da602091610062565b0190565b61006c565b90826000939282370152565b909291926101046100ff826100c0565b6100ab565b938185526020850190828401116101205761011e926100e3565b565b61005d565b9080601f8301121561014357816020610140933591016100ef565b90565b610058565b9060208282031261017957600082013567ffffffffffffffff8111610174576101719201610125565b90565b610053565b61004e565b90565b61018a9061017e565b9052565b91906101a290600060208501940190610181565b565b346101d4576101d06101bf6101ba366004610148565b610690565b6101c7610043565b9182918261018e565b0390f35b610049565b60009103126101e457565b61004e565b1c90565b60018060a01b031690565b61020890600861020d93026101e9565b6101ed565b90565b9061021b91546101f8565b90565b610229600080610210565b90565b60018060a01b031690565b90565b61024e6102496102539261022c565b610237565b61022c565b90565b61025f9061023a565b90565b61026b90610256565b90565b61027790610262565b9052565b919061028f9060006020850194019061026e565b565b346102c1576102a13660046101d9565b6102bd6102ac61021e565b6102b4610043565b9182918261027b565b0390f35b610049565b600080fd5b600090565b60001c90565b6102e26102e7916102d0565b6101ed565b90565b6102f490546102d6565b90565b60e01b90565b6103069061022c565b90565b610312816102fd565b0361031957565b600080fd5b9050519061032b82610309565b565b90602082820312610347576103449160000161031e565b90565b61004e565b5190565b60209181520190565b60005b83811061036d575050906000910152565b80602091830151818501520161035c565b61039d6103a66020936103ab936103948161034c565b93848093610350565b95869101610359565b610062565b0190565b6103c5916020820191600081840391015261037e565b90565b6103d0610043565b3d6000823e3d90fd5b90565b6103f06103eb6103f5926103d9565b610237565b61022c565b90565b610401906103dc565b90565b60007f496e76616c696420706f6f6c0000000000000000000000000000000000000000910152565b610439600c602092610350565b61044281610404565b0190565b61045c906020810190600081830391015261042c565b90565b1561046657565b61046e610043565b62461bcd60e51b81528061048460048201610446565b0390fd5b6104919061023a565b90565b61049d90610488565b90565b6104a990610256565b90565b6104b58161017e565b036104bc57565b600080fd5b905051906104ce826104ac565b565b91906040838203126104f957806104ed6104f692600086016104c1565b936020016104c1565b90565b61004e565b60000190565b61051861051361051d926103d9565b610237565b61017e565b90565b60007f506f6f6c206e6f7420696e697469616c697a6564000000000000000000000000910152565b6105556014602092610350565b61055e81610520565b0190565b6105789060208101906000818303910152610548565b90565b1561058257565b61058a610043565b62461bcd60e51b8152806105a060048201610562565b0390fd5b634e487b7160e01b600052601160045260246000fd5b6105c96105cf9193929361017e565b9261017e565b916105db83820261017e565b9281840414901517156105ea57565b6105a4565b90565b61060661060161060b926105ef565b610237565b61017e565b90565b61061d6106239193929361017e565b9261017e565b820180921161062e57565b6105a4565b634e487b7160e01b600052601260045260246000fd5b61065561065b9161017e565b9161017e565b908115610666570490565b610633565b61067a6106809193929361017e565b9261017e565b820391821161068b57565b6105a4565b60206106d89161069e6102cb565b506106b16106ac60006102ea565b610262565b6106cd636028e6676106c1610043565b958694859384936102f7565b8352600483016103af565b03915afa90811561084e5761072961072461073f93604093600091610820575b5061071f8161071861071261070d60006103f8565b6102fd565b916102fd565b141561045f565b610494565b6104a0565b630902f1ac90610737610043565b9384926102f7565b8252818061074f600482016104fe565b03915afa90811561081b576107c5916000809290916107e8575b506107bf90918261078361077d6000610504565b9161017e565b11806107c8575b6107939061057b565b6107b96107a18483906105ba565b916107b3670de0b6b3a76400006105f2565b9061060e565b90610649565b9061066b565b90565b50610793816107e06107da6000610504565b9161017e565b11905061078a565b6107bf925061080e915060403d8111610814575b6108068183610082565b8101906104d0565b91610769565b503d6107fc565b6103c8565b610841915060203d8111610847575b6108398183610082565b81019061032d565b386106f8565b503d61082f565b6103c856fea2646970667358221220d531201d4fef435855af4066ff043c468a22573137d0dc0c68b7d8476e87b92464736f6c634300081c0033\r\n";

    private static String librariesLinkedBinary;

    public static final String FUNC_EXCHANGE = "exchange";

    public static final String FUNC_GETTOKENPRICE = "getTokenPrice";

    @Deprecated
    protected ExchangeView(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ExchangeView(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ExchangeView(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ExchangeView(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> exchange() {
        final Function function = new Function(FUNC_EXCHANGE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getTokenPrice(String token_name) {
        final Function function = new Function(FUNC_GETTOKENPRICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static ExchangeView load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new ExchangeView(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ExchangeView load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ExchangeView(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ExchangeView load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new ExchangeView(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ExchangeView load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ExchangeView(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<ExchangeView> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider, String _exchange) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _exchange)));
        return deployRemoteCall(ExchangeView.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    public static RemoteCall<ExchangeView> deploy(Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider,
            String _exchange) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _exchange)));
        return deployRemoteCall(ExchangeView.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<ExchangeView> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit, String _exchange) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _exchange)));
        return deployRemoteCall(ExchangeView.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<ExchangeView> deploy(Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit,
            String _exchange) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _exchange)));
        return deployRemoteCall(ExchangeView.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
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