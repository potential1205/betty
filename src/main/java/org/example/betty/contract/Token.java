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
    public static final String BINARY = "癤�0x6080604052346100305761001a6100146101ba565b906104a7565b610022610035565b61122c610a07823961122c90f35b61003b565b60405190565b600080fd5b601f801991011690565b634e487b7160e01b600052604160045260246000fd5b9061006a90610040565b810190811060018060401b0382111761008257604052565b61004a565b9061009a610093610035565b9283610060565b565b600080fd5b600080fd5b600080fd5b600080fd5b60018060401b0381116100cc576100c8602091610040565b0190565b61004a565b60005b8381106100e5575050906000910152565b8060209183015181850152016100d4565b9092919261010b610106826100b0565b610087565b9381855260208501908284011161012757610125926100d1565b565b6100ab565b9080601f8301121561014a57816020610147935191016100f6565b90565b6100a6565b90565b61015b8161014f565b0361016257565b600080fd5b9050519061017482610152565b565b91906040838203126101b55760008301519060018060401b0382116101b0576101a4816101ad93860161012c565b93602001610167565b90565b6100a1565b61009c565b6101d8611c33803803806101cd81610087565b928339810190610176565b9091565b5190565b634e487b7160e01b600052602260045260246000fd5b9060016002830492168015610216575b602083101461021157565b6101e0565b91607f1691610206565b600052602060002090565b601f602091010490565b1b90565b9190600861025591029161024f60001984610235565b92610235565b9181191691161790565b90565b61027661027161027b9261014f565b61025f565b61014f565b90565b90565b919061029761029261029f93610262565b61027e565b908354610239565b9055565b600090565b6102ba916102b46102a3565b91610281565b565b5b8181106102c8575050565b806102d660006001936102a8565b016102bd565b9190601f81116102ec575b505050565b6102f861031d93610220565b9060206103048461022b565b83019310610325575b6103169061022b565b01906102bc565b3880806102e7565b91506103168192905061030d565b1c90565b906103489060001990600802610333565b191690565b8161035791610337565b906002021790565b90610369816101dc565b9060018060401b0382116104295761038b8261038585546101f6565b856102dc565b602090601f83116001146103c0579180916103af936000926103b4575b505061034d565b90555b565b909150015138806103a8565b601f198316916103cf85610220565b9260005b818110610411575091600293918560019694106103f7575b505050020190556103b2565b610407910151601f841690610337565b90553880806103eb565b919360206001819287870151815501950192016103d3565b61004a565b906104389161035f565b565b60ff1690565b634e487b7160e01b600052601160045260246000fd5b61045f9061043a565b604d811161046d57600a0a90565b610440565b6104816104879193929361014f565b9261014f565b9161049383820261014f565b9281840414901517156104a257565b610440565b906104cd6104ee926104bb33828391610555565b6104c681600661042e565b600761042e565b6104e833916104e26104dd6105fc565b610456565b90610472565b90610612565b565b90565b60018060a01b031690565b61051261050d610517926104f0565b61025f565b6104f3565b90565b610523906104fe565b90565b61052f906104f3565b90565b61053b90610526565b9052565b919061055390600060208501940190610532565b565b9161055f916105b4565b8061057b610575610570600061051a565b610526565b91610526565b1461058b5761058990610724565b565b6105b0610598600061051a565b6000918291631e4fbdf760e01b83526004830161053f565b0390fd5b906105be916105c0565b565b906105cf6105d692600361042e565b600461042e565b565b600090565b90565b6105f46105ef6105f9926105dd565b61025f565b61043a565b90565b6106046105d8565b5061060f60126105e0565b90565b8061062e610628610623600061051a565b610526565b91610526565b1461064b5761064991610641600061051a565b91909161088f565b565b610670610658600061051a565b600091829163ec442f0560e01b83526004830161053f565b0390fd5b60001c90565b60018060a01b031690565b61069161069691610674565b61067a565b90565b6106a39054610685565b90565b60001b90565b906106bd60018060a01b03916106a6565b9181191691161790565b6106db6106d66106e0926104f3565b61025f565b6104f3565b90565b6106ec906106c7565b90565b6106f8906106e3565b90565b90565b9061071361070e61071a926106ef565b6106fb565b82546106ac565b9055565b60000190565b61072e6005610699565b6107398260056106fe565b9061076d6107677f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0936106ef565b916106ef565b91610776610035565b806107808161071e565b0390a3565b9061078f906106ef565b600052602052604060002090565b90565b6107ac6107b191610674565b61079d565b90565b6107be90546107a0565b90565b6107ca9061014f565b9052565b6040906107f86107ff94969593966107ee60608401986000850190610532565b60208301906107c1565b01906107c1565b565b9061080c910361014f565b90565b9061081c600019916106a6565b9181191691161790565b9061083b61083661084292610262565b61027e565b825461080f565b9055565b61085561085b9193929361014f565b9261014f565b820180921161086657565b610440565b90610876910161014f565b90565b919061088d906000602085019401906107c1565b565b919091806108ae6108a86108a3600061051a565b610526565b91610526565b14600014610993576108d36108cc836108c760026107b4565b610846565b6002610826565b5b826108f06108ea6108e5600061051a565b610526565b91610526565b146000146109665761091561090e8361090960026107b4565b610801565b6002610826565b5b91909161096161094f6109497fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef936106ef565b936106ef565b93610958610035565b91829182610879565b0390a3565b61098e8261098861097960008790610785565b91610983836107b4565b61086b565b90610826565b610916565b6109a76109a260008390610785565b6107b4565b806109ba6109b48561014f565b9161014f565b106109e3576109cd6109de918490610801565b6109d960008490610785565b610826565b6108d4565b90610a02909192600093849363391434e360e21b8552600485016107ce565b0390fdfe60806040526004361015610013575b6106e8565b61001e60003561011d565b806306fdde0314610118578063095ea7b31461011357806318160ddd1461010e57806323b872dd14610109578063313ce5671461010457806340c10f19146100ff57806342966c68146100fa57806370a08231146100f5578063715018a6146100f057806379cc6790146100eb5780638da5cb5b146100e657806395d89b41146100e1578063a9059cbb146100dc578063da72c1e8146100d7578063dd62ed3e146100d25763f2fde38b0361000e576106b5565b61067f565b61061d565b6105e7565b6105b2565b61057d565b610526565b6104f3565b6104be565b61046c565b610419565b6103de565b61037f565b61030f565b6102b6565b6101c9565b60e01c90565b60405190565b600080fd5b600080fd5b600091031261013e57565b61012e565b5190565b60209181520190565b60005b838110610164575050906000910152565b806020918301518185015201610153565b601f801991011690565b61019e6101a76020936101ac9361019581610143565b93848093610147565b95869101610150565b610175565b0190565b6101c6916020820191600081840391015261017f565b90565b346101f9576101d9366004610133565b6101f56101e461084f565b6101ec610123565b918291826101b0565b0390f35b610129565b60018060a01b031690565b610212906101fe565b90565b61021e81610209565b0361022557565b600080fd5b9050359061023782610215565b565b90565b61024581610239565b0361024c57565b600080fd5b9050359061025e8261023c565b565b9190604083820312610289578061027d610286926000860161022a565b93602001610251565b90565b61012e565b151590565b61029c9061028e565b9052565b91906102b490600060208501940190610293565b565b346102e7576102e36102d26102cc366004610260565b9061086a565b6102da610123565b918291826102a0565b0390f35b610129565b6102f590610239565b9052565b919061030d906000602085019401906102ec565b565b3461033f5761031f366004610133565b61033b61032a6108bb565b610332610123565b918291826102f9565b0390f35b610129565b909160608284031261037a57610377610360846000850161022a565b9361036e816020860161022a565b93604001610251565b90565b61012e565b346103b0576103ac61039b610395366004610344565b916108d1565b6103a3610123565b918291826102a0565b0390f35b610129565b60ff1690565b6103c4906103b5565b9052565b91906103dc906000602085019401906103bb565b565b3461040e576103ee366004610133565b61040a6103f9610927565b610401610123565b918291826103c8565b0390f35b610129565b60000190565b346104485761043261042c366004610260565b9061093d565b61043a610123565b8061044481610413565b0390f35b610129565b906020828203126104675761046491600001610251565b90565b61012e565b3461049a5761048461047f36600461044d565b610949565b61048c610123565b8061049681610413565b0390f35b610129565b906020828203126104b9576104b69160000161022a565b90565b61012e565b346104ee576104ea6104d96104d436600461049f565b6109a8565b6104e1610123565b918291826102f9565b0390f35b610129565b3461052157610503366004610133565b61050b610a18565b610513610123565b8061051d81610413565b0390f35b610129565b346105555761053f610539366004610260565b90610a22565b610547610123565b8061055181610413565b0390f35b610129565b61056390610209565b9052565b919061057b9060006020850194019061055a565b565b346105ad5761058d366004610133565b6105a9610598610a72565b6105a0610123565b91829182610567565b0390f35b610129565b346105e2576105c2366004610133565b6105de6105cd610a88565b6105d5610123565b918291826101b0565b0390f35b610129565b34610618576106146106036105fd366004610260565b90610a9e565b61060b610123565b918291826102a0565b0390f35b610129565b3461064c57610636610630366004610344565b91610ac0565b61063e610123565b8061064881610413565b0390f35b610129565b919060408382031261067a578061066e610677926000860161022a565b9360200161022a565b90565b61012e565b346106b0576106ac61069b610695366004610651565b90610ae7565b6106a3610123565b918291826102f9565b0390f35b610129565b346106e3576106cd6106c836600461049f565b610b77565b6106d5610123565b806106df81610413565b0390f35b610129565b600080fd5b606090565b634e487b7160e01b600052602260045260246000fd5b9060016002830492168015610728575b602083101461072357565b6106f2565b91607f1691610718565b60209181520190565b600052602060002090565b906000929180549061076161075a83610708565b8094610732565b916001811690816000146107ba575060011461077d575b505050565b61078a919293945061073b565b916000925b8184106107a25750500190388080610778565b6001816020929593955484860152019101929061078f565b92949550505060ff1916825215156020020190388080610778565b906107df91610746565b90565b634e487b7160e01b600052604160045260246000fd5b9061080290610175565b810190811067ffffffffffffffff82111761081c57604052565b6107e2565b9061084161083a92610831610123565b938480926107d5565b03836107f8565b565b61084c90610821565b90565b6108576106ed565b506108626006610843565b90565b600090565b61088791610876610865565b5061087f610b82565b919091610b8f565b600190565b600090565b60001c90565b90565b6108a66108ab91610891565b610897565b90565b6108b8905461089a565b90565b6108c361088c565b506108ce60026108ae565b90565b916108fb926108de610865565b506108f36108ea610b82565b82908491610be0565b919091610c6f565b600190565b600090565b90565b90565b61091f61091a61092492610905565b610908565b6103b5565b90565b61092f610900565b5061093a601261090b565b90565b9061094791610d12565b565b61095a90610955610b82565b610d74565b565b61097061096b610975926101fe565b610908565b6101fe565b90565b6109819061095c565b90565b61098d90610978565b90565b9061099a90610984565b600052602052604060002090565b6109bf6109c4916109b761088c565b506000610990565b6108ae565b90565b6109cf610dd7565b6109d7610a04565b565b90565b6109f06109eb6109f5926109d9565b610908565b6101fe565b90565b610a01906109dc565b90565b610a16610a1160006109f8565b610e6a565b565b610a206109c7565b565b90610a3f91610a3a81610a33610b82565b8491610be0565b610d74565b565b600090565b60018060a01b031690565b610a5d610a6291610891565b610a46565b90565b610a6f9054610a51565b90565b610a7a610a41565b50610a856005610a65565b90565b610a906106ed565b50610a9b6007610843565b90565b610abb91610aaa610865565b50610ab3610b82565b919091610c6f565b600190565b91610acd92919091610c6f565b565b90610ad990610984565b600052602052604060002090565b610b0c91610b02610b0792610afa61088c565b506001610acf565b610990565b6108ae565b90565b610b2090610b1b610dd7565b610b22565b565b80610b3e610b38610b3360006109f8565b610209565b91610209565b14610b4e57610b4c90610e6a565b565b610b73610b5b60006109f8565b6000918291631e4fbdf760e01b835260048301610567565b0390fd5b610b8090610b0f565b565b610b8a610a41565b503390565b91610b9d9291600192610f21565b565b604090610bc9610bd09496959396610bbf6060840198600085019061055a565b60208301906102ec565b01906102ec565b565b90610bdd9103610239565b90565b929192610bee818390610ae7565b9081610c04610bfe600019610239565b91610239565b10610c11575b5050509050565b81610c24610c1e87610239565b91610239565b10610c4b57610c429394610c39919392610bd2565b90600092610f21565b80388080610c0a565b50610c6b849291926000938493637dc7a0d960e11b855260048501610b9f565b0390fd5b9182610c8c610c86610c8160006109f8565b610209565b91610209565b14610ce95781610cad610ca7610ca260006109f8565b610209565b91610209565b14610cc057610cbe9291909161107f565b565b610ce5610ccd60006109f8565b600091829163ec442f0560e01b835260048301610567565b0390fd5b610d0e610cf660006109f8565b6000918291634b637e8f60e11b835260048301610567565b0390fd5b80610d2e610d28610d2360006109f8565b610209565b91610209565b14610d4b57610d4991610d4160006109f8565b91909161107f565b565b610d70610d5860006109f8565b600091829163ec442f0560e01b835260048301610567565b0390fd5b9081610d91610d8b610d8660006109f8565b610209565b91610209565b14610dae57610dac9190610da560006109f8565b909161107f565b565b610dd3610dbb60006109f8565b6000918291634b637e8f60e11b835260048301610567565b0390fd5b610ddf610a72565b610df8610df2610ded610b82565b610209565b91610209565b03610dff57565b610e22610e0a610b82565b600091829163118cdaa760e01b835260048301610567565b0390fd5b60001b90565b90610e3d60018060a01b0391610e26565b9181191691161790565b90565b90610e5f610e5a610e6692610984565b610e47565b8254610e2c565b9055565b610e746005610a65565b610e7f826005610e4a565b90610eb3610ead7f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e093610984565b91610984565b91610ebc610123565b80610ec681610413565b0390a3565b90610ed860001991610e26565b9181191691161790565b610ef6610ef1610efb92610239565b610908565b610239565b90565b90565b90610f16610f11610f1d92610ee2565b610efe565b8254610ecb565b9055565b909281610f3f610f39610f3460006109f8565b610209565b91610209565b1461100d5783610f60610f5a610f5560006109f8565b610209565b91610209565b14610fe457610f8483610f7f610f7860018690610acf565b8790610990565b610f01565b610f8e575b505050565b919091610fd9610fc7610fc17f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92593610984565b93610984565b93610fd0610123565b918291826102f9565b0390a3388080610f89565b611009610ff160006109f8565b6000918291634a1406b160e11b835260048301610567565b0390fd5b61103261101a60006109f8565b600091829163e602df0560e01b835260048301610567565b0390fd5b634e487b7160e01b600052601160045260246000fd5b61105b61106191939293610239565b92610239565b820180921161106c57565b611036565b9061107c9101610239565b90565b9190918061109e61109861109360006109f8565b610209565b91610209565b14600014611183576110c36110bc836110b760026108ae565b61104c565b6002610f01565b5b826110e06110da6110d560006109f8565b610209565b91610209565b14600014611156576111056110fe836110f960026108ae565b610bd2565b6002610f01565b5b91909161115161113f6111397fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef93610984565b93610984565b93611148610123565b918291826102f9565b0390a3565b61117e8261117861116960008790610990565b91611173836108ae565b611071565b90610f01565b611106565b61119761119260008390610990565b6108ae565b806111aa6111a485610239565b91610239565b106111d3576111bd6111ce918490610bd2565b6111c960008490610990565b610f01565b6110c4565b906111f2909192600093849363391434e360e21b855260048501610b9f565b0390fdfea26469706673582212202319552708b57363fa21da15c66fce16a1675fdbf8a08588b0bf73df8b73a67c64736f6c634300081c0033\r\n";

    private static String librariesLinkedBinary;

    public static final String FUNC_ADMINTRANSFER = "adminTransfer";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_BURNFROM = "burnFrom";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
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

    public RemoteFunctionCall<TransactionReceipt> adminTransfer(String from, String to,
            BigInteger amount) {
        final Function function = new Function(
                FUNC_ADMINTRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
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

    public static List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnershipTransferredEventResponse getOwnershipTransferredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
        OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
        typedResponse.log = log;
        typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnershipTransferredEventFromLog(log));
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
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

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }
}
