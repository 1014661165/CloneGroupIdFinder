package bean;

public class CommitBlob {
    private String blobId;
    private String realPath;

    public CommitBlob() {
    }

    public CommitBlob(String blobId, String realPath) {
        this.blobId = blobId;
        this.realPath = realPath;
    }

    public String getBlobId() {
        return blobId;
    }

    public void setBlobId(String blobId) {
        this.blobId = blobId;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }
}
