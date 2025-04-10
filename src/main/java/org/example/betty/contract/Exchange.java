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
    public static final String BINARY = "癤�0x60806040523461002f576100196100146100fa565b6101a2565b610021610034565b6127966101b8823961279690f35b61003a565b60405190565b600080fd5b601f801991011690565b634e487b7160e01b600052604160045260246000fd5b906100699061003f565b810190811060018060401b0382111761008157604052565b610049565b90610099610092610034565b928361005f565b565b600080fd5b60018060a01b031690565b6100b4906100a0565b90565b6100c0816100ab565b036100c757565b600080fd5b905051906100d9826100b7565b565b906020828203126100f5576100f2916000016100cc565b90565b61009b565b61011861294e8038038061010d81610086565b9283398101906100db565b90565b90565b61013261012d610137926100a0565b61011b565b6100a0565b90565b6101439061011e565b90565b61014f9061013a565b90565b60001b90565b9061016960018060a01b0391610152565b9181191691161790565b61017c9061013a565b90565b90565b9061019761019261019e92610173565b61017f565b8254610158565b9055565b6101ae6101b591610146565b6000610182565b56fe60806040526004361015610013575b610b58565b61001e60003561014d565b80630774c366146101485780630ca5c504146101435780631003e2d21461013e57806322cb8f1714610139578063248f8864146101345780633872fd311461012f57806344b760391461012a5780634cc8221514610125578063524f3889146101205780636028e6671461011b57806362efb0c71461011657806378691f16146101115780639f6563211461010c578063a451f60214610107578063b116479414610102578063c2ad4984146100fd578063e90ca51a146100f8578063f08463fb146100f35763f93a47af0361000e57610b1f565b610a74565b610a40565b6109b9565b610956565b6108fa565b610858565b610823565b61077f565b61074a565b6105c0565b610557565b610521565b6104ed565b61042c565b6103f8565b6103c5565b61036a565b6101d6565b60e01c90565b60405190565b600080fd5b600080fd5b600091031261016e57565b61015e565b1c90565b90565b61018a90600861018f9302610173565b610177565b90565b9061019d915461017a565b90565b6101ad6003600090610192565b90565b90565b6101bc906101b0565b9052565b91906101d4906000602085019401906101b3565b565b34610206576101e6366004610163565b6102026101f16101a0565b6101f9610153565b918291826101c0565b0390f35b610159565b600080fd5b600080fd5b600080fd5b601f801991011690565b634e487b7160e01b600052604160045260246000fd5b906102449061021a565b810190811067ffffffffffffffff82111761025e57604052565b610224565b9061027661026f610153565b928361023a565b565b67ffffffffffffffff81116102965761029260209161021a565b0190565b610224565b90826000939282370152565b909291926102bc6102b782610278565b610263565b938185526020850190828401116102d8576102d69261029b565b565b610215565b9080601f830112156102fb578160206102f8933591016102a7565b90565b610210565b610309816101b0565b0361031057565b600080fd5b9050359061032282610300565b565b91906040838203126103655760008301359067ffffffffffffffff8211610360576103548161035d9386016102dd565b93602001610315565b90565b61020b565b61015e565b3461039b57610397610386610380366004610324565b90610e54565b61038e610153565b918291826101c0565b0390f35b610159565b906020828203126103ba576103b791600001610315565b90565b61015e565b60000190565b346103f3576103dd6103d83660046103a0565b611133565b6103e5610153565b806103ef816103bf565b0390f35b610159565b346104275761041161040b366004610324565b90611225565b610419610153565b80610423816103bf565b0390f35b610159565b3461045a5761044461043f3660046103a0565b61136d565b61044c610153565b80610456816103bf565b0390f35b610159565b60018060a01b031690565b6104739061045f565b90565b61047f8161046a565b0361048657565b600080fd5b9050359061049882610476565b565b90916060828403126104e857600082013567ffffffffffffffff81116104e3576104c9846104e09285016102dd565b936104d7816020860161048b565b93604001610315565b90565b61020b565b61015e565b3461051c5761050661050036600461049a565b9161148e565b61050e610153565b80610518816103bf565b0390f35b610159565b346105525761054e61053d61053736600461049a565b9161153b565b610545610153565b918291826101c0565b0390f35b610159565b346105855761056f61056a3660046103a0565b611763565b610577610153565b80610581816103bf565b0390f35b610159565b906020828203126105bb57600082013567ffffffffffffffff81116105b6576105b392016102dd565b90565b61020b565b61015e565b346105f0576105ec6105db6105d636600461058a565b61195e565b6105e3610153565b918291826101c0565b0390f35b610159565b5190565b905090565b60005b838110610612575050906000910152565b806020918301518185015201610601565b61064861063f92602092610636816105f5565b948580936105f9565b938491016105fe565b0190565b90565b61065b610660916101b0565b61064c565b9052565b61067461067b9160209493610623565b809261064f565b0190565b61069361068a610153565b92839283610664565b03902090565b6106a29161067f565b90565b60018060a01b031690565b6106c09060086106c59302610173565b6106a5565b90565b906106d391546106b0565b90565b6106ed906106e8600291600092610699565b6106c8565b90565b90565b61070761070261070c9261045f565b6106f0565b61045f565b90565b610718906106f3565b90565b6107249061070f565b90565b6107309061071b565b9052565b919061074890600060208501940190610727565b565b3461077a5761077661076561076036600461058a565b6106d6565b61076d610153565b91829182610734565b0390f35b610159565b346107b0576107ac61079b61079536600461049a565b91611a68565b6107a3610153565b918291826101c0565b0390f35b610159565b60018060a01b031690565b6107d09060086107d59302610173565b6107b5565b90565b906107e391546107c0565b90565b6107f16000806107d8565b90565b6107fd9061070f565b90565b610809906107f4565b9052565b919061082190600060208501940190610800565b565b3461085357610833366004610163565b61084f61083e6107e6565b610846610153565b9182918261080d565b0390f35b610159565b346108895761088561087461086e366004610324565b90611c94565b61087c610153565b918291826101c0565b0390f35b610159565b90916060828403126108f557600082013567ffffffffffffffff81116108f057836108ba9184016102dd565b9260208301359067ffffffffffffffff82116108eb576108df816108e89386016102dd565b93604001610315565b90565b61020b565b61020b565b61015e565b3461092b5761092761091661091036600461088e565b91611f8a565b61091e610153565b918291826101c0565b0390f35b610159565b6109399161067f565b90565b6109539061094e600191600092610930565b6107d8565b90565b346109865761098261097161096c36600461058a565b61093c565b610979610153565b9182918261080d565b0390f35b610159565b91906040838203126109b457806109a86109b1926000860161048b565b93602001610315565b90565b61015e565b346109e8576109d26109cc36600461098b565b90612255565b6109da610153565b806109e4816103bf565b0390f35b610159565b9091606082840312610a3b57600082013567ffffffffffffffff8111610a3657610a1c84610a339285016102dd565b93610a2a816020860161048b565b9360400161048b565b90565b61020b565b61015e565b34610a6f57610a59610a533660046109ed565b9161239a565b610a61610153565b80610a6b816103bf565b0390f35b610159565b34610aa357610a8d610a8736600461098b565b906123d6565b610a95610153565b80610a9f816103bf565b0390f35b610159565b608081830312610b1a57600081013567ffffffffffffffff8111610b155782610ad29183016102dd565b92602082013567ffffffffffffffff8111610b1057610af684610b0d9285016102dd565b93610b04816040860161048b565b93606001610315565b90565b61020b565b61020b565b61015e565b34610b5357610b4f610b3e610b35366004610aa8565b92919091612493565b610b46610153565b918291826101c0565b0390f35b610159565b600080fd5b600090565b60001c90565b610b74610b7991610b62565b6106a5565b90565b610b869054610b68565b90565b610b95610b9a91610b62565b6107b5565b90565b610ba79054610b89565b90565b610bb39061070f565b90565b600080fd5b60e01b90565b151590565b610bcf81610bc1565b03610bd657565b600080fd5b90505190610be882610bc6565b565b90602082820312610c0457610c0191600001610bdb565b90565b61015e565b610c129061046a565b9052565b604090610c40610c479496959396610c3660608401986000850190610c09565b6020830190610c09565b01906101b3565b565b610c51610153565b3d6000823e3d90fd5b60209181520190565b60007f5472616e7366657246726f6d206661696c656400000000000000000000000000910152565b610c986013602092610c5a565b610ca181610c63565b0190565b610cbb9060208101906000818303910152610c8b565b90565b15610cc557565b610ccd610153565b62461bcd60e51b815280610ce360048201610ca5565b0390fd5b916020610d09929493610d0260408201966000830190610c09565b01906101b3565b565b60007f5472616e7366657220746f20706f6f6c206661696c6564000000000000000000910152565b610d406017602092610c5a565b610d4981610d0b565b0190565b610d639060208101906000818303910152610d33565b90565b15610d6d57565b610d75610153565b62461bcd60e51b815280610d8b60048201610d4d565b0390fd5b90505190610d9c82610300565b565b90602082820312610db857610db591600001610d8f565b90565b61015e565b916020610ddf929493610dd8604082019660008301906101b3565b0190610c09565b565b610dea9061070f565b90565b610e0c610e15602093610e1a93610e03816105f5565b93848093610c5a565b958691016105fe565b61021a565b0190565b604090610e4b610e40610e529597969460608401908482036000860152610ded565b9660208301906101b3565b01906101b3565b565b90610e5d610b5d565b50610e72610e6d60028490610699565b610b7c565b610e84610e7f6000610b9d565b6107f4565b60206323b872dd913390610eb56000610e9c30610baa565b95610ec089610ea9610153565b98899788968795610bbb565b855260048501610c16565b03925af1801561107657610edc91600091611048575b50610cbe565b610eee610ee96000610b9d565b6107f4565b90602063a9059cbb92610f008361071b565b90610f1f60008796610f2a610f13610153565b98899687958694610bbb565b845260048401610ce7565b03925af191821561104357610f5092610f4b91600091611015575b50610d66565b61071b565b6020632cf5dbda918390610f7860003395610f83610f6c610153565b97889687958694610bbb565b845260048401610dbd565b03925af190811561101057600091610fe2575b50913390918392610fdc610fca7f2ffcd8ea21e3668481ddff339b825e8ca9278c9ee226175e1ab589019a960b6b94610de1565b94610fd3610153565b93849384610e1e565b0390a290565b611003915060203d8111611009575b610ffb818361023a565b810190610d9e565b38610f96565b503d610ff1565b610c49565b611036915060203d811161103c575b61102e818361023a565b810190610bea565b38610f45565b503d611024565b610c49565b611069915060203d811161106f575b611061818361023a565b810190610bea565b38610ed6565b503d611057565b610c49565b61108761108c91610b62565b610177565b90565b611099905461107b565b90565b634e487b7160e01b600052601160045260246000fd5b6110c16110c7919392936101b0565b926101b0565b82018092116110d257565b61109c565b60001b90565b906110ea600019916110d7565b9181191691161790565b61110861110361110d926101b0565b6106f0565b6101b0565b90565b90565b9061112861112361112f926110f4565b611110565b82546110dd565b9055565b6111456111406000610b9d565b6107f4565b9060206323b872dd923390611177600061115e30610baa565b966111828761116b610153565b998a9788968795610bbb565b855260048501610c16565b03925af19081156111ec576111a66111b5926111bc946000916111be575b50610cbe565b6111b0600361108f565b6110b2565b6003611113565b565b6111df915060203d81116111e5575b6111d7818361023a565b810190610bea565b386111a0565b503d6111cd565b610c49565b6111fa906106f3565b90565b611206906111f1565b90565b6112129061070f565b90565b600091031261122057565b61015e565b61124b61124661124161123c611250946001610930565b610b9d565b6107f4565b6111fd565b611209565b906379cc679090339092803b156112ce5761127f6000809461128a611273610153565b97889687958694610bbb565b845260048401610ce7565b03925af180156112c95761129c575b50565b6112bc9060003d81116112c2575b6112b4818361023a565b810190611215565b38611299565b503d6112aa565b610c49565b610bb6565b91906112e790600060208501940190610c09565b565b60007f496e73756666696369656e742042455420696e20636f6e747261637400000000910152565b61131e601c602092610c5a565b611327816112e9565b0190565b6113419060208101906000818303910152611311565b90565b1561134b57565b611353610153565b62461bcd60e51b8152806113696004820161132b565b0390fd5b6113b6602061138461137f6000610b9d565b6107f4565b6370a08231906113ab61139630610baa565b9261139f610153565b95869485938493610bbb565b8352600483016112d3565b03915afa8015611489576113f99160009161145b575b506113f26113ec6113e76113e0600361108f565b86906110b2565b6101b0565b916101b0565b1015611344565b61141661140f8261140a600361108f565b6110b2565b6003611113565b336114566114447f366b0ebbcb55cbff833a0746d889416b516d9b070e39248210dbbcc5f2f69e3a92610de1565b9261144d610153565b918291826101c0565b0390a2565b61147c915060203d8111611482575b611474818361023a565b810190610d9e565b386113cc565b503d61146a565b610c49565b6114b46114af6114aa6114a56114b9946001610930565b610b9d565b6107f4565b6111fd565b611209565b916379cc6790919092803b15611536576114e7600080946114f26114db610153565b97889687958694610bbb565b845260048401610ce7565b03925af1801561153157611504575b50565b6115249060003d811161152a575b61151c818361023a565b810190611215565b38611501565b503d611512565b610c49565b610bb6565b919091611546610b5d565b5061155b61155660028390610699565b610b7c565b61156d6115686000610b9d565b6107f4565b60206323b872dd91869061159e600061158530610baa565b956115a98a611592610153565b98899788968795610bbb565b855260048501610c16565b03925af1801561175e576115c591600091611730575b50610cbe565b6115d76115d26000610b9d565b6107f4565b90602063a9059cbb926115e98361071b565b90611608600088966116136115fc610153565b98899687958694610bbb565b845260048401610ce7565b03925af191821561172b5761163992611634916000916116fd575b50610d66565b61071b565b6020632cf5dbda9184906116616000889561166c611655610153565b97889687958694610bbb565b845260048401610dbd565b03925af19081156116f8576000916116ca575b5092909183926116c46116b27f2ffcd8ea21e3668481ddff339b825e8ca9278c9ee226175e1ab589019a960b6b94610de1565b946116bb610153565b93849384610e1e565b0390a290565b6116eb915060203d81116116f1575b6116e3818361023a565b810190610d9e565b3861167f565b503d6116d9565b610c49565b61171e915060203d8111611724575b611716818361023a565b810190610bea565b3861162e565b503d61170c565b610c49565b611751915060203d8111611757575b611749818361023a565b810190610bea565b386115bf565b503d61173f565b610c49565b60206117776117726000610b9d565b6107f4565b6323b872dd906117a5600033936117b061179030610baa565b97611799610153565b98899788968795610bbb565b855260048501610c16565b03925af180156117fc576117cc916000916117ce575b50610cbe565b565b6117ef915060203d81116117f5575b6117e7818361023a565b810190610bea565b386117c6565b503d6117dd565b610c49565b919060408382031261182a578061181e6118279260008601610d8f565b93602001610d8f565b90565b61015e565b90565b61184661184161184b9261182f565b6106f0565b6101b0565b90565b60007f4e6f7420696e697469616c697a65640000000000000000000000000000000000910152565b611883600f602092610c5a565b61188c8161184e565b0190565b6118a69060208101906000818303910152611876565b90565b156118b057565b6118b8610153565b62461bcd60e51b8152806118ce60048201611890565b0390fd5b90565b6118e96118e46118ee926118d2565b6106f0565b6101b0565b90565b611900611906919392936101b0565b926101b0565b916119128382026101b0565b92818404149015171561192157565b61109c565b634e487b7160e01b600052601260045260246000fd5b61194861194e916101b0565b916101b0565b908115611959570490565b611926565b604061198761198261197d61199d94611975610b5d565b506002610699565b610b7c565b61071b565b630902f1ac90611995610153565b938492610bbb565b825281806119ad600482016103bf565b03915afa908115611a6357611a0d91600080929091611a30575b50611a089091826119e16119db6000611832565b916101b0565b1180611a10575b6119f1906118a9565b611a02670de0b6b3a76400006118d5565b906118f1565b61193c565b90565b506119f181611a28611a226000611832565b916101b0565b1190506119e8565b611a089250611a56915060403d8111611a5c575b611a4e818361023a565b810190611801565b916119c7565b503d611a44565b610c49565b919091611a73610b5d565b50611a88611a8360028390610699565b610b7c565b611a9c611a9760018490610930565b610b9d565b611aa5816107f4565b9060206323b872dd928790611ad76000611abe30610baa565b96611ae28b611acb610153565b998a9788968795610bbb565b855260048501610c16565b03925af1918215611c8f57611b0892611b0391600091611c61575b50610cbe565b6107f4565b90602063a9059cbb92611b1a8361071b565b90611b3960008896611b44611b2d610153565b98899687958694610bbb565b845260048401610ce7565b03925af1918215611c5c57611b6a92611b6591600091611c2e575b50610d66565b61071b565b6020633bce1c75918490611b9260008895611b9d611b86610153565b97889687958694610bbb565b845260048401610dbd565b03925af1908115611c2957600091611bfb575b509290918392611bf5611be37fd81d3d3fdda16e753fb787fb7e2c57fbbedf1c8c4aab4d998fba081f2d4f220194610de1565b94611bec610153565b93849384610e1e565b0390a290565b611c1c915060203d8111611c22575b611c14818361023a565b810190610d9e565b38611bb0565b503d611c0a565b610c49565b611c4f915060203d8111611c55575b611c47818361023a565b810190610bea565b38611b5f565b503d611c3d565b610c49565b611c82915060203d8111611c88575b611c7a818361023a565b810190610bea565b38611afd565b503d611c70565b610c49565b90611c9d610b5d565b50611cb2611cad60028490610699565b610b7c565b611cc6611cc160018590610930565b610b9d565b611ccf816107f4565b9060206323b872dd923390611d016000611ce830610baa565b96611d0c8a611cf5610153565b998a9788968795610bbb565b855260048501610c16565b03925af1918215611eba57611d3292611d2d91600091611e8c575b50610cbe565b6107f4565b90602063a9059cbb92611d448361071b565b90611d6360008796611d6e611d57610153565b98899687958694610bbb565b845260048401610ce7565b03925af1918215611e8757611d9492611d8f91600091611e59575b50610d66565b61071b565b6020633bce1c75918390611dbc60003395611dc7611db0610153565b97889687958694610bbb565b845260048401610dbd565b03925af1908115611e5457600091611e26575b50913390918392611e20611e0e7fd81d3d3fdda16e753fb787fb7e2c57fbbedf1c8c4aab4d998fba081f2d4f220194610de1565b94611e17610153565b93849384610e1e565b0390a290565b611e47915060203d8111611e4d575b611e3f818361023a565b810190610d9e565b38611dda565b503d611e35565b610c49565b611e7a915060203d8111611e80575b611e72818361023a565b810190610bea565b38611d89565b503d611e68565b610c49565b611ead915060203d8111611eb3575b611ea5818361023a565b810190610bea565b38611d27565b503d611e9b565b610c49565b60007f5472616e7366657220746f2066726f6d506f6f6c206661696c65640000000000910152565b611ef4601b602092610c5a565b611efd81611ebf565b0190565b611f179060208101906000818303910152611ee7565b90565b15611f2157565b611f29610153565b62461bcd60e51b815280611f3f60048201611f01565b0390fd5b611f81611f76611f8895979694611f6860609560808601908682036000880152610ded565b908482036020860152610ded565b9660408301906101b3565b01906101b3565b565b611f92610b5d565b50611fa7611fa260028390610699565b610b7c565b611fbb611fb660028590610699565b610b7c565b90611fd0611fcb60018590610930565b610b9d565b611fd9816107f4565b9060206323b872dd92339061200b6000611ff230610baa565b966120168d611fff610153565b998a9788968795610bbb565b855260048501610c16565b03925af19182156122505761203c9261203791600091612222575b50610cbe565b6107f4565b90602063a9059cbb9261204e8361071b565b9061206d60008a96612078612061610153565b98899687958694610bbb565b845260048401610ce7565b03925af191821561221d5761209e92612099916000916121ef575b50611f1a565b61071b565b6020633bce1c759186906120ce60006120b68761071b565b956120d96120c2610153565b97889687958694610bbb565b845260048401610dbd565b03925af19081156121ea576020916120fa916000916121bd575b509261071b565b632cf5dbda929061211f6000339561212a612113610153565b97889687958694610bbb565b845260048401610dbd565b03925af19081156121b85760009161218a575b50923391929092612184856121727fbe28245e56c141dd9dddce73731b090cb4ca241a4d2a22a77ed6425dc5e1b1c695610de1565b9561217b610153565b94859485611f43565b0390a290565b6121ab915060203d81116121b1575b6121a3818361023a565b810190610d9e565b3861213d565b503d612199565b610c49565b6121dd9150833d81116121e3575b6121d5818361023a565b810190610d9e565b386120f3565b503d6121cb565b610c49565b612210915060203d8111612216575b612208818361023a565b810190610bea565b38612093565b503d6121fe565b610c49565b612243915060203d8111612249575b61223b818361023a565b810190610bea565b38612031565b503d612231565b610c49565b60209061226a6122656000610b9d565b6107f4565b61229560006323b872dd6122a061228030610baa565b97612289610153565b98899788968795610bbb565b855260048501610c16565b03925af180156122ec576122bc916000916122be575b50610cbe565b565b6122df915060203d81116122e5575b6122d7818361023a565b810190610bea565b386122b6565b503d6122cd565b610c49565b6122fa906106f3565b90565b612306906122f1565b90565b9061231a60018060a01b03916110d7565b9181191691161790565b61232d906122f1565b90565b90565b9061234861234361234f92612324565b612330565b8254612309565b9055565b61235c906106f3565b90565b61236890612353565b90565b61237490612353565b90565b90565b9061238f61238a6123969261236b565b612377565b8254612309565b9055565b906123c76123d4936123c26123b16123cf946122fd565b6123bd60018790610930565b612333565b61235f565b916002610699565b61237a565b565b9060206123eb6123e66000610b9d565b6107f4565b6323b872dd9390612419600061240030610baa565b966124248761240d610153565b998a9788968795610bbb565b855260048501610c16565b03925af190811561248e576124486124579261245e94600091612460575b50610cbe565b612452600361108f565b6110b2565b6003611113565b565b612481915060203d8111612487575b612479818361023a565b810190610bea565b38612442565b503d61246f565b610c49565b90919261249e610b5d565b506124b36124ae60028490610699565b610b7c565b6124c76124c260028690610699565b610b7c565b906124dc6124d760018690610930565b610b9d565b6124e5816107f4565b9060206323b872dd92899061251760006124fe30610baa565b966125228b61250b610153565b998a9788968795610bbb565b855260048501610c16565b03925af191821561275b57612548926125439160009161272d575b50610cbe565b6107f4565b90602063a9059cbb9261255a8361071b565b906125796000889661258461256d610153565b98899687958694610bbb565b845260048401610ce7565b03925af1918215612728576125aa926125a5916000916126fa575b50611f1a565b61071b565b6020633bce1c759184906125da60006125c28761071b565b956125e56125ce610153565b97889687958694610bbb565b845260048401610dbd565b03925af19081156126f557602091612606916000916126c8575b509261071b565b632cf5dbda929061262b6000899561263661261f610153565b97889687958694610bbb565b845260048401610dbd565b03925af19081156126c357600091612695575b50939192909261268f8561267d7fbe28245e56c141dd9dddce73731b090cb4ca241a4d2a22a77ed6425dc5e1b1c695610de1565b95612686610153565b94859485611f43565b0390a290565b6126b6915060203d81116126bc575b6126ae818361023a565b810190610d9e565b38612649565b503d6126a4565b610c49565b6126e89150833d81116126ee575b6126e0818361023a565b810190610d9e565b386125ff565b503d6126d6565b610c49565b61271b915060203d8111612721575b612713818361023a565b810190610bea565b3861259f565b503d612709565b610c49565b61274e915060203d8111612754575b612746818361023a565b810190610bea565b3861253d565b503d61273c565b610c4956fea26469706673582212201905b89108c71d196873848477666d887cb270e37eb49e2d8c86d71491273a9164736f6c634300081c0033\r\n";

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
//java 수정