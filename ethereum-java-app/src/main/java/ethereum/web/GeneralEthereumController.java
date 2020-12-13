package ethereum.web;

import java.util.List;
import java.util.stream.Collectors;

import ethereum.web.assemblers.EthereumTransactionInfoResourceAssembler;
import ethereum.web.dto.EthereumTransactionInfoResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ethereum.services.EthereumService;
import ethereum.web.assemblers.ChangeFileOwnerEventResourceAssembler;
import ethereum.web.assemblers.InitFileStoreEventResourceAssembler;
import ethereum.web.dto.ChangeFileOwnerEventResource;
import ethereum.web.dto.InitFileStoreEventResource;
import ipfs.services.IPFSService;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@RestController
@RequestMapping(value = "/ethereum/filestore", produces = MediaType.APPLICATION_JSON_VALUE)
public class GeneralEthereumController {
    @Autowired
    private EthereumService ethereumService;
    @Autowired
    private IPFSService ipfsService;
    @Autowired
    private InitFileStoreEventResourceAssembler initFileStoreEventResourceAssembler;
    @Autowired
    private ChangeFileOwnerEventResourceAssembler changeFileOwnerEventResourceAssembler;
    @Autowired
    private EthereumTransactionInfoResourceAssembler ethereumTransactionInfoResourceAssembler;

    protected static String FILE_STORAGE_CONTRACT_ADDRESS;

    @PostMapping("/init/info")
    public EthereumTransactionInfoResource storeFileInfo(
            @RequestBody
                    MultipartFile file,
            @RequestParam
                    String contractAddress,
            @RequestParam
                    String privateKey
    )
            throws Exception {
        String hashOfFile = ipfsService.uploadMultipartFile(file);
        TransactionReceipt transactionReceipt = ethereumService.storeHashOfFileWithTransactionInfo(hashOfFile, contractAddress, privateKey);
        return ethereumTransactionInfoResourceAssembler.toResource(transactionReceipt, hashOfFile);
    }

    @PostMapping("/init")
    public String storeFile(
            @RequestBody
                    MultipartFile file,
            @RequestParam
                    String contractAddress,
            @RequestParam
                    String privateKey
    )
            throws Exception {
        String hashOfFile = ipfsService.uploadMultipartFile(file);
        return ethereumService.storeHashOfFile(hashOfFile, contractAddress, privateKey);
    }

    @PostMapping("/changeOwner")
    public String changeFileOwner(
            @RequestParam
                    String hashOfFile,
            @RequestParam
                    String sendToAddress,
            @RequestParam
                    String contractAddress,
            @RequestParam
                    String privateKey
    )
            throws Exception {
        return ethereumService.changeFileOwner(hashOfFile, sendToAddress, contractAddress, privateKey);
    }

    @GetMapping("/hashes")
    public List<String> getHashes(
            @RequestParam
                    String contractAddress,
            @RequestParam
                    String privateKey
    )
            throws Exception {
        return ethereumService.getHashes(contractAddress, privateKey);
    }

    @GetMapping("/events/initFile")
    public List<InitFileStoreEventResource> getInitFileEvents(
            @RequestParam
                    String contractAddress,
            @RequestParam
                    String privateKey
    ) {
        return ethereumService.getInitFileStoreEvents(contractAddress, privateKey).stream()
                              .map(initFileStoreEventResourceAssembler::toResource)
                              .collect(Collectors.toList());
    }

    @GetMapping("/events/changeOwner")
    public List<ChangeFileOwnerEventResource> getChangeFileOwnerEvents(
            @RequestParam
                    String contractAddress,
            @RequestParam
                    String privateKey
    ) {
        return ethereumService.getChangeFileOwnerEvents(contractAddress, privateKey).stream()
                              .map(changeFileOwnerEventResourceAssembler::toResource)
                              .collect(Collectors.toList());
    }

    @PostMapping("/deployContract")
    public String deployKycContract(
            @RequestParam
                    String privateKey
    )
            throws Exception {
        String contractAddress = ethereumService.deployFileStorageContract(privateKey);
        FILE_STORAGE_CONTRACT_ADDRESS = contractAddress;
        System.out.println("Contract address is " + contractAddress);
        return contractAddress;
    }
}
