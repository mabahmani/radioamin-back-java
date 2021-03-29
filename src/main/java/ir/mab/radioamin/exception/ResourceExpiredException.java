package ir.mab.radioamin.exception;

public class ResourceExpiredException extends RuntimeException{
    private String resource;

    public ResourceExpiredException(String resource){
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

}
