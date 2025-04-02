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
    public static final String BINARY = "癤�0x608060405234801561000f575f5ffd5b506040516119dc3803806119dc833981810160405281019061003191906104b6565b818281600390816100429190610717565b5080600490816100529190610717565b5050506100893361006761009060201b60201c565b600a610073919061094e565b8361007e9190610998565b61009860201b60201c565b5050610ac1565b5f6012905090565b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610108575f6040517fec442f050000000000000000000000000000000000000000000000000000000081526004016100ff9190610a18565b60405180910390fd5b6101195f838361011d60201b60201c565b5050565b5f73ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff160361016d578060025f8282546101619190610a31565b9250508190555061023b565b5f5f5f8573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f20549050818110156101f6578381836040517fe450d38c0000000000000000000000000000000000000000000000000000000081526004016101ed93929190610a73565b60405180910390fd5b8181035f5f8673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f2081905550505b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610282578060025f82825403925050819055506102cc565b805f5f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f82825401925050819055505b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040516103299190610aa8565b60405180910390a3505050565b5f604051905090565b5f5ffd5b5f5ffd5b5f5ffd5b5f5ffd5b5f601f19601f8301169050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52604160045260245ffd5b6103958261034f565b810181811067ffffffffffffffff821117156103b4576103b361035f565b5b80604052505050565b5f6103c6610336565b90506103d2828261038c565b919050565b5f67ffffffffffffffff8211156103f1576103f061035f565b5b6103fa8261034f565b9050602081019050919050565b8281835e5f83830152505050565b5f610427610422846103d7565b6103bd565b9050828152602081018484840111156104435761044261034b565b5b61044e848285610407565b509392505050565b5f82601f83011261046a57610469610347565b5b815161047a848260208601610415565b91505092915050565b5f819050919050565b61049581610483565b811461049f575f5ffd5b50565b5f815190506104b08161048c565b92915050565b5f5f604083850312156104cc576104cb61033f565b5b5f83015167ffffffffffffffff8111156104e9576104e8610343565b5b6104f585828601610456565b9250506020610506858286016104a2565b9150509250929050565b5f81519050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f600282049050600182168061055e57607f821691505b6020821081036105715761057061051a565b5b50919050565b5f819050815f5260205f209050919050565b5f6020601f8301049050919050565b5f82821b905092915050565b5f600883026105d37fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82610598565b6105dd8683610598565b95508019841693508086168417925050509392505050565b5f819050919050565b5f61061861061361060e84610483565b6105f5565b610483565b9050919050565b5f819050919050565b610631836105fe565b61064561063d8261061f565b8484546105a4565b825550505050565b5f5f905090565b61065c61064d565b610667818484610628565b505050565b5b8181101561068a5761067f5f82610654565b60018101905061066d565b5050565b601f8211156106cf576106a081610577565b6106a984610589565b810160208510156106b8578190505b6106cc6106c485610589565b83018261066c565b50505b505050565b5f82821c905092915050565b5f6106ef5f19846008026106d4565b1980831691505092915050565b5f61070783836106e0565b9150826002028217905092915050565b61072082610510565b67ffffffffffffffff8111156107395761073861035f565b5b6107438254610547565b61074e82828561068e565b5f60209050601f83116001811461077f575f841561076d578287015190505b61077785826106fc565b8655506107de565b601f19841661078d86610577565b5f5b828110156107b45784890151825560018201915060208501945060208101905061078f565b868310156107d157848901516107cd601f8916826106e0565b8355505b6001600288020188555050505b505050505050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52601160045260245ffd5b5f8160011c9050919050565b5f5f8291508390505b600185111561086857808604811115610844576108436107e6565b5b60018516156108535780820291505b808102905061086185610813565b9450610828565b94509492505050565b5f82610880576001905061093b565b8161088d575f905061093b565b81600181146108a357600281146108ad576108dc565b600191505061093b565b60ff8411156108bf576108be6107e6565b5b8360020a9150848211156108d6576108d56107e6565b5b5061093b565b5060208310610133831016604e8410600b84101617156109115782820a90508381111561090c5761090b6107e6565b5b61093b565b61091e848484600161081f565b92509050818404811115610935576109346107e6565b5b81810290505b9392505050565b5f60ff82169050919050565b5f61095882610483565b915061096383610942565b92506109907fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8484610871565b905092915050565b5f6109a282610483565b91506109ad83610483565b92508282026109bb81610483565b915082820484148315176109d2576109d16107e6565b5b5092915050565b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f610a02826109d9565b9050919050565b610a12816109f8565b82525050565b5f602082019050610a2b5f830184610a09565b92915050565b5f610a3b82610483565b9150610a4683610483565b9250828201905080821115610a5e57610a5d6107e6565b5b92915050565b610a6d81610483565b82525050565b5f606082019050610a865f830186610a09565b610a936020830185610a64565b610aa06040830184610a64565b949350505050565b5f602082019050610abb5f830184610a64565b92915050565b610f0e80610ace5f395ff3fe608060405234801561000f575f5ffd5b50600436106100a7575f3560e01c806342966c681161006f57806342966c681461016557806370a082311461018157806379cc6790146101b157806395d89b41146101cd578063a9059cbb146101eb578063dd62ed3e1461021b576100a7565b806306fdde03146100ab578063095ea7b3146100c957806318160ddd146100f957806323b872dd14610117578063313ce56714610147575b5f5ffd5b6100b361024b565b6040516100c09190610b5c565b60405180910390f35b6100e360048036038101906100de9190610c0d565b6102db565b6040516100f09190610c65565b60405180910390f35b6101016102fd565b60405161010e9190610c8d565b60405180910390f35b610131600480360381019061012c9190610ca6565b610306565b60405161013e9190610c65565b60405180910390f35b61014f610334565b60405161015c9190610d11565b60405180910390f35b61017f600480360381019061017a9190610d2a565b61033c565b005b61019b60048036038101906101969190610d55565b610350565b6040516101a89190610c8d565b60405180910390f35b6101cb60048036038101906101c69190610c0d565b610395565b005b6101d56103b5565b6040516101e29190610b5c565b60405180910390f35b61020560048036038101906102009190610c0d565b610445565b6040516102129190610c65565b60405180910390f35b61023560048036038101906102309190610d80565b610467565b6040516102429190610c8d565b60405180910390f35b60606003805461025a90610deb565b80601f016020809104026020016040519081016040528092919081815260200182805461028690610deb565b80156102d15780601f106102a8576101008083540402835291602001916102d1565b820191905f5260205f20905b8154815290600101906020018083116102b457829003601f168201915b5050505050905090565b5f5f6102e56104e9565b90506102f28185856104f0565b600191505092915050565b5f600254905090565b5f5f6103106104e9565b905061031d858285610502565b610328858585610595565b60019150509392505050565b5f6012905090565b61034d6103476104e9565b82610685565b50565b5f5f5f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f20549050919050565b6103a7826103a16104e9565b83610502565b6103b18282610685565b5050565b6060600480546103c490610deb565b80601f01602080910402602001604051908101604052809291908181526020018280546103f090610deb565b801561043b5780601f106104125761010080835404028352916020019161043b565b820191905f5260205f20905b81548152906001019060200180831161041e57829003601f168201915b5050505050905090565b5f5f61044f6104e9565b905061045c818585610595565b600191505092915050565b5f60015f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f2054905092915050565b5f33905090565b6104fd8383836001610704565b505050565b5f61050d8484610467565b90507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff81101561058f5781811015610580578281836040517ffb8f41b200000000000000000000000000000000000000000000000000000000815260040161057793929190610e2a565b60405180910390fd5b61058e84848484035f610704565b5b50505050565b5f73ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1603610605575f6040517f96c6fd1e0000000000000000000000000000000000000000000000000000000081526004016105fc9190610e5f565b60405180910390fd5b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610675575f6040517fec442f0500000000000000000000000000000000000000000000000000000000815260040161066c9190610e5f565b60405180910390fd5b6106808383836108d3565b505050565b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16036106f5575f6040517f96c6fd1e0000000000000000000000000000000000000000000000000000000081526004016106ec9190610e5f565b60405180910390fd5b610700825f836108d3565b5050565b5f73ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff1603610774575f6040517fe602df0500000000000000000000000000000000000000000000000000000000815260040161076b9190610e5f565b60405180910390fd5b5f73ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16036107e4575f6040517f94280d620000000000000000000000000000000000000000000000000000000081526004016107db9190610e5f565b60405180910390fd5b8160015f8673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f208190555080156108cd578273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040516108c49190610c8d565b60405180910390a35b50505050565b5f73ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1603610923578060025f8282546109179190610ea5565b925050819055506109f1565b5f5f5f8573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f20549050818110156109ac578381836040517fe450d38c0000000000000000000000000000000000000000000000000000000081526004016109a393929190610e2a565b60405180910390fd5b8181035f5f8673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f2081905550505b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610a38578060025f8282540392505081905550610a82565b805f5f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f82825401925050819055505b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef83604051610adf9190610c8d565b60405180910390a3505050565b5f81519050919050565b5f82825260208201905092915050565b8281835e5f83830152505050565b5f601f19601f8301169050919050565b5f610b2e82610aec565b610b388185610af6565b9350610b48818560208601610b06565b610b5181610b14565b840191505092915050565b5f6020820190508181035f830152610b748184610b24565b905092915050565b5f5ffd5b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f610ba982610b80565b9050919050565b610bb981610b9f565b8114610bc3575f5ffd5b50565b5f81359050610bd481610bb0565b92915050565b5f819050919050565b610bec81610bda565b8114610bf6575f5ffd5b50565b5f81359050610c0781610be3565b92915050565b5f5f60408385031215610c2357610c22610b7c565b5b5f610c3085828601610bc6565b9250506020610c4185828601610bf9565b9150509250929050565b5f8115159050919050565b610c5f81610c4b565b82525050565b5f602082019050610c785f830184610c56565b92915050565b610c8781610bda565b82525050565b5f602082019050610ca05f830184610c7e565b92915050565b5f5f5f60608486031215610cbd57610cbc610b7c565b5b5f610cca86828701610bc6565b9350506020610cdb86828701610bc6565b9250506040610cec86828701610bf9565b9150509250925092565b5f60ff82169050919050565b610d0b81610cf6565b82525050565b5f602082019050610d245f830184610d02565b92915050565b5f60208284031215610d3f57610d3e610b7c565b5b5f610d4c84828501610bf9565b91505092915050565b5f60208284031215610d6a57610d69610b7c565b5b5f610d7784828501610bc6565b91505092915050565b5f5f60408385031215610d9657610d95610b7c565b5b5f610da385828601610bc6565b9250506020610db485828601610bc6565b9150509250929050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f6002820490506001821680610e0257607f821691505b602082108103610e1557610e14610dbe565b5b50919050565b610e2481610b9f565b82525050565b5f606082019050610e3d5f830186610e1b565b610e4a6020830185610c7e565b610e576040830184610c7e565b949350505050565b5f602082019050610e725f830184610e1b565b92915050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52601160045260245ffd5b5f610eaf82610bda565b9150610eba83610bda565b9250828201905080821115610ed257610ed1610e78565b5b9291505056fea264697066735822122075ce628232f411202c80616960e8408183aa592bd7a4fc7b40e188b04ddad9f464736f6c634300081c0033\r\n";

    private static String librariesLinkedBinary;

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_BURNFROM = "burnFrom";

    public static final String FUNC_DECIMALS = "decimals";

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
        return BINARY;
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
