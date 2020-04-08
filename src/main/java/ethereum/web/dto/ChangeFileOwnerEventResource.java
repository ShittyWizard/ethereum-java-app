package ethereum.web.dto;

public class ChangeFileOwnerEventResource {
    private String hashOfFile;
    private String prevOwner;
    private String actualOwner;

    public String getHashOfFile() {
        return hashOfFile;
    }

    public void setHashOfFile(String hashOfFile) {
        this.hashOfFile = hashOfFile;
    }

    public String getPrevOwner() {
        return prevOwner;
    }

    public void setPrevOwner(String prevOwner) {
        this.prevOwner = prevOwner;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;
    }
}
