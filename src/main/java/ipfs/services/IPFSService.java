package ipfs.services;

import org.springframework.web.multipart.MultipartFile;

public interface IPFSService {
    public boolean uploadFile(MultipartFile file);
    public byte[] downloadFile(String hash);
}
