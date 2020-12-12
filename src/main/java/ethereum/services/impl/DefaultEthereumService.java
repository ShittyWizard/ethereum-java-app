package ethereum.services.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

import contracts.generated.FileStorageContract;
import ethereum.services.EthereumService;

import static org.web3j.protocol.core.DefaultBlockParameterName.EARLIEST;
import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

// todo: split into 2 services (EthFileStorageService and EthGeneralService)
@Service
public class DefaultEthereumService implements EthereumService, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultEthereumService.class);

//    @Value("${eth.ganache.local.url}")
    private String ganacheLocalHttpUrl = "http://localhost:8545";

    private Web3j web3;

    @Override
    public void afterPropertiesSet() {
        LOG.info("Connecting to Web3 ganache test network...");
        web3 = Web3j.build(new HttpService(ganacheLocalHttpUrl));
        LOG.info("Successfully connected to Ethereum! Address {}", "http://localhost:8545");
    }

    @Override
    public Credentials getCredentialsByPrivateKey(String privateKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("Private key should not be null!");
        }
        return Credentials.create(privateKey);
    }

    @Override
    public String storeHashOfFile(String hashOfFile, String contractAddress, String privateKey)
            throws Exception {
        Credentials credentials = Credentials.create(privateKey);
        FileStorageContract fileStorageContract = FileStorageContract.load(contractAddress, web3, credentials, new DefaultGasProvider());
        TransactionReceipt txStore = fileStorageContract.initFileStore(hashOfFile).send();
        String txHash = txStore.getTransactionHash();
        System.out.println("Transaction hash " + txHash);

        return txHash;
    }

    @Override
    public TransactionReceipt storeHashOfFileWithTransactionInfo(String hashOfFile, String contractAddress, String privateKey) throws Exception {
        Credentials credentials = Credentials.create(privateKey);
        FileStorageContract fileStorageContract = FileStorageContract.load(contractAddress, web3, credentials, new DefaultGasProvider());
        TransactionReceipt txStore = fileStorageContract.initFileStore(hashOfFile).send();
        return txStore;
    }

    @Override
    public String changeFileOwner(String hashOfFile, String sendToAddress, String contractAddress, String privateKey)
            throws Exception {
        TransactionReceipt txChange = storeHashOfFileWithTransactionInfo(hashOfFile, contractAddress, privateKey);
        String txHash = txChange.getTransactionHash();
        System.out.println("Transaction hash " + txHash);

        return txHash;
    }

    @Override
    public List<String> getHashes(String contractAddress, String privateKey)
            throws Exception {
        Credentials credentials = Credentials.create(privateKey);
        FileStorageContract fileStorageContract = FileStorageContract.load(contractAddress, web3, credentials, new DefaultGasProvider());
        return (List<String>) fileStorageContract.getFileStore().send();
    }

    @Override
    public List<FileStorageContract.InitFileStoreEventResponse> getInitFileStoreEvents(String contractAddress, String privateKey) {
        Credentials credentials = Credentials.create(privateKey);
        FileStorageContract fileStorageContract = FileStorageContract.load(contractAddress, web3, credentials, new DefaultGasProvider());
        final EthFilter ethFilter = new EthFilter(EARLIEST, LATEST, fileStorageContract.getContractAddress());

        ethFilter.addSingleTopic(EventEncoder.encode(FileStorageContract.INITFILESTORE_EVENT));
        List<FileStorageContract.InitFileStoreEventResponse> responses = new ArrayList<>();
        fileStorageContract.initFileStoreEventFlowable(ethFilter)
                           .subscribe(responses::add);

        return responses;
    }

    @Override
    public List<FileStorageContract.ChangeFileOwnerEventResponse> getChangeFileOwnerEvents(String contractAddress, String privateKey) {
        Credentials credentials = Credentials.create(privateKey);
        FileStorageContract fileStorageContract = FileStorageContract.load(contractAddress, web3, credentials, new DefaultGasProvider());
        final EthFilter ethFilter = new EthFilter(EARLIEST, LATEST, fileStorageContract.getContractAddress());

        ethFilter.addSingleTopic(EventEncoder.encode(FileStorageContract.CHANGEFILEOWNER_EVENT));
        List<FileStorageContract.ChangeFileOwnerEventResponse> responses = new ArrayList<>();
        fileStorageContract.changeFileOwnerEventFlowable(ethFilter)
                           .subscribe(responses::add);

        return responses;
    }

    @Override
    public String deployFileStorageContract(String privateKey)
            throws Exception {
        Credentials credentials = Credentials.create(privateKey);
        FileStorageContract fileStorageContract = FileStorageContract.deploy(web3, credentials, new DefaultGasProvider()).send();
        if (fileStorageContract.getTransactionReceipt().isPresent()) {
            LOG.info("Transaction successful!");
        }

        return fileStorageContract.getContractAddress();
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
}
