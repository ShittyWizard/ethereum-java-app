package ethereum.web;

import ethereum.web.assemblers.ChangeFileOwnerEventResourceAssembler;
import ethereum.web.assemblers.InitFileStoreEventResourceAssembler;
import ethereum.web.dto.ChangeFileOwnerEventResource;
import ethereum.web.dto.InitFileStoreEventResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import ethereum.services.EthereumService;
import ipfs.services.IPFSService;

import java.util.List;
import java.util.stream.Collectors;

import static ethereum.web.GeneralEthereumController.FILE_STORAGE_CONTRACT_ADDRESS;

@RestController
@RequestMapping(value = "/crosschain/ethereum/filestore/receiver", produces = MediaType.APPLICATION_JSON_VALUE)
public class CrosschainReceiverEthereumController {
    @Autowired
    private EthereumService ethereumService;
    @Autowired
    private IPFSService ipfsService;
    @Autowired
    private InitFileStoreEventResourceAssembler initFileStoreEventResourceAssembler;
    @Autowired
    private ChangeFileOwnerEventResourceAssembler changeFileOwnerEventResourceAssembler;

    private String CROSS_NODE_PRIVATE_KEY = "0x5c690e5e749aaca8cdd21dfffd1cda77d1dca1326f0d9c756f5943947f8f5516";

    @GetMapping("/checkGrantForFile")
    public boolean checkGrantForFile(
            @RequestParam
                    String ipfsHashOfFile,
            @RequestParam
                    String senderPublicKey // sender of file
    ) {
        List<InitFileStoreEventResource> listOfAllInits =
                ethereumService.getInitFileStoreEvents(FILE_STORAGE_CONTRACT_ADDRESS, CROSS_NODE_PRIVATE_KEY).stream()
                        .map(initFileStoreEventResourceAssembler::toResource)
                        .collect(Collectors.toList());
        List<ChangeFileOwnerEventResource> listOfAllChangeOwners =
                ethereumService.getChangeFileOwnerEvents(FILE_STORAGE_CONTRACT_ADDRESS, CROSS_NODE_PRIVATE_KEY).stream()
                        .map(changeFileOwnerEventResourceAssembler::toResource)
                        .collect(Collectors.toList());
        boolean isInitBySender = listOfAllInits.stream()
                .anyMatch(res -> res.getHashOfFile().equalsIgnoreCase(ipfsHashOfFile) && res.getOwner().equalsIgnoreCase(senderPublicKey));
        if (!listOfAllChangeOwners.isEmpty()) {
            if (!isInitBySender) {
                return listOfAllChangeOwners.stream().anyMatch(res -> res.getActualOwner().equalsIgnoreCase(senderPublicKey));
            } else {
                return listOfAllChangeOwners.stream().anyMatch(res -> !res.getPrevOwner().equalsIgnoreCase(senderPublicKey));
            }
        } else {
            return isInitBySender;
        }
    }


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
