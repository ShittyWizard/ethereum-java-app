package ethereum.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import contracts.generated.FileStorageContract;

public interface EthereumService {
    public Credentials getCredentialsByPrivateKey(String privateKey);
    public BigDecimal getEthBalance(String publicKey, Convert.Unit unit)
            throws IOException;
    public TransactionReceipt sendFunds(BigDecimal value, Convert.Unit unit, String publicKeyOfRecipient, Credentials credentials)
            throws Exception;
    public String storeHashOfFile(String hashOfFile, String contractAddress, String privateKey)
            throws Exception;
    public List<String> getHashes(String contractAddress, String privateKey)
            throws Exception;
    public List<FileStorageContract.InitFileStoreEventResponse> getInitFileStoreEvents(String contractAddress, String privateKey);
    public String deployFileStorageContract(String privateKey)
            throws Exception;
}
