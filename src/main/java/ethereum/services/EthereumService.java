package ethereum.services;

import java.io.IOException;
import java.math.BigDecimal;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

public interface EthereumService {
    public Credentials getCredentialsByPrivateKey(String privateKey);
    public BigDecimal getEthBalance(String publicKey, Convert.Unit unit)
            throws IOException;
    public TransactionReceipt sendFunds(BigDecimal value, Convert.Unit unit, String publicKeyOfRecipient, Credentials credentials)
            throws Exception;
    public boolean storeHashOfFile(String hashOfFile, String contractAddress, String privateKey)
            throws Exception;
    public void getHashes(String contractAddress, String privateKey)
            throws Exception;
    public String deployKycContract(String privateKey)
            throws Exception;
}
