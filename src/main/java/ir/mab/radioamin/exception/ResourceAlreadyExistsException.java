package ir.mab.radioamin.exception;

public class ResourceAlreadyExistsException extends RuntimeException{
    private String resource;
    private String value;

    public ResourceAlreadyExistsException(String resource){
        this.resource = resource;
    }

    public ResourceAlreadyExistsException(String resource, String value){
        this.resource = resource;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
