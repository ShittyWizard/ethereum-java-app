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
@RequestMapping(value = "/crosschain/ethereum/filestore/receiver", produces = MediaType.APPLICATION_JSON_VALUE)
public class CrosschainReceiverEthereumController {
    @Autowired
    private EthereumService ethereumService;
    @Autowired
    private IPFSService ipfsService;

    @Value("${eth.crossnode.private.key}")
    private String CROSS_NODE_PRIVATE_KEY;

    //    UNUSED
    @PostMapping("/init")
    public String storeFileCrosschain(
            @RequestBody
                    InputStreamResource inputStreamResource
    )
            throws Exception {
        System.out.println("Got request for crosschain init transaction...");
        String hashOfFile = ipfsService.uploadFileByInputStream(inputStreamResource.getInputStream());
        return ethereumService.storeHashOfFile(hashOfFile, FILE_STORAGE_CONTRACT_ADDRESS, CROSS_NODE_PRIVATE_KEY);
    }

    //    this request coming from Corda app
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
