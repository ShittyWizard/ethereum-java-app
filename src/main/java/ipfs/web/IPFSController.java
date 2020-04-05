package ipfs.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ipfs.services.IPFSService;

@RestController
@RequestMapping(value = "/ipfs", produces = MediaType.APPLICATION_JSON_VALUE)
public class IPFSController {
    @Autowired
    private IPFSService ipfsService;

    @PostMapping("/upload")
    public boolean uploadFile(
            @RequestBody
                    MultipartFile file
    ) {
        return ipfsService.uploadFile(file);
    }

    @GetMapping("/upload/{hash}")
    public byte[] downloadFile(
            @PathVariable String hash
    ) {
        return ipfsService.downloadFile(hash);
    }
}
