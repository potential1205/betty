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
import org.web3j.abi.datatypes.generated.Uint8;
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
public class Token extends Contract {
    public static final String BINARY = "癤�0x608060405234801561001057600080fd5b50604051611b91380380611b9183398181016040528101906100329190610512565b818281600390816100439190610785565b5080600490816100539190610785565b50505081600590816100659190610785565b5081600690816100759190610785565b506100aa336100886100b160201b60201c565b600a61009491906109c6565b8361009f9190610a11565b6100ba60201b60201c565b5050610b44565b60006012905090565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff160361012c5760006040517fec442f050000000000000000000000000000000000000000000000000000000081526004016101239190610a94565b60405180910390fd5b61013e6000838361014260201b60201c565b5050565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16036101945780600260008282546101889190610aaf565b92505081905550610267565b60008060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905081811015610220578381836040517fe450d38c00000000000000000000000000000000000000000000000000000000815260040161021793929190610af2565b60405180910390fd5b8181036000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550505b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16036102b057806002600082825403925050819055506102fd565b806000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055505b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8360405161035a9190610b29565b60405180910390a3505050565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6103ce82610385565b810181811067ffffffffffffffff821117156103ed576103ec610396565b5b80604052505050565b6000610400610367565b905061040c82826103c5565b919050565b600067ffffffffffffffff82111561042c5761042b610396565b5b61043582610385565b9050602081019050919050565b60005b83811015610460578082015181840152602081019050610445565b60008484015250505050565b600061047f61047a84610411565b6103f6565b90508281526020810184848401111561049b5761049a610380565b5b6104a6848285610442565b509392505050565b600082601f8301126104c3576104c261037b565b5b81516104d384826020860161046c565b91505092915050565b6000819050919050565b6104ef816104dc565b81146104fa57600080fd5b50565b60008151905061050c816104e6565b92915050565b6000806040838503121561052957610528610371565b5b600083015167ffffffffffffffff81111561054757610546610376565b5b610553858286016104ae565b9250506020610564858286016104fd565b9150509250929050565b600081519050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b600060028204905060018216806105c057607f821691505b6020821081036105d3576105d2610579565b5b50919050565b60008190508160005260206000209050919050565b60006020601f8301049050919050565b600082821b905092915050565b60006008830261063b7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff826105fe565b61064586836105fe565b95508019841693508086168417925050509392505050565b6000819050919050565b600061068261067d610678846104dc565b61065d565b6104dc565b9050919050565b6000819050919050565b61069c83610667565b6106b06106a882610689565b84845461060b565b825550505050565b600090565b6106c56106b8565b6106d0818484610693565b505050565b5b818110156106f4576106e96000826106bd565b6001810190506106d6565b5050565b601f8211156107395761070a816105d9565b610713846105ee565b81016020851015610722578190505b61073661072e856105ee565b8301826106d5565b50505b505050565b600082821c905092915050565b600061075c6000198460080261073e565b1980831691505092915050565b6000610775838361074b565b9150826002028217905092915050565b61078e8261056e565b67ffffffffffffffff8111156107a7576107a6610396565b5b6107b182546105a8565b6107bc8282856106f8565b600060209050601f8311600181146107ef57600084156107dd578287015190505b6107e78582610769565b86555061084f565b601f1984166107fd866105d9565b60005b8281101561082557848901518255600182019150602085019450602081019050610800565b86831015610842578489015161083e601f89168261074b565b8355505b6001600288020188555050505b505050505050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b60008160011c9050919050565b6000808291508390505b60018511156108dd578086048111156108b9576108b8610857565b5b60018516156108c85780820291505b80810290506108d685610886565b945061089d565b94509492505050565b6000826108f657600190506109b2565b8161090457600090506109b2565b816001811461091a576002811461092457610953565b60019150506109b2565b60ff84111561093657610935610857565b5b8360020a91508482111561094d5761094c610857565b5b506109b2565b5060208310610133831016604e8410600b84101617156109885782820a90508381111561098357610982610857565b5b6109b2565b6109958484846001610893565b925090508184048111156109ac576109ab610857565b5b81810290505b9392505050565b600060ff82169050919050565b60006109d1826104dc565b91506109dc836109b9565b9250610a097fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff84846108e6565b905092915050565b6000610a1c826104dc565b9150610a27836104dc565b9250828202610a35816104dc565b91508282048414831517610a4c57610a4b610857565b5b5092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000610a7e82610a53565b9050919050565b610a8e81610a73565b82525050565b6000602082019050610aa96000830184610a85565b92915050565b6000610aba826104dc565b9150610ac5836104dc565b9250828201905080821115610add57610adc610857565b5b92915050565b610aec816104dc565b82525050565b6000606082019050610b076000830186610a85565b610b146020830185610ae3565b610b216040830184610ae3565b949350505050565b6000602082019050610b3e6000830184610ae3565b92915050565b61103e80610b536000396000f3fe608060405234801561001057600080fd5b50600436106100b45760003560e01c806342966c681161007157806342966c681461018f57806370a08231146101ab57806379cc6790146101db57806395d89b41146101f7578063a9059cbb14610215578063dd62ed3e14610245576100b4565b806306fdde03146100b9578063095ea7b3146100d757806318160ddd1461010757806323b872dd14610125578063313ce5671461015557806340c10f1914610173575b600080fd5b6100c1610275565b6040516100ce9190610c65565b60405180910390f35b6100f160048036038101906100ec9190610d20565b610307565b6040516100fe9190610d7b565b60405180910390f35b61010f61032a565b60405161011c9190610da5565b60405180910390f35b61013f600480360381019061013a9190610dc0565b610334565b60405161014c9190610d7b565b60405180910390f35b61015d610363565b60405161016a9190610e2f565b60405180910390f35b61018d60048036038101906101889190610d20565b61036c565b005b6101a960048036038101906101a49190610e4a565b61037a565b005b6101c560048036038101906101c09190610e77565b61038e565b6040516101d29190610da5565b60405180910390f35b6101f560048036038101906101f09190610d20565b6103d6565b005b6101ff6103f6565b60405161020c9190610c65565b60405180910390f35b61022f600480360381019061022a9190610d20565b610488565b60405161023c9190610d7b565b60405180910390f35b61025f600480360381019061025a9190610ea4565b6104ab565b60405161026c9190610da5565b60405180910390f35b60606005805461028490610f13565b80601f01602080910402602001604051908101604052809291908181526020018280546102b090610f13565b80156102fd5780601f106102d2576101008083540402835291602001916102fd565b820191906000526020600020905b8154815290600101906020018083116102e057829003601f168201915b5050505050905090565b600080610312610532565b905061031f81858561053a565b600191505092915050565b6000600254905090565b60008061033f610532565b905061034c85828561054c565b6103578585856105e1565b60019150509392505050565b60006012905090565b61037682826106d5565b5050565b61038b610385610532565b82610757565b50565b60008060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b6103e8826103e2610532565b8361054c565b6103f28282610757565b5050565b60606006805461040590610f13565b80601f016020809104026020016040519081016040528092919081815260200182805461043190610f13565b801561047e5780601f106104535761010080835404028352916020019161047e565b820191906000526020600020905b81548152906001019060200180831161046157829003601f168201915b5050505050905090565b600080610493610532565b90506104a08185856105e1565b600191505092915050565b6000600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b600033905090565b61054783838360016107d9565b505050565b600061055884846104ab565b90507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8110156105db57818110156105cb578281836040517ffb8f41b20000000000000000000000000000000000000000000000000000000081526004016105c293929190610f53565b60405180910390fd5b6105da848484840360006107d9565b5b50505050565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16036106535760006040517f96c6fd1e00000000000000000000000000000000000000000000000000000000815260040161064a9190610f8a565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16036106c55760006040517fec442f050000000000000000000000000000000000000000000000000000000081526004016106bc9190610f8a565b60405180910390fd5b6106d08383836109b0565b505050565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16036107475760006040517fec442f0500000000000000000000000000000000000000000000000000000000815260040161073e9190610f8a565b60405180910390fd5b610753600083836109b0565b5050565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16036107c95760006040517f96c6fd1e0000000000000000000000000000000000000000000000000000000081526004016107c09190610f8a565b60405180910390fd5b6107d5826000836109b0565b5050565b600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff160361084b5760006040517fe602df050000000000000000000000000000000000000000000000000000000081526004016108429190610f8a565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16036108bd5760006040517f94280d620000000000000000000000000000000000000000000000000000000081526004016108b49190610f8a565b60405180910390fd5b81600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555080156109aa578273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040516109a19190610da5565b60405180910390a35b50505050565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1603610a025780600260008282546109f69190610fd4565b92505081905550610ad5565b60008060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905081811015610a8e578381836040517fe450d38c000000000000000000000000000000000000000000000000000000008152600401610a8593929190610f53565b60405180910390fd5b8181036000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550505b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610b1e5780600260008282540392505081905550610b6b565b806000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055505b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef83604051610bc89190610da5565b60405180910390a3505050565b600081519050919050565b600082825260208201905092915050565b60005b83811015610c0f578082015181840152602081019050610bf4565b60008484015250505050565b6000601f19601f8301169050919050565b6000610c3782610bd5565b610c418185610be0565b9350610c51818560208601610bf1565b610c5a81610c1b565b840191505092915050565b60006020820190508181036000830152610c7f8184610c2c565b905092915050565b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000610cb782610c8c565b9050919050565b610cc781610cac565b8114610cd257600080fd5b50565b600081359050610ce481610cbe565b92915050565b6000819050919050565b610cfd81610cea565b8114610d0857600080fd5b50565b600081359050610d1a81610cf4565b92915050565b60008060408385031215610d3757610d36610c87565b5b6000610d4585828601610cd5565b9250506020610d5685828601610d0b565b9150509250929050565b60008115159050919050565b610d7581610d60565b82525050565b6000602082019050610d906000830184610d6c565b92915050565b610d9f81610cea565b82525050565b6000602082019050610dba6000830184610d96565b92915050565b600080600060608486031215610dd957610dd8610c87565b5b6000610de786828701610cd5565b9350506020610df886828701610cd5565b9250506040610e0986828701610d0b565b9150509250925092565b600060ff82169050919050565b610e2981610e13565b82525050565b6000602082019050610e446000830184610e20565b92915050565b600060208284031215610e6057610e5f610c87565b5b6000610e6e84828501610d0b565b91505092915050565b600060208284031215610e8d57610e8c610c87565b5b6000610e9b84828501610cd5565b91505092915050565b60008060408385031215610ebb57610eba610c87565b5b6000610ec985828601610cd5565b9250506020610eda85828601610cd5565b9150509250929050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b60006002820490506001821680610f2b57607f821691505b602082108103610f3e57610f3d610ee4565b5b50919050565b610f4d81610cac565b82525050565b6000606082019050610f686000830186610f44565b610f756020830185610d96565b610f826040830184610d96565b949350505050565b6000602082019050610f9f6000830184610f44565b92915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000610fdf82610cea565b9150610fea83610cea565b925082820190508082111561100257611001610fa5565b5b9291505056fea2646970667358221220959b2fe6b60011ed75e81415511d874de2dc68fc75260af402d350a0d2ac4cb264736f6c634300081c0033\r\n";

