package ipfs.services;

import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface IPFSService {
    public String uploadMultipartFile(MultipartFile file);
    public String uploadFileByInputStream(InputStream inputStream);
    public InputStreamResource downloadFile(String hash);
}
