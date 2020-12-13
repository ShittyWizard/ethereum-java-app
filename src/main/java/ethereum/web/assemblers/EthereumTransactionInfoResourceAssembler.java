package ethereum.web.assemblers;

import ethereum.web.dto.EthereumTransactionInfoResource;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.ArrayList;
import java.util.List;

@Component
public class EthereumTransactionInfoResourceAssembler {
    public EthereumTransactionInfoResource toResource(TransactionReceipt transaction, String ipfsFileHash, String sha256Hash) {
        EthereumTransactionInfoResource resource = new EthereumTransactionInfoResource();
        if (transaction != null) {
            resource.setIpfsFileHash(ipfsFileHash);
            resource.setBlockNumber(transaction.getBlockNumber().toString());
            resource.setBlockHash(transaction.getBlockHash());
            resource.setContractAddress(transaction.getContractAddress());
            resource.setTransactionIndex(transaction.getTransactionIndexRaw());
            resource.setTransactionHash(transaction.getTransactionHash());
            resource.setFrom(transaction.getFrom());
            resource.setTo(transaction.getTo());
            resource.setStatus(transaction.getStatus());
            resource.setRoot(transaction.getRoot());
            resource.setLogs(getStringLogs(transaction.getLogs()));
            resource.setSha256Hash(sha256Hash);
        }
        return resource;
    }

    private List<String> getStringLogs(List<Log> logs) {
        List<String> resultList = new ArrayList<>();
        logs.forEach(log -> {
            resultList.add(
                    String.format("LOG %s: TYPE %s; DATA: %s", log.getLogIndexRaw(), log.getType(), log.getData())
            );
        });
        return resultList;
    }
}
