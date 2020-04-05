package ipfs.services;

public interface IPFSService {
    public boolean uploadFile(String pathToFile);
    public byte[] downloadFile(String hash);
}
