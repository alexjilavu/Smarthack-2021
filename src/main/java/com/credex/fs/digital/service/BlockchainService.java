package com.credex.fs.digital.service;

import com.credex.fs.digital.util.FantasticDeeds;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.contracts.token.ERC20Interface;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

@Service
@Slf4j
public class BlockchainService {

    public Web3j web3j;

    private static final String NETWORK_URL = "http://192.168.100.15:7545";

    private static final String CONTRACT_ADDRESS = "0xD57Db7C6419851979e77D4F4b55189C6Da3ccE98";

    private static final String BURN_WALLET_ADDRESS = "0x949d352fc92E611A57d2234eEd5CdDBD89F07a88";

    private static final String MONEY_WALLET_ADDRESS = "0x14dFBE129740F5B09D8bde8bd4bbee392Cf25c96";

    private static final BigInteger GAS_PRICE = BigInteger.valueOf(0);

    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(77856);

    public BlockchainService() {
        this.web3j = Web3j.build(new HttpService(NETWORK_URL));
    }

    public void burn(String senderAddr, Long amount) throws ExecutionException, InterruptedException {
        Credentials credentials = Credentials.create(senderAddr);
        FantasticDeeds fantasticDeeds = new FantasticDeeds(CONTRACT_ADDRESS, web3j, credentials, GAS_PRICE, GAS_LIMIT);

        fantasticDeeds.approve(BURN_WALLET_ADDRESS, BigInteger.valueOf(amount));
        fantasticDeeds.transfer(BURN_WALLET_ADDRESS, BigInteger.valueOf(amount)).sendAsync().get();
    }

    public void addTokens(String receiptAddr, Long amount) throws ExecutionException, InterruptedException {
        Credentials credentials = Credentials.create(MONEY_WALLET_ADDRESS);
        FantasticDeeds fantasticDeeds = new FantasticDeeds(CONTRACT_ADDRESS, web3j, credentials, GAS_PRICE, GAS_LIMIT);

        fantasticDeeds.approve(receiptAddr, BigInteger.valueOf(amount));
        fantasticDeeds.transfer(receiptAddr, BigInteger.valueOf(amount)).sendAsync().get();
    }

    public String balanceOf(String privateKey) throws ExecutionException, InterruptedException {
        Credentials credentials = Credentials.create(privateKey);
        FantasticDeeds fantasticDeeds = new FantasticDeeds(CONTRACT_ADDRESS, web3j, credentials, GAS_PRICE, GAS_LIMIT);

        BigInteger ethGetBalance = fantasticDeeds.balanceOf(credentials.getAddress()).sendAsync().get();
        return stringCut(ethGetBalance.toString());
    }

    private String stringCut(String str) {
        if (str != null && str.length() > 10) {
            str = str.substring(0, str.length() - 18);
        }
        return str;
    }
}
