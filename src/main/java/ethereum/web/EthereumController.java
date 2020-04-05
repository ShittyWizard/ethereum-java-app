package ethereum.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ethereum.services.EthereumService;

@RestController
@RequestMapping(value = "/ethereum", produces = MediaType.APPLICATION_JSON_VALUE)
public class EthereumController {
    @Autowired
    private EthereumService ethereumService;

    @PostMapping("/store")
    public boolean storeHashOfFile(
            @RequestParam String hashOfFile,
            @RequestParam String contractAddress,
            @RequestParam String privateKey
    )
            throws Exception {
        return ethereumService.storeHashOfFile(hashOfFile, contractAddress, privateKey);
    }

    @GetMapping("/hashes")
    public void getHashes(
            @RequestParam String contractAddress,
            @RequestParam String privateKey
    )
            throws Exception {
        ethereumService.getHashes(contractAddress, privateKey);
    }

    @PostMapping("/deployContract")
    public String deployKycContract(
            @RequestParam String privateKey
    )
            throws Exception {
        return ethereumService.deployKycContract(privateKey);
    }
}
