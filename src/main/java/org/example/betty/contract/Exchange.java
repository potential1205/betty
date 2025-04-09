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
public class Exchange extends Contract {
    public static final String BINARY = "癤�0x60806040523461002f576100196100146100fa565b6101a2565b610021610034565b6122b96101b882396122b990f35b61003a565b60405190565b600080fd5b601f801991011690565b634e487b7160e01b600052604160045260246000fd5b906100699061003f565b810190811060018060401b0382111761008157604052565b610049565b90610099610092610034565b928361005f565b565b600080fd5b60018060a01b031690565b6100b4906100a0565b90565b6100c0816100ab565b036100c757565b600080fd5b905051906100d9826100b7565b565b906020828203126100f5576100f2916000016100cc565b90565b61009b565b6101186124718038038061010d81610086565b9283398101906100db565b90565b90565b61013261012d610137926100a0565b61011b565b6100a0565b90565b6101439061011e565b90565b61014f9061013a565b90565b60001b90565b9061016960018060a01b0391610152565b9181191691161790565b61017c9061013a565b90565b90565b9061019761019261019e92610173565b61017f565b8254610158565b9055565b6101ae6101b591610146565b6000610182565b56fe60806040526004361015610013575b610b58565b61001e60003561014d565b80630774c366146101485780630ca5c504146101435780631003e2d21461013e57806322cb8f1714610139578063248f8864146101345780633872fd311461012f57806344b760391461012a5780634cc8221514610125578063524f3889146101205780636028e6671461011b57806362efb0c71461011657806378691f16146101115780639f6563211461010c578063a451f60214610107578063b116479414610102578063c2ad4984146100fd578063e90ca51a146100f8578063f08463fb146100f35763f93a47af0361000e57610b1f565b610a74565b610a40565b6109b9565b610956565b6108fa565b610858565b610823565b61077f565b61074a565b6105c0565b610557565b610521565b6104ed565b61042c565b6103f8565b6103c5565b61036a565b6101d6565b60e01c90565b60405190565b600080fd5b600080fd5b600091031261016e57565b61015e565b1c90565b90565b61018a90600861018f9302610173565b610177565b90565b9061019d915461017a565b90565b6101ad6003600090610192565b90565b90565b6101bc906101b0565b9052565b91906101d4906000602085019401906101b3565b565b34610206576101e6366004610163565b6102026101f16101a0565b6101f9610153565b918291826101c0565b0390f35b610159565b600080fd5b600080fd5b600080fd5b601f801991011690565b634e487b7160e01b600052604160045260246000fd5b906102449061021a565b810190811067ffffffffffffffff82111761025e57604052565b610224565b9061027661026f610153565b928361023a565b565b67ffffffffffffffff81116102965761029260209161021a565b0190565b610224565b90826000939282370152565b909291926102bc6102b782610278565b610263565b938185526020850190828401116102d8576102d69261029b565b565b610215565b9080601f830112156102fb578160206102f8933591016102a7565b90565b610210565b610309816101b0565b0361031057565b600080fd5b9050359061032282610300565b565b91906040838203126103655760008301359067ffffffffffffffff8211610360576103548161035d9386016102dd565b93602001610315565b90565b61020b565b61015e565b3461039b57610397610386610380366004610324565b90610d1c565b61038e610153565b918291826101c0565b0390f35b610159565b906020828203126103ba576103b791600001610315565b90565b61015e565b60000190565b346103f3576103dd6103d83660046103a0565b610f5d565b6103e5610153565b806103ef816103bf565b0390f35b610159565b346104275761041161040b366004610324565b90611066565b610419610153565b80610423816103bf565b0390f35b610159565b3461045a5761044461043f3660046103a0565b6111ae565b61044c610153565b80610456816103bf565b0390f35b610159565b60018060a01b031690565b6104739061045f565b90565b61047f8161046a565b0361048657565b600080fd5b9050359061049882610476565b565b90916060828403126104e857600082013567ffffffffffffffff81116104e3576104c9846104e09285016102dd565b936104d7816020860161048b565b93604001610315565b90565b61020b565b61015e565b3461051c5761050661050036600461049a565b916112cf565b61050e610153565b80610518816103bf565b0390f35b610159565b346105525761054e61053d61053736600461049a565b91611400565b610545610153565b918291826101c0565b0390f35b610159565b346105855761056f61056a3660046103a0565b61158c565b610577610153565b80610581816103bf565b0390f35b610159565b906020828203126105bb57600082013567ffffffffffffffff81116105b6576105b392016102dd565b90565b61020b565b61015e565b346105f0576105ec6105db6105d636600461058a565b611779565b6105e3610153565b918291826101c0565b0390f35b610159565b5190565b905090565b60005b838110610612575050906000910152565b806020918301518185015201610601565b61064861063f92602092610636816105f5565b948580936105f9565b938491016105fe565b0190565b90565b61065b610660916101b0565b61064c565b9052565b61067461067b9160209493610623565b809261064f565b0190565b61069361068a610153565b92839283610664565b03902090565b6106a29161067f565b90565b60018060a01b031690565b6106c09060086106c59302610173565b6106a5565b90565b906106d391546106b0565b90565b6106ed906106e8600291600092610699565b6106c8565b90565b90565b61070761070261070c9261045f565b6106f0565b61045f565b90565b610718906106f3565b90565b6107249061070f565b90565b6107309061071b565b9052565b919061074890600060208501940190610727565b565b3461077a5761077661076561076036600461058a565b6106d6565b61076d610153565b91829182610734565b0390f35b610159565b346107b0576107ac61079b61079536600461049a565b91611883565b6107a3610153565b918291826101c0565b0390f35b610159565b60018060a01b031690565b6107d09060086107d59302610173565b6107b5565b90565b906107e391546107c0565b90565b6107f16000806107d8565b90565b6107fd9061070f565b90565b610809906107f4565b9052565b919061082190600060208501940190610800565b565b3461085357610833366004610163565b61084f61083e6107e6565b610846610153565b9182918261080d565b0390f35b610159565b346108895761088561087461086e366004610324565b90611a19565b61087c610153565b918291826101c0565b0390f35b610159565b90916060828403126108f557600082013567ffffffffffffffff81116108f057836108ba9184016102dd565b9260208301359067ffffffffffffffff82116108eb576108df816108e89386016102dd565b93604001610315565b90565b61020b565b61020b565b61015e565b3461092b5761092761091661091036600461088e565b91611be7565b61091e610153565b918291826101c0565b0390f35b610159565b6109399161067f565b90565b6109539061094e600191600092610930565b6107d8565b90565b346109865761098261097161096c36600461058a565b61093c565b610979610153565b9182918261080d565b0390f35b610159565b91906040838203126109b457806109a86109b1926000860161048b565b93602001610315565b90565b61015e565b346109e8576109d26109cc36600461098b565b90611e0e565b6109da610153565b806109e4816103bf565b0390f35b610159565b9091606082840312610a3b57600082013567ffffffffffffffff8111610a3657610a1c84610a339285016102dd565b93610a2a816020860161048b565b9360400161048b565b90565b61020b565b61015e565b34610a6f57610a59610a533660046109ed565b91611f53565b610a61610153565b80610a6b816103bf565b0390f35b610159565b34610aa357610a8d610a8736600461098b565b90611f8f565b610a95610153565b80610a9f816103bf565b0390f35b610159565b608081830312610b1a57600081013567ffffffffffffffff8111610b155782610ad29183016102dd565b92602082013567ffffffffffffffff8111610b1057610af684610b0d9285016102dd565b93610b04816040860161048b565b93606001610315565b90565b61020b565b61020b565b61015e565b34610b5357610b4f610b3e610b35366004610aa8565b9291909161204c565b610b46610153565b918291826101c0565b0390f35b610159565b600080fd5b600090565b60001c90565b610b74610b7991610b62565b6106a5565b90565b610b869054610b68565b90565b610b95610b9a91610b62565b6107b5565b90565b610ba79054610b89565b90565b600080fd5b60e01b90565b151590565b610bc381610bb5565b03610bca57565b600080fd5b90505190610bdc82610bba565b565b90602082820312610bf857610bf591600001610bcf565b90565b61015e565b610c069061046a565b9052565b604090610c34610c3b9496959396610c2a60608401986000850190610bfd565b6020830190610bfd565b01906101b3565b565b610c45610153565b3d6000823e3d90fd5b90505190610c5b82610300565b565b90602082820312610c7757610c7491600001610c4e565b90565b61015e565b916020610c9e929493610c97604082019660008301906101b3565b0190610bfd565b565b610ca99061070f565b90565b60209181520190565b610cd4610cdd602093610ce293610ccb816105f5565b93848093610cac565b958691016105fe565b61021a565b0190565b604090610d13610d08610d1a9597969460608401908482036000860152610cb5565b9660208301906101b3565b01906101b3565b565b90610d25610b5d565b50610d3a610d3560028490610699565b610b7c565b610d4c610d476000610b9d565b6107f4565b9060206323b872dd923390610d7e6000610d658661071b565b96610d8989610d72610153565b998a9788968795610baf565b855260048501610c0a565b03925af1918215610e9457610da392610e68575b5061071b565b6020632cf5dbda918390610dcb60003395610dd6610dbf610153565b97889687958694610baf565b845260048401610c7c565b03925af1908115610e6357600091610e35575b50913390918392610e2f610e1d7f2ffcd8ea21e3668481ddff339b825e8ca9278c9ee226175e1ab589019a960b6b94610ca0565b94610e26610153565b93849384610ce6565b0390a290565b610e56915060203d8111610e5c575b610e4e818361023a565b810190610c5d565b38610de9565b503d610e44565b610c3d565b610e889060203d8111610e8d575b610e80818361023a565b810190610bde565b610d9d565b503d610e76565b610c3d565b610ea29061070f565b90565b610eb1610eb691610b62565b610177565b90565b610ec39054610ea5565b90565b634e487b7160e01b600052601160045260246000fd5b610eeb610ef1919392936101b0565b926101b0565b8201809211610efc57565b610ec6565b60001b90565b90610f1460001991610f01565b9181191691161790565b610f32610f2d610f37926101b0565b6106f0565b6101b0565b90565b90565b90610f52610f4d610f5992610f1e565b610f3a565b8254610f07565b9055565b610f6f610f6a6000610b9d565b6107f4565b9060206323b872dd923390610fa16000610f8830610e99565b96610fac87610f95610153565b998a9788968795610baf565b855260048501610c0a565b03925af190811561100957610fdb92610fd492610fdd575b50610fcf6003610eb9565b610edc565b6003610f3d565b565b610ffd9060203d8111611002575b610ff5818361023a565b810190610bde565b610fc4565b503d610feb565b610c3d565b611017906106f3565b90565b6110239061100e565b90565b61102f9061070f565b90565b600091031261103d57565b61015e565b91602061106492949361105d60408201966000830190610bfd565b01906101b3565b565b61108c61108761108261107d611091946001610930565b610b9d565b6107f4565b61101a565b611026565b906379cc679090339092803b1561110f576110c0600080946110cb6110b4610153565b97889687958694610baf565b845260048401611042565b03925af1801561110a576110dd575b50565b6110fd9060003d8111611103575b6110f5818361023a565b810190611032565b386110da565b503d6110eb565b610c3d565b610baa565b919061112890600060208501940190610bfd565b565b60007f496e73756666696369656e742042455420696e20636f6e747261637400000000910152565b61115f601c602092610cac565b6111688161112a565b0190565b6111829060208101906000818303910152611152565b90565b1561118c57565b611194610153565b62461bcd60e51b8152806111aa6004820161116c565b0390fd5b6111f760206111c56111c06000610b9d565b6107f4565b6370a08231906111ec6111d730610e99565b926111e0610153565b95869485938493610baf565b835260048301611114565b03915afa80156112ca5761123a9160009161129c575b5061123361122d6112286112216003610eb9565b8690610edc565b6101b0565b916101b0565b1015611185565b6112576112508261124b6003610eb9565b610edc565b6003610f3d565b336112976112857f366b0ebbcb55cbff833a0746d889416b516d9b070e39248210dbbcc5f2f69e3a92610ca0565b9261128e610153565b918291826101c0565b0390a2565b6112bd915060203d81116112c3575b6112b5818361023a565b810190610c5d565b3861120d565b503d6112ab565b610c3d565b6112f56112f06112eb6112e66112fa946001610930565b610b9d565b6107f4565b61101a565b611026565b916379cc6790919092803b15611377576113286000809461133361131c610153565b97889687958694610baf565b845260048401611042565b03925af1801561137257611345575b50565b6113659060003d811161136b575b61135d818361023a565b810190611032565b38611342565b503d611353565b610c3d565b610baa565b60007f5472616e7366657246726f6d206661696c656400000000000000000000000000910152565b6113b16013602092610cac565b6113ba8161137c565b0190565b6113d490602081019060008183039101526113a4565b90565b156113de57565b6113e6610153565b62461bcd60e51b8152806113fc600482016113be565b0390fd5b91909161140b610b5d565b5061142061141b60028390610699565b610b7c565b61143261142d6000610b9d565b6107f4565b9060206323b872dd928690611464600061144b8661071b565b9661146f8a611458610153565b998a9788968795610baf565b855260048501610c0a565b03925af1918215611587576114959261149091600091611559575b506113d7565b61071b565b6020632cf5dbda9184906114bd600088956114c86114b1610153565b97889687958694610baf565b845260048401610c7c565b03925af190811561155457600091611526575b50929091839261152061150e7f2ffcd8ea21e3668481ddff339b825e8ca9278c9ee226175e1ab589019a960b6b94610ca0565b94611517610153565b93849384610ce6565b0390a290565b611547915060203d811161154d575b61153f818361023a565b810190610c5d565b386114db565b503d611535565b610c3d565b61157a915060203d8111611580575b611572818361023a565b810190610bde565b3861148a565b503d611568565b610c3d565b60206115a061159b6000610b9d565b6107f4565b6323b872dd906115ce600033936115d96115b930610e99565b976115c2610153565b98899788968795610baf565b855260048501610c0a565b03925af18015611617576115eb575b50565b61160b9060203d8111611610575b611603818361023a565b810190610bde565b6115e8565b503d6115f9565b610c3d565b919060408382031261164557806116396116429260008601610c4e565b93602001610c4e565b90565b61015e565b90565b61166161165c6116669261164a565b6106f0565b6101b0565b90565b60007f4e6f7420696e697469616c697a65640000000000000000000000000000000000910152565b61169e600f602092610cac565b6116a781611669565b0190565b6116c19060208101906000818303910152611691565b90565b156116cb57565b6116d3610153565b62461bcd60e51b8152806116e9600482016116ab565b0390fd5b90565b6117046116ff611709926116ed565b6106f0565b6101b0565b90565b61171b611721919392936101b0565b926101b0565b9161172d8382026101b0565b92818404149015171561173c57565b610ec6565b634e487b7160e01b600052601260045260246000fd5b611763611769916101b0565b916101b0565b908115611774570490565b611741565b60406117a261179d6117986117b894611790610b5d565b506002610699565b610b7c565b61071b565b630902f1ac906117b0610153565b938492610baf565b825281806117c8600482016103bf565b03915afa90811561187e576118289160008092909161184b575b506118239091826117fc6117f6600061164d565b916101b0565b118061182b575b61180c906116c4565b61181d670de0b6b3a76400006116f0565b9061170c565b611757565b90565b5061180c8161184361183d600061164d565b916101b0565b119050611803565b6118239250611871915060403d8111611877575b611869818361023a565b81019061161c565b916117e2565b503d61185f565b610c3d565b91909161188e610b5d565b506118a361189e60028390610699565b610b7c565b6118bf6118ba6118b560018590610930565b610b9d565b6107f4565b9060206323b872dd9286906118f160006118d88661071b565b966118fc8a6118e5610153565b998a9788968795610baf565b855260048501610c0a565b03925af1918215611a14576119229261191d916000916119e6575b506113d7565b61071b565b6020633bce1c7591849061194a6000889561195561193e610153565b97889687958694610baf565b845260048401610c7c565b03925af19081156119e1576000916119b3575b5092909183926119ad61199b7fd81d3d3fdda16e753fb787fb7e2c57fbbedf1c8c4aab4d998fba081f2d4f220194610ca0565b946119a4610153565b93849384610ce6565b0390a290565b6119d4915060203d81116119da575b6119cc818361023a565b810190610c5d565b38611968565b503d6119c2565b610c3d565b611a07915060203d8111611a0d575b6119ff818361023a565b810190610bde565b38611917565b503d6119f5565b610c3d565b90611a22610b5d565b50611a37611a3260028490610699565b610b7c565b611a53611a4e611a4960018690610930565b610b9d565b6107f4565b9060206323b872dd923390611a856000611a6c8661071b565b96611a9089611a79610153565b998a9788968795610baf565b855260048501610c0a565b03925af1918215611b9b57611aaa92611b6f575b5061071b565b6020633bce1c75918390611ad260003395611add611ac6610153565b97889687958694610baf565b845260048401610c7c565b03925af1908115611b6a57600091611b3c575b50913390918392611b36611b247fd81d3d3fdda16e753fb787fb7e2c57fbbedf1c8c4aab4d998fba081f2d4f220194610ca0565b94611b2d610153565b93849384610ce6565b0390a290565b611b5d915060203d8111611b63575b611b55818361023a565b810190610c5d565b38611af0565b503d611b4b565b610c3d565b611b8f9060203d8111611b94575b611b87818361023a565b810190610bde565b611aa4565b503d611b7d565b610c3d565b611bde611bd3611be595979694611bc560609560808601908682036000880152610cb5565b908482036020860152610cb5565b9660408301906101b3565b01906101b3565b565b611bef610b5d565b50611c04611bff60028390610699565b610b7c565b611c18611c1360028590610699565b610b7c565b90611c35611c30611c2b60018690610930565b610b9d565b6107f4565b9060206323b872dd923390611c676000611c4e8661071b565b96611c728c611c5b610153565b998a9788968795610baf565b855260048501610c0a565b03925af1918215611e0957611c8c92611ddd575b5061071b565b6020633bce1c75918690611cbc6000611ca48761071b565b95611cc7611cb0610153565b97889687958694610baf565b845260048401610c7c565b03925af1908115611dd857602091611ce891600091611dab575b509261071b565b632cf5dbda9290611d0d60003395611d18611d01610153565b97889687958694610baf565b845260048401610c7c565b03925af1908115611da657600091611d78575b50923391929092611d7285611d607fbe28245e56c141dd9dddce73731b090cb4ca241a4d2a22a77ed6425dc5e1b1c695610ca0565b95611d69610153565b94859485611ba0565b0390a290565b611d99915060203d8111611d9f575b611d91818361023a565b810190610c5d565b38611d2b565b503d611d87565b610c3d565b611dcb9150833d8111611dd1575b611dc3818361023a565b810190610c5d565b38611ce1565b503d611db9565b610c3d565b611dfd9060203d8111611e02575b611df5818361023a565b810190610bde565b611c86565b503d611deb565b610c3d565b602090611e23611e1e6000610b9d565b6107f4565b611e4e60006323b872dd611e59611e3930610e99565b97611e42610153565b98899788968795610baf565b855260048501610c0a565b03925af18015611ea557611e7591600091611e77575b506113d7565b565b611e98915060203d8111611e9e575b611e90818361023a565b810190610bde565b38611e6f565b503d611e86565b610c3d565b611eb3906106f3565b90565b611ebf90611eaa565b90565b90611ed360018060a01b0391610f01565b9181191691161790565b611ee690611eaa565b90565b90565b90611f01611efc611f0892611edd565b611ee9565b8254611ec2565b9055565b611f15906106f3565b90565b611f2190611f0c565b90565b611f2d90611f0c565b90565b90565b90611f48611f43611f4f92611f24565b611f30565b8254611ec2565b9055565b90611f80611f8d93611f7b611f6a611f8894611eb6565b611f7660018790610930565b611eec565b611f18565b916002610699565b611f33565b565b906020611fa4611f9f6000610b9d565b6107f4565b6323b872dd9390611fd26000611fb930610e99565b96611fdd87611fc6610153565b998a9788968795610baf565b855260048501610c0a565b03925af1908115612047576120016120109261201794600091612019575b506113d7565b61200b6003610eb9565b610edc565b6003610f3d565b565b61203a915060203d8111612040575b612032818361023a565b810190610bde565b38611ffb565b503d612028565b610c3d565b909192612057610b5d565b5061206c61206760028490610699565b610b7c565b61208061207b60028690610699565b610b7c565b9061209d61209861209360018790610930565b610b9d565b6107f4565b9060206323b872dd9288906120cf60006120b68661071b565b966120da8a6120c3610153565b998a9788968795610baf565b855260048501610c0a565b03925af191821561227e57612100926120fb91600091612250575b506113d7565b61071b565b6020633bce1c7591849061213060006121188761071b565b9561213b612124610153565b97889687958694610baf565b845260048401610c7c565b03925af190811561224b5760209161215c9160009161221e575b509261071b565b632cf5dbda92906121816000899561218c612175610153565b97889687958694610baf565b845260048401610c7c565b03925af1908115612219576000916121eb575b5093919290926121e5856121d37fbe28245e56c141dd9dddce73731b090cb4ca241a4d2a22a77ed6425dc5e1b1c695610ca0565b956121dc610153565b94859485611ba0565b0390a290565b61220c915060203d8111612212575b612204818361023a565b810190610c5d565b3861219f565b503d6121fa565b610c3d565b61223e9150833d8111612244575b612236818361023a565b810190610c5d565b38612155565b503d61222c565b610c3d565b612271915060203d8111612277575b612269818361023a565b810190610bde565b386120f5565b503d61225f565b610c3d56fea2646970667358221220e2a85ced3cec362bc0000ff2145017c8c67e57336ab7cc1540630965356efd1b64736f6c634300081c0033\r\n";

