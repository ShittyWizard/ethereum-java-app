package ethereum.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ethereum.services.EthereumService;
import ipfs.services.IPFSService;

import static ethereum.web.GeneralEthereumController.FILE_STORAGE_CONTRACT_ADDRESS;

@RestController
@RequestMapping(value = "/crosschain/ethereum/filestore", produces = MediaType.APPLICATION_JSON_VALUE)
public class CrosschainEthereumController {
    @Autowired
    private EthereumService ethereumService;
    @Autowired
    private IPFSService ipfsService;

    //@Value("${eth.crossnode.private.key}")

    private String CROSS_NODE_PRIVATE_KEY = "0x5c690e5e749aaca8cdd21dfffd1cda77d1dca1326f0d9c756f5943947f8f5516";

    @PostMapping("/init")
    public String storeFileCrosschain(
            @RequestBody
                    InputStreamResource inputStreamResource
    )
            throws Exception {
        System.out.println("Got request for crosschain init transaction...");
        String hashOfFile = ipfsService.uploadFileByInputStream(inputStreamResource.getInputStream());
        if (FILE_STORAGE_CONTRACT_ADDRESS != null) {
            return ethereumService.storeHashOfFile(hashOfFile, FILE_STORAGE_CONTRACT_ADDRESS, CROSS_NODE_PRIVATE_KEY);
        } else {
            throw new IllegalArgumentException("Can't get contract address...");
        }
    }

    @PostMapping("/changeOwner")
    public String changeFileOwnerCrosschain(
            @RequestBody
                    InputStreamResource inputStreamResource,
            @RequestParam
                    String sendToAddress
    )
            throws Exception {
        System.out.println("Got request for crosschain changeFileOwner transaction...");
        String hashOfFile = ipfsService.uploadFileByInputStream(inputStreamResource.getInputStream());

        return ethereumService.changeFileOwner(hashOfFile, sendToAddress, FILE_STORAGE_CONTRACT_ADDRESS, CROSS_NODE_PRIVATE_KEY);
    }
}
