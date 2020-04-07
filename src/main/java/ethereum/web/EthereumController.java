package ethereum.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ethereum.services.EthereumService;
import ethereum.web.assemblers.InitFileStoreEventResourceAssembler;
import ethereum.web.dto.InitFileStoreEventResource;
import ipfs.services.IPFSService;

@RestController
@RequestMapping(value = "/ethereum/filestore", produces = MediaType.APPLICATION_JSON_VALUE)
public class EthereumController {
    @Autowired
    private EthereumService ethereumService;
    @Autowired
    private IPFSService ipfsService;
    @Autowired
    private InitFileStoreEventResourceAssembler initFileStoreEventResourceAssembler;

    @Value("${eth.crossnode.private.key}")
    private String CROSS_NODE_PRIVATE_KEY;
    private static String FILE_STORAGE_CONTRACT_ADDRESS;

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

//    todo: add publicAddress and init contract for providing rights to node with this address
    @PostMapping("/crosschain/init")
    public String storeFileCrosschain(
            @RequestBody
                    InputStreamResource inputStreamResource
            )
            throws Exception {
        System.out.println("Got request for crosschain transaction...");
        String hashOfFile = ipfsService.uploadFileByInputStream(inputStreamResource.getInputStream());
        if (FILE_STORAGE_CONTRACT_ADDRESS != null) {
            return ethereumService.storeHashOfFile(hashOfFile, FILE_STORAGE_CONTRACT_ADDRESS, CROSS_NODE_PRIVATE_KEY);
        } else {
            throw new IllegalArgumentException("Can't get contract address...");
        }
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

    @GetMapping("/initEvents")
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
