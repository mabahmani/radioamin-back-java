package ir.mab.radioamin.exception;

public class TokenExpiredException extends RuntimeException{
    private String tokenName;

    public TokenExpiredException(String tokenName){
        this.tokenName = tokenName;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

}
