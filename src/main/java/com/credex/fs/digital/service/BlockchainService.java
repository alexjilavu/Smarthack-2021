package com.credex.fs.digital.service;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.http.HttpService;

@Service
@Slf4j
public class BlockchainService {

    public void test() throws ExecutionException, InterruptedException {
        Web3j web3 = Web3j.build(new HttpService("http://192.168.100.15:7545"));
        Credentials credentials = Credentials.create("5c9b465dbee146b52a968590f2e4ac04f06e37b7e44ef7fd3369d3702bfd659c");

        EthBlockNumber result = web3.ethBlockNumber().sendAsync().get();
        EthAccounts ethAccounts = web3.ethAccounts().sendAsync().get();

        ethAccounts.getAccounts().forEach(log::info);
        //log.info(" The Block Number is: " + result.getBlockNumber().toString());
    }

    public void burn(String walletAddress, BigInteger valueOf) {}
}