    private static String librariesLinkedBinary;

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_BURNFROM = "burnFrom";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Token(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Token(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Token(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Token(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> allowance(String owner, String spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.Address(160, spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String spender, BigInteger value) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String account) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> burn(BigInteger value) {
        final Function function = new Function(
                FUNC_BURN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> burnFrom(String account, BigInteger value) {
        final Function function = new Function(
                FUNC_BURNFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, account), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> mint(String to, BigInteger amount) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String to, BigInteger value) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String from, String to,
            BigInteger value) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static List<ApprovalEventResponse> getApprovalEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ApprovalEventResponse getApprovalEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(APPROVAL_EVENT, log);
        ApprovalEventResponse typedResponse = new ApprovalEventResponse();
        typedResponse.log = log;
        typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getApprovalEventFromLog(log));
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public static List<TransferEventResponse> getTransferEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TransferEventResponse getTransferEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRANSFER_EVENT, log);
        TransferEventResponse typedResponse = new TransferEventResponse();
        typedResponse.log = log;
        typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTransferEventFromLog(log));
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    @Deprecated
    public static Token load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new Token(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Token load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Token(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Token load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new Token(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Token load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Token(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Token> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider, String token_name, BigInteger initialSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.generated.Uint256(initialSupply)));
        return deployRemoteCall(Token.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    public static RemoteCall<Token> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider, String token_name, BigInteger initialSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.generated.Uint256(initialSupply)));
        return deployRemoteCall(Token.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Token> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit, String token_name, BigInteger initialSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.generated.Uint256(initialSupply)));
        return deployRemoteCall(Token.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Token> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit, String token_name, BigInteger initialSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.generated.Uint256(initialSupply)));
        return deployRemoteCall(Token.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }



    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }
}
