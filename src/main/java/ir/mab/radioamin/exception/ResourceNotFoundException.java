package ir.mab.radioamin.exception;

public class ResourceNotFoundException extends RuntimeException{
    private String resource;
    private String parameter;
    private String value;

    public ResourceNotFoundException(String resource){
        this.resource = resource;
    }

    public ResourceNotFoundException(String resource, String value){
        this.resource = resource;
        this.value = value;
    }

    public ResourceNotFoundException(String resource, String value, String parameter){
        this.resource = resource;
        this.value = value;
        this.parameter = parameter;
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

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
