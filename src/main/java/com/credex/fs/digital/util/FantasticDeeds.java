package com.credex.fs.digital.util;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
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
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class FantasticDeeds extends Contract {

    public static final String BINARY =
        "0x60806040523480156200001157600080fd5b5060405162001ab538038062001ab583398181016040528101906200003791906200036d565b6040518060400160405280600e81526020017f46616e74617374696344656564730000000000000000000000000000000000008152506040518060400160405280600481526020017f44656564000000000000000000000000000000000000000000000000000000008152508160039080519060200190620000bb929190620002a6565b508060049080519060200190620000d4929190620002a6565b5050506200011333620000ec6200011a60201b60201c565b600a620000fa9190620004f4565b8362000107919062000631565b6200012360201b60201c565b5062000764565b60006012905090565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16141562000196576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016200018d90620003ec565b60405180910390fd5b620001aa600083836200029c60201b60201c565b8060026000828254620001be91906200043c565b92505081905550806000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282546200021591906200043c565b925050819055508173ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040516200027c91906200040e565b60405180910390a36200029860008383620002a160201b60201c565b5050565b505050565b505050565b828054620002b490620006a9565b90600052602060002090601f016020900481019282620002d8576000855562000324565b82601f10620002f357805160ff191683800117855562000324565b8280016001018555821562000324579182015b828111156200032357825182559160200191906001019062000306565b5b50905062000333919062000337565b5090565b5b808211156200035257600081600090555060010162000338565b5090565b60008151905062000367816200074a565b92915050565b6000602082840312156200038057600080fd5b6000620003908482850162000356565b91505092915050565b6000620003a8601f836200042b565b91507f45524332303a206d696e7420746f20746865207a65726f2061646472657373006000830152602082019050919050565b620003e68162000692565b82525050565b60006020820190508181036000830152620004078162000399565b9050919050565b6000602082019050620004256000830184620003db565b92915050565b600082825260208201905092915050565b6000620004498262000692565b9150620004568362000692565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff038211156200048e576200048d620006df565b5b828201905092915050565b6000808291508390505b6001851115620004eb57808604811115620004c357620004c2620006df565b5b6001851615620004d35780820291505b8081029050620004e3856200073d565b9450620004a3565b94509492505050565b6000620005018262000692565b91506200050e836200069c565b92506200053d7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff848462000545565b905092915050565b6000826200055757600190506200062a565b816200056757600090506200062a565b81600181146200058057600281146200058b57620005c1565b60019150506200062a565b60ff841115620005a0576200059f620006df565b5b8360020a915084821115620005ba57620005b9620006df565b5b506200062a565b5060208310610133831016604e8410600b8410161715620005fb5782820a905083811115620005f557620005f4620006df565b5b6200062a565b6200060a848484600162000499565b92509050818404811115620006245762000623620006df565b5b81810290505b9392505050565b60006200063e8262000692565b91506200064b8362000692565b9250817fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0483118215151615620006875762000686620006df565b5b828202905092915050565b6000819050919050565b600060ff82169050919050565b60006002820490506001821680620006c257607f821691505b60208210811415620006d957620006d86200070e565b5b50919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b60008160011c9050919050565b620007558162000692565b81146200076157600080fd5b50565b61134180620007746000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c80633950935111610071578063395093511461016857806370a082311461019857806395d89b41146101c8578063a457c2d7146101e6578063a9059cbb14610216578063dd62ed3e14610246576100a9565b806306fdde03146100ae578063095ea7b3146100cc57806318160ddd146100fc57806323b872dd1461011a578063313ce5671461014a575b600080fd5b6100b6610276565b6040516100c3919061100a565b60405180910390f35b6100e660048036038101906100e19190610c83565b610308565b6040516100f39190610fef565b60405180910390f35b610104610326565b604051610111919061110c565b60405180910390f35b610134600480360381019061012f9190610c34565b610330565b6040516101419190610fef565b60405180910390f35b610152610428565b60405161015f9190611127565b60405180910390f35b610182600480360381019061017d9190610c83565b610431565b60405161018f9190610fef565b60405180910390f35b6101b260048036038101906101ad9190610bcf565b6104dd565b6040516101bf919061110c565b60405180910390f35b6101d0610525565b6040516101dd919061100a565b60405180910390f35b61020060048036038101906101fb9190610c83565b6105b7565b60405161020d9190610fef565b60405180910390f35b610230600480360381019061022b9190610c83565b6106a2565b60405161023d9190610fef565b60405180910390f35b610260600480360381019061025b9190610bf8565b6106c0565b60405161026d919061110c565b60405180910390f35b6060600380546102859061123c565b80601f01602080910402602001604051908101604052809291908181526020018280546102b19061123c565b80156102fe5780601f106102d3576101008083540402835291602001916102fe565b820191906000526020600020905b8154815290600101906020018083116102e157829003601f168201915b5050505050905090565b600061031c610315610747565b848461074f565b6001905092915050565b6000600254905090565b600061033d84848461091a565b6000600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000610388610747565b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905082811015610408576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016103ff9061108c565b60405180910390fd5b61041c85610414610747565b85840361074f565b60019150509392505050565b60006012905090565b60006104d361043e610747565b84846001600061044c610747565b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546104ce919061115e565b61074f565b6001905092915050565b60008060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b6060600480546105349061123c565b80601f01602080910402602001604051908101604052809291908181526020018280546105609061123c565b80156105ad5780601f10610582576101008083540402835291602001916105ad565b820191906000526020600020905b81548152906001019060200180831161059057829003601f168201915b5050505050905090565b600080600160006105c6610747565b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905082811015610683576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161067a906110ec565b60405180910390fd5b61069761068e610747565b8585840361074f565b600191505092915050565b60006106b66106af610747565b848461091a565b6001905092915050565b6000600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b600033905090565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1614156107bf576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016107b6906110cc565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16141561082f576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108269061104c565b60405180910390fd5b80600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9258360405161090d919061110c565b60405180910390a3505050565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16141561098a576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610981906110ac565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156109fa576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016109f19061102c565b60405180910390fd5b610a05838383610b9b565b60008060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905081811015610a8b576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610a829061106c565b60405180910390fd5b8181036000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550816000808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254610b1e919061115e565b925050819055508273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef84604051610b82919061110c565b60405180910390a3610b95848484610ba0565b50505050565b505050565b505050565b600081359050610bb4816112dd565b92915050565b600081359050610bc9816112f4565b92915050565b600060208284031215610be157600080fd5b6000610bef84828501610ba5565b91505092915050565b60008060408385031215610c0b57600080fd5b6000610c1985828601610ba5565b9250506020610c2a85828601610ba5565b9150509250929050565b600080600060608486031215610c4957600080fd5b6000610c5786828701610ba5565b9350506020610c6886828701610ba5565b9250506040610c7986828701610bba565b9150509250925092565b60008060408385031215610c9657600080fd5b6000610ca485828601610ba5565b9250506020610cb585828601610bba565b9150509250929050565b610cc8816111c6565b82525050565b6000610cd982611142565b610ce3818561114d565b9350610cf3818560208601611209565b610cfc816112cc565b840191505092915050565b6000610d1460238361114d565b91507f45524332303a207472616e7366657220746f20746865207a65726f206164647260008301527f65737300000000000000000000000000000000000000000000000000000000006020830152604082019050919050565b6000610d7a60228361114d565b91507f45524332303a20617070726f766520746f20746865207a65726f20616464726560008301527f73730000000000000000000000000000000000000000000000000000000000006020830152604082019050919050565b6000610de060268361114d565b91507f45524332303a207472616e7366657220616d6f756e742065786365656473206260008301527f616c616e636500000000000000000000000000000000000000000000000000006020830152604082019050919050565b6000610e4660288361114d565b91507f45524332303a207472616e7366657220616d6f756e742065786365656473206160008301527f6c6c6f77616e63650000000000000000000000000000000000000000000000006020830152604082019050919050565b6000610eac60258361114d565b91507f45524332303a207472616e736665722066726f6d20746865207a65726f20616460008301527f64726573730000000000000000000000000000000000000000000000000000006020830152604082019050919050565b6000610f1260248361114d565b91507f45524332303a20617070726f76652066726f6d20746865207a65726f2061646460008301527f72657373000000000000000000000000000000000000000000000000000000006020830152604082019050919050565b6000610f7860258361114d565b91507f45524332303a2064656372656173656420616c6c6f77616e63652062656c6f7760008301527f207a65726f0000000000000000000000000000000000000000000000000000006020830152604082019050919050565b610fda816111f2565b82525050565b610fe9816111fc565b82525050565b60006020820190506110046000830184610cbf565b92915050565b600060208201905081810360008301526110248184610cce565b905092915050565b6000602082019050818103600083015261104581610d07565b9050919050565b6000602082019050818103600083015261106581610d6d565b9050919050565b6000602082019050818103600083015261108581610dd3565b9050919050565b600060208201905081810360008301526110a581610e39565b9050919050565b600060208201905081810360008301526110c581610e9f565b9050919050565b600060208201905081810360008301526110e581610f05565b9050919050565b6000602082019050818103600083015261110581610f6b565b9050919050565b60006020820190506111216000830184610fd1565b92915050565b600060208201905061113c6000830184610fe0565b92915050565b600081519050919050565b600082825260208201905092915050565b6000611169826111f2565b9150611174836111f2565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff038211156111a9576111a861126e565b5b828201905092915050565b60006111bf826111d2565b9050919050565b60008115159050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b600060ff82169050919050565b60005b8381101561122757808201518184015260208101905061120c565b83811115611236576000848401525b50505050565b6000600282049050600182168061125457607f821691505b602082108114156112685761126761129d565b5b50919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b6000601f19601f8301169050919050565b6112e6816111b4565b81146112f157600080fd5b50565b6112fd816111f2565b811461130857600080fd5b5056fea2646970667358221220069de7ee19e7893398dd911e219200d9f4f1b97664d7d19807d0d0cd6fc64a4c64736f6c63430008000033";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_DECREASEALLOWANCE = "decreaseAllowance";

    public static final String FUNC_INCREASEALLOWANCE = "increaseAllowance";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final Event APPROVAL_EVENT = new Event(
        "Approval",
        Arrays.<TypeReference<?>>asList(
            new TypeReference<Address>(true) {},
            new TypeReference<Address>(true) {},
            new TypeReference<Uint256>() {}
        )
    );

    public static final Event TRANSFER_EVENT = new Event(
        "Transfer",
        Arrays.<TypeReference<?>>asList(
            new TypeReference<Address>(true) {},
            new TypeReference<Address>(true) {},
            new TypeReference<Uint256>() {}
        )
    );

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("5777", "0xD57Db7C6419851979e77D4F4b55189C6Da3ccE98");
    }

    @Deprecated
    protected FantasticDeeds(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FantasticDeeds(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FantasticDeeds(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit
    ) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FantasticDeeds(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        ContractGasProvider contractGasProvider
    ) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
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

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j
            .ethLogFlowable(filter)
            .map(
                new Function<Log, ApprovalEventResponse>() {
                    @Override
                    public ApprovalEventResponse apply(Log log) {
                        Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                        ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                        typedResponse.log = log;
                        typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                        typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                        return typedResponse;
                    }
                }
            );
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
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

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j
            .ethLogFlowable(filter)
            .map(
                new Function<Log, TransferEventResponse>() {
                    @Override
                    public TransferEventResponse apply(Log log) {
                        Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                        TransferEventResponse typedResponse = new TransferEventResponse();
                        typedResponse.log = log;
                        typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                        typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                        return typedResponse;
                    }
                }
            );
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> allowance(String owner, String spender) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
            FUNC_ALLOWANCE,
            Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner), new org.web3j.abi.datatypes.Address(spender)),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {})
        );
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String spender, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
            FUNC_APPROVE,
            Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(spender), new org.web3j.abi.datatypes.generated.Uint256(amount)),
            Collections.<TypeReference<?>>emptyList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
            FUNC_BALANCEOF,
            Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account)),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {})
        );
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
            FUNC_DECIMALS,
            Arrays.<Type>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {})
        );
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> decreaseAllowance(String spender, BigInteger subtractedValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
            FUNC_DECREASEALLOWANCE,
            Arrays.<Type>asList(
                new org.web3j.abi.datatypes.Address(spender),
                new org.web3j.abi.datatypes.generated.Uint256(subtractedValue)
            ),
            Collections.<TypeReference<?>>emptyList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> increaseAllowance(String spender, BigInteger addedValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
            FUNC_INCREASEALLOWANCE,
            Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(spender), new org.web3j.abi.datatypes.generated.Uint256(addedValue)),
            Collections.<TypeReference<?>>emptyList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> name() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
            FUNC_NAME,
            Arrays.<Type>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {})
        );
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> symbol() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
            FUNC_SYMBOL,
            Arrays.<Type>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {})
        );
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> totalSupply() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
            FUNC_TOTALSUPPLY,
            Arrays.<Type>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {})
        );
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String recipient, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
            FUNC_TRANSFER,
            Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(recipient), new org.web3j.abi.datatypes.generated.Uint256(amount)),
            Collections.<TypeReference<?>>emptyList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String sender, String recipient, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
            FUNC_TRANSFERFROM,
            Arrays.<Type>asList(
                new org.web3j.abi.datatypes.Address(sender),
                new org.web3j.abi.datatypes.Address(recipient),
                new org.web3j.abi.datatypes.generated.Uint256(amount)
            ),
            Collections.<TypeReference<?>>emptyList()
        );
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static FantasticDeeds load(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit
    ) {
        return new FantasticDeeds(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FantasticDeeds load(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit
    ) {
        return new FantasticDeeds(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FantasticDeeds load(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        ContractGasProvider contractGasProvider
    ) {
        return new FantasticDeeds(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FantasticDeeds load(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        ContractGasProvider contractGasProvider
    ) {
        return new FantasticDeeds(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<FantasticDeeds> deploy(
        Web3j web3j,
        Credentials credentials,
        ContractGasProvider contractGasProvider,
        BigInteger _supply
    ) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(
            Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_supply))
        );
        return deployRemoteCall(FantasticDeeds.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<FantasticDeeds> deploy(
        Web3j web3j,
        TransactionManager transactionManager,
        ContractGasProvider contractGasProvider,
        BigInteger _supply
    ) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(
            Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_supply))
        );
        return deployRemoteCall(FantasticDeeds.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<FantasticDeeds> deploy(
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger _supply
    ) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(
            Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_supply))
        );
        return deployRemoteCall(FantasticDeeds.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<FantasticDeeds> deploy(
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger _supply
    ) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(
            Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_supply))
        );
        return deployRemoteCall(FantasticDeeds.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
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
