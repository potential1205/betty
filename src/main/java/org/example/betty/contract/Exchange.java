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
    public static final String BINARY = "癤�0x608060405234801561001057600080fd5b50604051612678380380612678833981810160405281019061003291906100db565b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050610108565b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006100a88261007d565b9050919050565b6100b88161009d565b81146100c357600080fd5b50565b6000815190506100d5816100af565b92915050565b6000602082840312156100f1576100f0610078565b5b60006100ff848285016100c6565b91505092915050565b612561806101176000396000f3fe608060405234801561001057600080fd5b50600436106101215760003560e01c80636028e667116100ad578063b116479411610071578063b11647941461033e578063c2ad49841461036e578063e90ca51a1461038a578063f08463fb146103a6578063f93a47af146103c257610121565b80636028e6671461026057806362efb0c71461029057806378691f16146102c05780639f656321146102de578063a451f6021461030e57610121565b8063248f8864116100f4578063248f8864146101ac5780633872fd31146101c857806344b76039146101e45780634cc8221514610214578063524f38891461023057610121565b80630774c366146101265780630ca5c504146101445780631003e2d21461017457806322cb8f1714610190575b600080fd5b61012e6103f2565b60405161013b9190611a16565b60405180910390f35b61015e60048036038101906101599190611bb7565b6103f8565b60405161016b9190611a16565b60405180910390f35b61018e60048036038101906101899190611c13565b6105bc565b005b6101aa60048036038101906101a59190611bb7565b610679565b005b6101c660048036038101906101c19190611c13565b61072e565b005b6101e260048036038101906101dd9190611c9e565b610881565b005b6101fe60048036038101906101f99190611c9e565b610937565b60405161020b9190611a16565b60405180910390f35b61022e60048036038101906102299190611c13565b610b3a565b005b61024a60048036038101906102459190611d0d565b610bde565b6040516102579190611a16565b60405180910390f35b61027a60048036038101906102759190611d0d565b610d0f565b6040516102879190611db5565b60405180910390f35b6102aa60048036038101906102a59190611c9e565b610d58565b6040516102b79190611a16565b60405180910390f35b6102c8610f79565b6040516102d59190611df1565b60405180910390f35b6102f860048036038101906102f39190611bb7565b610f9d565b6040516103059190611a16565b60405180910390f35b61032860048036038101906103239190611e0c565b61117f565b6040516103359190611a16565b60405180910390f35b61035860048036038101906103539190611d0d565b61142b565b6040516103659190611df1565b60405180910390f35b61038860048036038101906103839190611e97565b611474565b005b6103a4600480360381019061039f9190611ed7565b611557565b005b6103c060048036038101906103bb9190611e97565b611616565b005b6103dc60048036038101906103d79190611f46565b611712565b6040516103e99190611a16565b60405180910390f35b60035481565b60008060028460405161040b9190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3383866040518463ffffffff1660e01b81526004016104999392919061207c565b6020604051808303816000875af11580156104b8573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104dc91906120eb565b5060008173ffffffffffffffffffffffffffffffffffffffff16632cf5dbda85336040518363ffffffff1660e01b815260040161051a929190612118565b6020604051808303816000875af1158015610539573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061055d9190612156565b90503373ffffffffffffffffffffffffffffffffffffffff167f2ffcd8ea21e3668481ddff339b825e8ca9278c9ee226175e1ab589019a960b6b8686846040516105a9939291906121cd565b60405180910390a2809250505092915050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3330846040518463ffffffff1660e01b81526004016106199392919061207c565b6020604051808303816000875af1158015610638573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061065c91906120eb565b50806003600082825461066f919061223a565b9250508190555050565b600060018360405161068b9190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508073ffffffffffffffffffffffffffffffffffffffff166379cc679033846040518363ffffffff1660e01b81526004016106f792919061226e565b600060405180830381600087803b15801561071157600080fd5b505af1158015610725573d6000803e3d6000fd5b50505050505050565b8060035461073c919061223a565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166370a08231306040518263ffffffff1660e01b81526004016107959190612297565b602060405180830381865afa1580156107b2573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906107d69190612156565b1015610817576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161080e906122fe565b60405180910390fd5b8060036000828254610829919061223a565b925050819055503373ffffffffffffffffffffffffffffffffffffffff167f366b0ebbcb55cbff833a0746d889416b516d9b070e39248210dbbcc5f2f69e3a826040516108769190611a16565b60405180910390a250565b60006001846040516108939190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508073ffffffffffffffffffffffffffffffffffffffff166379cc679084846040518363ffffffff1660e01b81526004016108ff92919061226e565b600060405180830381600087803b15801561091957600080fd5b505af115801561092d573d6000803e3d6000fd5b5050505050505050565b60008060028560405161094a9190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd8583866040518463ffffffff1660e01b81526004016109d89392919061207c565b6020604051808303816000875af11580156109f7573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610a1b91906120eb565b610a5a576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610a519061236a565b60405180910390fd5b60008173ffffffffffffffffffffffffffffffffffffffff16632cf5dbda85876040518363ffffffff1660e01b8152600401610a97929190612118565b6020604051808303816000875af1158015610ab6573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610ada9190612156565b90508473ffffffffffffffffffffffffffffffffffffffff167f2ffcd8ea21e3668481ddff339b825e8ca9278c9ee226175e1ab589019a960b6b878684604051610b26939291906121cd565b60405180910390a280925050509392505050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3330846040518463ffffffff1660e01b8152600401610b979392919061207c565b6020604051808303816000875af1158015610bb6573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610bda91906120eb565b5050565b600080600283604051610bf19190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506000808273ffffffffffffffffffffffffffffffffffffffff16630902f1ac6040518163ffffffff1660e01b81526004016040805180830381865afa158015610c6f573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610c93919061238a565b91509150600082118015610ca75750600081115b610ce6576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610cdd90612416565b60405180910390fd5b81670de0b6b3a764000082610cfb9190612436565b610d0591906124a7565b9350505050919050565b6002818051602081018201805184825260208301602085012081835280955050505050506000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600080600285604051610d6b9190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600185604051610dac9190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd8583866040518463ffffffff1660e01b8152600401610e179392919061207c565b6020604051808303816000875af1158015610e36573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610e5a91906120eb565b610e99576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610e909061236a565b60405180910390fd5b60008173ffffffffffffffffffffffffffffffffffffffff16633bce1c7585876040518363ffffffff1660e01b8152600401610ed6929190612118565b6020604051808303816000875af1158015610ef5573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610f199190612156565b90508473ffffffffffffffffffffffffffffffffffffffff167fd81d3d3fdda16e753fb787fb7e2c57fbbedf1c8c4aab4d998fba081f2d4f2201878684604051610f65939291906121cd565b60405180910390a280925050509392505050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600080600284604051610fb09190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600184604051610ff19190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3383866040518463ffffffff1660e01b815260040161105c9392919061207c565b6020604051808303816000875af115801561107b573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061109f91906120eb565b5060008173ffffffffffffffffffffffffffffffffffffffff16633bce1c7585336040518363ffffffff1660e01b81526004016110dd929190612118565b6020604051808303816000875af11580156110fc573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906111209190612156565b90503373ffffffffffffffffffffffffffffffffffffffff167fd81d3d3fdda16e753fb787fb7e2c57fbbedf1c8c4aab4d998fba081f2d4f220186868460405161116c939291906121cd565b60405180910390a2809250505092915050565b6000806002856040516111929190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060006002856040516111d59190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506001866040516112169190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd3384876040518463ffffffff1660e01b81526004016112819392919061207c565b6020604051808303816000875af11580156112a0573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906112c491906120eb565b5060008273ffffffffffffffffffffffffffffffffffffffff16633bce1c7586846040518363ffffffff1660e01b8152600401611302929190612118565b6020604051808303816000875af1158015611321573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906113459190612156565b905060008273ffffffffffffffffffffffffffffffffffffffff16632cf5dbda83336040518363ffffffff1660e01b8152600401611384929190612118565b6020604051808303816000875af11580156113a3573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906113c79190612156565b90503373ffffffffffffffffffffffffffffffffffffffff167fbe28245e56c141dd9dddce73731b090cb4ca241a4d2a22a77ed6425dc5e1b1c68989898560405161141594939291906124d8565b60405180910390a2809450505050509392505050565b6001818051602081018201805184825260208301602085012081835280955050505050506000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd8330846040518463ffffffff1660e01b81526004016114d19392919061207c565b6020604051808303816000875af11580156114f0573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061151491906120eb565b611553576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161154a9061236a565b60405180910390fd5b5050565b816001846040516115689190612056565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550806002846040516115c59190612056565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd8330846040518463ffffffff1660e01b81526004016116739392919061207c565b6020604051808303816000875af1158015611692573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906116b691906120eb565b6116f5576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016116ec9061236a565b60405180910390fd5b8060036000828254611707919061223a565b925050819055505050565b6000806002866040516117259190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060006002866040516117689190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506001876040516117a99190612056565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd8684876040518463ffffffff1660e01b81526004016118149392919061207c565b6020604051808303816000875af1158015611833573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061185791906120eb565b611896576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161188d9061236a565b60405180910390fd5b60008273ffffffffffffffffffffffffffffffffffffffff16633bce1c7586846040518363ffffffff1660e01b81526004016118d3929190612118565b6020604051808303816000875af11580156118f2573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906119169190612156565b905060008273ffffffffffffffffffffffffffffffffffffffff16632cf5dbda83896040518363ffffffff1660e01b8152600401611955929190612118565b6020604051808303816000875af1158015611974573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906119989190612156565b90508673ffffffffffffffffffffffffffffffffffffffff167fbe28245e56c141dd9dddce73731b090cb4ca241a4d2a22a77ed6425dc5e1b1c68a8a89856040516119e694939291906124d8565b60405180910390a280945050505050949350505050565b6000819050919050565b611a10816119fd565b82525050565b6000602082019050611a2b6000830184611a07565b92915050565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b611a9882611a4f565b810181811067ffffffffffffffff82111715611ab757611ab6611a60565b5b80604052505050565b6000611aca611a31565b9050611ad68282611a8f565b919050565b600067ffffffffffffffff821115611af657611af5611a60565b5b611aff82611a4f565b9050602081019050919050565b82818337600083830152505050565b6000611b2e611b2984611adb565b611ac0565b905082815260208101848484011115611b4a57611b49611a4a565b5b611b55848285611b0c565b509392505050565b600082601f830112611b7257611b71611a45565b5b8135611b82848260208601611b1b565b91505092915050565b611b94816119fd565b8114611b9f57600080fd5b50565b600081359050611bb181611b8b565b92915050565b60008060408385031215611bce57611bcd611a3b565b5b600083013567ffffffffffffffff811115611bec57611beb611a40565b5b611bf885828601611b5d565b9250506020611c0985828601611ba2565b9150509250929050565b600060208284031215611c2957611c28611a3b565b5b6000611c3784828501611ba2565b91505092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000611c6b82611c40565b9050919050565b611c7b81611c60565b8114611c8657600080fd5b50565b600081359050611c9881611c72565b92915050565b600080600060608486031215611cb757611cb6611a3b565b5b600084013567ffffffffffffffff811115611cd557611cd4611a40565b5b611ce186828701611b5d565b9350506020611cf286828701611c89565b9250506040611d0386828701611ba2565b9150509250925092565b600060208284031215611d2357611d22611a3b565b5b600082013567ffffffffffffffff811115611d4157611d40611a40565b5b611d4d84828501611b5d565b91505092915050565b6000819050919050565b6000611d7b611d76611d7184611c40565b611d56565b611c40565b9050919050565b6000611d8d82611d60565b9050919050565b6000611d9f82611d82565b9050919050565b611daf81611d94565b82525050565b6000602082019050611dca6000830184611da6565b92915050565b6000611ddb82611d82565b9050919050565b611deb81611dd0565b82525050565b6000602082019050611e066000830184611de2565b92915050565b600080600060608486031215611e2557611e24611a3b565b5b600084013567ffffffffffffffff811115611e4357611e42611a40565b5b611e4f86828701611b5d565b935050602084013567ffffffffffffffff811115611e7057611e6f611a40565b5b611e7c86828701611b5d565b9250506040611e8d86828701611ba2565b9150509250925092565b60008060408385031215611eae57611ead611a3b565b5b6000611ebc85828601611c89565b9250506020611ecd85828601611ba2565b9150509250929050565b600080600060608486031215611ef057611eef611a3b565b5b600084013567ffffffffffffffff811115611f0e57611f0d611a40565b5b611f1a86828701611b5d565b9350506020611f2b86828701611c89565b9250506040611f3c86828701611c89565b9150509250925092565b60008060008060808587031215611f6057611f5f611a3b565b5b600085013567ffffffffffffffff811115611f7e57611f7d611a40565b5b611f8a87828801611b5d565b945050602085013567ffffffffffffffff811115611fab57611faa611a40565b5b611fb787828801611b5d565b9350506040611fc887828801611c89565b9250506060611fd987828801611ba2565b91505092959194509250565b600081519050919050565b600081905092915050565b60005b83811015612019578082015181840152602081019050611ffe565b60008484015250505050565b600061203082611fe5565b61203a8185611ff0565b935061204a818560208601611ffb565b80840191505092915050565b60006120628284612025565b915081905092915050565b61207681611c60565b82525050565b6000606082019050612091600083018661206d565b61209e602083018561206d565b6120ab6040830184611a07565b949350505050565b60008115159050919050565b6120c8816120b3565b81146120d357600080fd5b50565b6000815190506120e5816120bf565b92915050565b60006020828403121561210157612100611a3b565b5b600061210f848285016120d6565b91505092915050565b600060408201905061212d6000830185611a07565b61213a602083018461206d565b9392505050565b60008151905061215081611b8b565b92915050565b60006020828403121561216c5761216b611a3b565b5b600061217a84828501612141565b91505092915050565b600082825260208201905092915050565b600061219f82611fe5565b6121a98185612183565b93506121b9818560208601611ffb565b6121c281611a4f565b840191505092915050565b600060608201905081810360008301526121e78186612194565b90506121f66020830185611a07565b6122036040830184611a07565b949350505050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000612245826119fd565b9150612250836119fd565b92508282019050808211156122685761226761220b565b5b92915050565b6000604082019050612283600083018561206d565b6122906020830184611a07565b9392505050565b60006020820190506122ac600083018461206d565b92915050565b7f496e73756666696369656e742042455420696e20636f6e747261637400000000600082015250565b60006122e8601c83612183565b91506122f3826122b2565b602082019050919050565b60006020820190508181036000830152612317816122db565b9050919050565b7f5472616e7366657246726f6d206661696c656400000000000000000000000000600082015250565b6000612354601383612183565b915061235f8261231e565b602082019050919050565b6000602082019050818103600083015261238381612347565b9050919050565b600080604083850312156123a1576123a0611a3b565b5b60006123af85828601612141565b92505060206123c085828601612141565b9150509250929050565b7f4e6f7420696e697469616c697a65640000000000000000000000000000000000600082015250565b6000612400600f83612183565b915061240b826123ca565b602082019050919050565b6000602082019050818103600083015261242f816123f3565b9050919050565b6000612441826119fd565b915061244c836119fd565b925082820261245a816119fd565b915082820484148315176124715761247061220b565b5b5092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601260045260246000fd5b60006124b2826119fd565b91506124bd836119fd565b9250826124cd576124cc612478565b5b828204905092915050565b600060808201905081810360008301526124f28187612194565b905081810360208301526125068186612194565b90506125156040830185611a07565b6125226060830184611a07565b9594505050505056fea26469706673582212205c433088dfe77935e74e5a4cef2602f708284ec54bcbea453ef95757d840c0e864736f6c634300081c0033\r\n";

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
