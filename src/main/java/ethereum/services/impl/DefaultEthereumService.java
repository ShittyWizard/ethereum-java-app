package ethereum.services.impl;

import java.io.IOException;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import contracts.generated.KycContract;
import ethereum.services.EthereumService;
import io.reactivex.disposables.Disposable;

import static org.web3j.protocol.core.DefaultBlockParameterName.EARLIEST;
import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

@Service
public class DefaultEthereumService implements EthereumService, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultEthereumService.class);

    @Value("${eth.ganache.local.url}")
    private static String ganacheLocalHttpUrl;

    private Web3j web3;

    @Override
    public Credentials getCredentialsByPrivateKey(String privateKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("Private key should not be null!");
        }
        return Credentials.create(privateKey);
    }

    @Override
    public BigDecimal getEthBalance(String publicKey, Convert.Unit unit)
            throws IOException {
        EthGetBalance ethBalance = web3.ethGetBalance(publicKey, LATEST).send();

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
    public boolean storeHashOfFile(String hashOfFile, String contractAddress, String privateKey)
            throws Exception {
        Credentials credentials = Credentials.create(privateKey);
        KycContract kycContract = KycContract.load(contractAddress, web3, credentials, new DefaultGasProvider());
        kycContract.addFileToMyStore(hashOfFile);

        System.out.println("Hashes size " + kycContract.getFilesByOwner().send().size());
//        fixme
        if (kycContract.getTransactionReceipt().isPresent()) {
            System.out.println("Events size after insert new hash " + kycContract.getKycStatusChangeEvents(kycContract.getTransactionReceipt().get()).size());
        }
        return true;
    }

    @Override
    public void getHashes(String contractAddress, String privateKey)
            throws Exception {
        Credentials credentials = Credentials.create(privateKey);
        KycContract kycContract = KycContract.load(contractAddress, web3, credentials, new DefaultGasProvider());
        final EthFilter ethFilter = new EthFilter(EARLIEST, LATEST, kycContract.getContractAddress().substring(2));

        ethFilter.addSingleTopic(EventEncoder.encode(KycContract.KYCSTATUSCHANGE_EVENT));
        Disposable subscribe = kycContract.kycStatusChangeEventFlowable(ethFilter)
                                          .subscribe(event -> {
                                              final String owner = event.owner;
                                              System.out.println("Owner : " + owner);
                                              LOG.info("Owner of file: {}", owner);
                                          });
        System.out.println("Hashes size " + kycContract.getFilesByOwner().send().size());
//        fixme
        if (kycContract.getTransactionReceipt().isPresent()) {
            System.out.println("Events size after getHashes " + kycContract.getKycStatusChangeEvents(kycContract.getTransactionReceipt().get()).size());
        }
    }

    @Override
    public String deployKycContract(String privateKey)
            throws Exception {
        Credentials credentials = Credentials.create(privateKey);
        KycContract kycContract = KycContract.deploy(web3, credentials, new DefaultGasProvider()).send();

        if (kycContract.getTransactionReceipt().isPresent()) {
            System.out.println("Events size after deploy " + kycContract.getKycStatusChangeEvents(kycContract.getTransactionReceipt().get()).size());
            kycContract.getKycStatusChangeEvents(kycContract.getTransactionReceipt().get()).forEach(event -> {
                System.out.println("event owner " + event.owner);
                System.out.println("event hash " + kycContract.getHash(event.hash));
            });
        }
        return kycContract.getContractAddress();
    }


    @Override
    public void afterPropertiesSet() {
        LOG.info("Connecting to Web3 ganache test network...");
//        web3 = Web3j.build(new HttpService(ganacheLocalHttpUrl));
        web3 = Web3j.build(new HttpService("http://localhost:8545"));
        LOG.info("Successfully connected to Ethereum! Address {}", "http://localhost:8545");
    }
}
