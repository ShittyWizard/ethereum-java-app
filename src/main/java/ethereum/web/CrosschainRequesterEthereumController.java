package ethereum.web;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import ethereum.services.EthereumService;
import ipfs.services.IPFSService;

import static ethereum.web.GeneralEthereumController.FILE_STORAGE_CONTRACT_ADDRESS;

@RestController
@RequestMapping(value = "/crosschain/ethereum/filestore/requester", produces = MediaType.APPLICATION_JSON_VALUE)
public class CrosschainRequesterEthereumController {
    @Autowired
    private EthereumService ethereumService;
    @Autowired
    private IPFSService ipfsService;
    @Autowired
    private RestTemplate restTemplate;

    //    todo: change address to new IP
    private static String CORDA_START_KYC_FLOW_URL = "http://84.201.169.143:10056/attachments/startKYCFlow/crosschain/receiver";

    @Value("${eth.crossnode.public.key}")
    private String CROSS_NODE_PUBLIC_KEY;
    @Value("${eth.crossnode.private.key}")
    private String CROSS_NODE_PRIVATE_KEY;

    @PostMapping("/changeOwner/init")
    public String changeExistingFileOwner(
            @RequestParam
                    String ipfsHashFile,
            @RequestParam
                    String organisation,
            @RequestParam
                    String locality,
            @RequestParam
                    String country,
            @RequestParam
                    String publicKey,
            @RequestParam
                    String privateKey
    ) throws Exception {
        String txHash = ethereumService.changeFileOwner(ipfsHashFile, CROSS_NODE_PUBLIC_KEY, FILE_STORAGE_CONTRACT_ADDRESS, privateKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        InputStreamResource fileStreamResource = ipfsService.downloadFile(ipfsHashFile);
        HttpEntity<InputStreamResource> entity = new HttpEntity<>(fileStreamResource, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(CORDA_START_KYC_FLOW_URL)
                .queryParam("organisation", organisation)
                .queryParam("locality", locality)
                .queryParam("country", country)
                .queryParam("filename", "filename")
                .queryParam("uploader", "ethereum:" + publicKey);

        restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, Void.class).getBody();
        return txHash;
    }

    @PostMapping("/changeOwnerExpress")
    public String changeInitFileOwner(
            @RequestBody
                    MultipartFile file,
            @RequestParam
                    String organisation,
            @RequestParam
                    String locality,
            @RequestParam
                    String country,
            @RequestParam
                    String publicKey,
            @RequestParam
                    String privateKey
    )
            throws Exception {
        String hashOfFile = ipfsService.uploadMultipartFile(file);

        String txHash = ethereumService.changeFileOwner(hashOfFile, CROSS_NODE_PUBLIC_KEY, FILE_STORAGE_CONTRACT_ADDRESS, privateKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<InputStreamResource> entity = new HttpEntity<>(new InputStreamResource(file.getInputStream()), headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(CORDA_START_KYC_FLOW_URL)
                .queryParam("organisation", organisation)
                .queryParam("locality", locality)
                .queryParam("country", country)
                .queryParam("filename", "filename")
                .queryParam("uploader", "ethereum:" + publicKey);

        restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, Void.class).getBody();
        return txHash;
    }

    @PostMapping("/changeOwner")
    public String changeExistingFileOwner(
            @RequestParam
                    String hashOfFile,
            @RequestParam
                    String organisation,
            @RequestParam
                    String locality,
            @RequestParam
                    String country,
            @RequestParam
                    String publicKey,
            @RequestParam
                    String privateKey
    )
            throws Exception {
        String txHash = ethereumService.changeFileOwner(hashOfFile, CROSS_NODE_PUBLIC_KEY, FILE_STORAGE_CONTRACT_ADDRESS, privateKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        InputStreamResource fileResource = ipfsService.downloadFile(hashOfFile);
        HttpEntity<InputStreamResource> entity = new HttpEntity<>(fileResource, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(CORDA_START_KYC_FLOW_URL)
                .queryParam("organisation", organisation)
                .queryParam("locality", locality)
                .queryParam("country", country)
                .queryParam("filename", "filename")
                .queryParam("uploader", "ethereum:" + publicKey);

        restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, Void.class).getBody();
        return txHash;
    }
}
