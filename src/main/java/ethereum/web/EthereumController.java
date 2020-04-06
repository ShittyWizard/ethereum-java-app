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
        String hashOfFile = ipfsService.uploadFile(file);
        return ethereumService.storeHashOfFile(hashOfFile, contractAddress, privateKey);
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
        return ethereumService.deployFileStorageContract(privateKey);
    }
}
