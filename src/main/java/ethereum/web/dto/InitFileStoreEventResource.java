package ethereum.web.dto;

public class InitFileStoreEventResource {
    private String owner;
    private String hashOfFile;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getHashOfFile() {
        return hashOfFile;
    }

    public void setHashOfFile(String hashOfFile) {
        this.hashOfFile = hashOfFile;
    }
}