    private static String librariesLinkedBinary;

    public static final String FUNC_ADD = "add";

    public static final String FUNC_ADDDIRECT = "addDirect";

    public static final String FUNC_ADDFANTOKEN = "addFanToken";

    public static final String FUNC_ADDFROM = "addFrom";

    public static final String FUNC_BETTOKEN = "betToken";

    public static final String FUNC_BUY = "buy";

    public static final String FUNC_BUYFROM = "buyFrom";

    public static final String FUNC_FANTOKENS = "fanTokens";

    public static final String FUNC_GETPRICE = "getPrice";

    public static final String FUNC_LIQUIDITYPOOLS = "liquidityPools";

    public static final String FUNC_REMOVE = "remove";

    public static final String FUNC_REMOVEFROM = "removeFrom";

    public static final String FUNC_SELL = "sell";

    public static final String FUNC_SELLFROM = "sellFrom";

    public static final String FUNC_SWAP = "swap";

    public static final String FUNC_SWAPFROM = "swapFrom";

    public static final String FUNC_TOTALBETADDED = "totalBETAdded";

    public static final String FUNC_USE = "use";

    public static final String FUNC_USEFROM = "useFrom";

    public static final Event BUYEXECUTED_EVENT = new Event("BuyExecuted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event DIRECTADD_EVENT = new Event("DirectAdd", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SELLEXECUTED_EVENT = new Event("SellExecuted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SWAPEXECUTED_EVENT = new Event("SwapExecuted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

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

    public RemoteFunctionCall<TransactionReceipt> add(BigInteger amountBET) {
        final Function function = new Function(
                FUNC_ADD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountBET)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addDirect(BigInteger amountBET) {
        final Function function = new Function(
                FUNC_ADDDIRECT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountBET)), 
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

    public RemoteFunctionCall<TransactionReceipt> addFrom(String from, BigInteger amountBET) {
        final Function function = new Function(
                FUNC_ADDFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.generated.Uint256(amountBET)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> betToken() {
        final Function function = new Function(FUNC_BETTOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> buy(String token_name, BigInteger amountBET) {
        final Function function = new Function(
                FUNC_BUY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.generated.Uint256(amountBET)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> buyFrom(String token_name, String from,
            BigInteger amountBET) {
        final Function function = new Function(
                FUNC_BUYFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.generated.Uint256(amountBET)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> fanTokens(String param0) {
        final Function function = new Function(FUNC_FANTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getPrice(String token_name) {
        final Function function = new Function(FUNC_GETPRICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> liquidityPools(String param0) {
        final Function function = new Function(FUNC_LIQUIDITYPOOLS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> remove(BigInteger amountBET) {
        final Function function = new Function(
                FUNC_REMOVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountBET)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> removeFrom(String from, BigInteger amountBET) {
        final Function function = new Function(
                FUNC_REMOVEFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.generated.Uint256(amountBET)), 
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

    public RemoteFunctionCall<TransactionReceipt> sellFrom(String token_name, String from,
            BigInteger amountFanToken) {
        final Function function = new Function(
                FUNC_SELLFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.Address(160, from), 
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

    public RemoteFunctionCall<TransactionReceipt> swapFrom(String token_from, String token_to,
            String from, BigInteger amountFanToken) {
        final Function function = new Function(
                FUNC_SWAPFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_from), 
                new org.web3j.abi.datatypes.Utf8String(token_to), 
                new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.generated.Uint256(amountFanToken)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> totalBETAdded() {
        final Function function = new Function(FUNC_TOTALBETADDED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> use(String token_name, BigInteger amount) {
        final Function function = new Function(
                FUNC_USE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> useFrom(String token_name, String from,
            BigInteger amount) {
        final Function function = new Function(
                FUNC_USEFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(token_name), 
                new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static List<BuyExecutedEventResponse> getBuyExecutedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BUYEXECUTED_EVENT, transactionReceipt);
        ArrayList<BuyExecutedEventResponse> responses = new ArrayList<BuyExecutedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BuyExecutedEventResponse typedResponse = new BuyExecutedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.tokenName = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BuyExecutedEventResponse getBuyExecutedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BUYEXECUTED_EVENT, log);
        BuyExecutedEventResponse typedResponse = new BuyExecutedEventResponse();
        typedResponse.log = log;
        typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.tokenName = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<BuyExecutedEventResponse> buyExecutedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBuyExecutedEventFromLog(log));
    }

    public Flowable<BuyExecutedEventResponse> buyExecutedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BUYEXECUTED_EVENT));
        return buyExecutedEventFlowable(filter);
    }

    public static List<DirectAddEventResponse> getDirectAddEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(DIRECTADD_EVENT, transactionReceipt);
        ArrayList<DirectAddEventResponse> responses = new ArrayList<DirectAddEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DirectAddEventResponse typedResponse = new DirectAddEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static DirectAddEventResponse getDirectAddEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(DIRECTADD_EVENT, log);
        DirectAddEventResponse typedResponse = new DirectAddEventResponse();
        typedResponse.log = log;
        typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<DirectAddEventResponse> directAddEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getDirectAddEventFromLog(log));
    }

    public Flowable<DirectAddEventResponse> directAddEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DIRECTADD_EVENT));
        return directAddEventFlowable(filter);
    }

    public static List<SellExecutedEventResponse> getSellExecutedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(SELLEXECUTED_EVENT, transactionReceipt);
        ArrayList<SellExecutedEventResponse> responses = new ArrayList<SellExecutedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SellExecutedEventResponse typedResponse = new SellExecutedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.seller = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.tokenName = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static SellExecutedEventResponse getSellExecutedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(SELLEXECUTED_EVENT, log);
        SellExecutedEventResponse typedResponse = new SellExecutedEventResponse();
        typedResponse.log = log;
        typedResponse.seller = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.tokenName = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<SellExecutedEventResponse> sellExecutedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getSellExecutedEventFromLog(log));
    }

    public Flowable<SellExecutedEventResponse> sellExecutedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SELLEXECUTED_EVENT));
        return sellExecutedEventFlowable(filter);
    }

    public static List<SwapExecutedEventResponse> getSwapExecutedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(SWAPEXECUTED_EVENT, transactionReceipt);
        ArrayList<SwapExecutedEventResponse> responses = new ArrayList<SwapExecutedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SwapExecutedEventResponse typedResponse = new SwapExecutedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.tokenFrom = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.tokenTo = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static SwapExecutedEventResponse getSwapExecutedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(SWAPEXECUTED_EVENT, log);
        SwapExecutedEventResponse typedResponse = new SwapExecutedEventResponse();
        typedResponse.log = log;
        typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.tokenFrom = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.tokenTo = (String) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.amountIn = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        typedResponse.amountOut = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
        return typedResponse;
    }

    public Flowable<SwapExecutedEventResponse> swapExecutedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getSwapExecutedEventFromLog(log));
    }

    public Flowable<SwapExecutedEventResponse> swapExecutedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SWAPEXECUTED_EVENT));
        return swapExecutedEventFlowable(filter);
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
            ContractGasProvider contractGasProvider, String _betToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _betToken)));
        return deployRemoteCall(Exchange.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    public static RemoteCall<Exchange> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider, String _betToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _betToken)));
        return deployRemoteCall(Exchange.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Exchange> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit, String _betToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _betToken)));
        return deployRemoteCall(Exchange.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Exchange> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit, String _betToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _betToken)));
        return deployRemoteCall(Exchange.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class BuyExecutedEventResponse extends BaseEventResponse {
        public String buyer;

        public String tokenName;

        public BigInteger amountIn;

        public BigInteger amountOut;
    }

    public static class DirectAddEventResponse extends BaseEventResponse {
        public String sender;

        public BigInteger amount;
    }

    public static class SellExecutedEventResponse extends BaseEventResponse {
        public String seller;

        public String tokenName;

        public BigInteger amountIn;

        public BigInteger amountOut;
    }

    public static class SwapExecutedEventResponse extends BaseEventResponse {
        public String user;

        public String tokenFrom;

        public String tokenTo;

        public BigInteger amountIn;

        public BigInteger amountOut;
    }
}
