package ipfs.services;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface IPFSService {
    public String uploadFile(MultipartFile file);
    public InputStreamResource downloadFile(String hash);
}
