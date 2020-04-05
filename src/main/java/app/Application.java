package app;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import contracts.generated.DocumentRegistry;

import static org.web3j.protocol.core.DefaultBlockParameterName.EARLIEST;
import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;
import static org.web3j.utils.Convert.Unit.ETHER;

@SpringBootApplication
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Value("${eth.ganache.local.url}")
    private static String ganacheLocalHttpUrl;
    @Value("${eth.first.account.public.key}")
    private static String firstAccountPublicKey;
    @Value("${eth.second.account.public.key}")
    private static String secondAccountPublicKey;

    private static final String password = null;
    @Value("${eth.first.account.mnemonic.phrase}")
    private static String firstAccountMnemonicPhrase;
    @Value("${eth.first.account.private.key}")
    private static String firstAccountPrivateKey;

    private static final String documentHash = "QmXoypizjW3WknFiJnKLwHCnL72vedxj";

    public static void main(String[] args)
            throws Exception {
        LOG.info("Connecting to Web3 ganache test network...");
        Web3j web3 = Web3j.build(new HttpService(ganacheLocalHttpUrl));
        LOG.info("Successfully connected to Ethereum! Address {}", ganacheLocalHttpUrl);

        try {
            LOG.info("Try to get client from web3 ganache test network...");
            Web3ClientVersion client = web3.web3ClientVersion().send();
            EthBlockNumber blockNumber = web3.ethBlockNumber().send();
            EthGasPrice gasPrice = web3.ethGasPrice().send();

            EthGetBalance balance = web3.ethGetBalance(firstAccountPublicKey, LATEST).send();
            BigDecimal balanceInEth = Convert.fromWei(balance.getBalance().toString(), ETHER);
            EthGetTransactionCount transactionCount = web3.ethGetTransactionCount(firstAccountPublicKey, LATEST).send();

            LOG.info("Client version: {}", client.getWeb3ClientVersion());
            LOG.info("Ethereum block number: {}", blockNumber.getBlockNumber());
            LOG.info("Gas price: {}", gasPrice.getGasPrice());
            LOG.info("Balance of metamask wallet: {}", balanceInEth);
            LOG.info("Transaction count: {}", transactionCount.getTransactionCount());
        } catch (IOException e) {
            LOG.info("Something went wrong: {}", e.getMessage());
            e.printStackTrace();
        }

//        Credentials credentials = WalletUtils.loadBip39Credentials(password, firstAccountMnemonicPhrase);
        Credentials credentials = Credentials.create(firstAccountPrivateKey);
        LOG.info("Address of first account: {}", credentials.getAddress());

        TransactionReceipt receipt = Transfer.sendFunds(web3, credentials, secondAccountPublicKey, BigDecimal.valueOf(1), ETHER).send();
        System.out.println("Transaction " + receipt.getTransactionHash() + " was mined in block # " + receipt.getBlockNumber());
        System.out.println("Balance: " + Convert.fromWei(web3.ethGetBalance(credentials.getAddress(), LATEST).send().getBalance().toString(), ETHER));

        DocumentRegistry documentRegistry = DocumentRegistry.load(credentials.getAddress(), web3, credentials, new DefaultGasProvider());

        final EthFilter ethFilter = new EthFilter(EARLIEST, LATEST,
                                                  documentRegistry.getContractAddress());

        ethFilter.addSingleTopic(EventEncoder.encode(DocumentRegistry.NOTARIZED_EVENT));
        ethFilter.addOptionalTopics("0x" + TypeEncoder.encode(new Address("0x00a329c0648769a73afac7f9381e08fb43dbea72")));

        documentRegistry
                .notarizedEventFlowable(ethFilter)
                .subscribe(event -> {
                    final String notary = event._signer;
                    final String documentHash = Arrays.toString(event._documentHash);

                    LOG.info("Notary: {}", notary);
                    //Perform processing based on event values
                });

        TransactionReceipt txReceipt = documentRegistry.notarizeDocument(stringToBytes32(documentHash)).send();

        String txHash = txReceipt.getTransactionHash();
        LOG.info("Transaction hash of documentRegistry: {}", txHash);
    }

    public static byte[] stringToBytes32(String string) {
        byte[] byteValue = string.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return byteValueLen32;
    }
}
