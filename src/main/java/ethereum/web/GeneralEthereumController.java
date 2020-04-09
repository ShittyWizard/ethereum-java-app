package ethereum.web;

import java.util.List;
import java.util.stream.Collectors;

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

    protected static String FILE_STORAGE_CONTRACT_ADDRESS;

    @PostMapping("/init")
    public String storeFile(
            @RequestBody
                    MultipartFile file,
            @RequestParam
                    String privateKey
    )
            throws Exception {
        String hashOfFile = ipfsService.uploadMultipartFile(file);
        return ethereumService.storeHashOfFile(hashOfFile, FILE_STORAGE_CONTRACT_ADDRESS, privateKey);
    }

    @PostMapping("/changeOwner")
    public String changeFileOwner(
            @RequestParam
                    String hashOfFile,
            @RequestParam
                    String sendToAddress,
            @RequestParam
                    String privateKey
    )
            throws Exception {
        return ethereumService.changeFileOwner(hashOfFile, sendToAddress, FILE_STORAGE_CONTRACT_ADDRESS, privateKey);
    }

    @GetMapping("/hashes")
    public List<String> getHashes(
            @RequestParam
                    String privateKey
    )
            throws Exception {
        return ethereumService.getHashes(FILE_STORAGE_CONTRACT_ADDRESS, privateKey);
    }

    @GetMapping("/events/initFile")
    public List<InitFileStoreEventResource> getInitFileEvents(
            @RequestParam
                    String privateKey
    ) {
        return ethereumService.getInitFileStoreEvents(FILE_STORAGE_CONTRACT_ADDRESS, privateKey).stream()
                              .map(initFileStoreEventResourceAssembler::toResource)
                              .collect(Collectors.toList());
    }

    @GetMapping("/events/changeOwner")
    public List<ChangeFileOwnerEventResource> getChangeFileOwnerEvents(
            @RequestParam
                    String privateKey
    ) {
        return ethereumService.getChangeFileOwnerEvents(FILE_STORAGE_CONTRACT_ADDRESS, privateKey).stream()
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
