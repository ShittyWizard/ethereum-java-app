package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        LOG.debug("Try to start Spring boot application...");
        SpringApplication.run(Application.class, args);
        LOG.debug("Successful start of spring boot application...");
    }
        /*
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

         */
}
