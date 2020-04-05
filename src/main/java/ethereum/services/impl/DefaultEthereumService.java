package ethereum.services.impl;

import java.io.IOException;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import ethereum.services.EthereumService;

@Service
public class DefaultEthereumService implements EthereumService, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultEthereumService.class);

    @Value("${eth.ganache.local.url}")
    private static String ganacheLocalHttpUrl;

    private Web3j web3;

    @Override
    public Credentials getCredentialsByPrivateKey(String privateKey) {
        return Credentials.create(privateKey);
    }

    @Override
    public BigDecimal getEthBalance(String publicKey, Convert.Unit unit)
            throws IOException {
        EthGetBalance ethBalance = web3.ethGetBalance(publicKey, DefaultBlockParameterName.LATEST).send();

        return Convert.fromWei(ethBalance.getBalance().toString(), unit);
    }

    @Override
    public TransactionReceipt sendFunds(
            BigDecimal value, Convert.Unit unit, String publicKeyOfRecipient, Credentials credentials
    )
            throws Exception {
        return Transfer.sendFunds(web3, credentials, publicKeyOfRecipient, value, unit).send();
    }

    @Override
    public void afterPropertiesSet() {
        LOG.info("Connecting to Web3 ganache test network...");
        web3 = Web3j.build(new HttpService(ganacheLocalHttpUrl));
        LOG.info("Successfully connected to Ethereum! Address {}", ganacheLocalHttpUrl);
    }
}
