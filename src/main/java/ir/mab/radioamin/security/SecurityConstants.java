package ir.mab.radioamin.security;

public class SecurityConstants {
    public static final String JWT_SECRET = "SECRET_KEY";
    public static final long JWT_EXPIRATION_TIME = 2 * 60 * 1000;
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_STRING = "Authorization";
    public static final String JWT_EXPIRED_ATTRIBUTE = "JWT_EXP";
    public static final String JWT_NOT_ATTACHED_ATTRIBUTE = "JWT_NOT_ATTACH";
    public static final String JWT_BEARER_NOT_ATTACHED_ATTRIBUTE = "JWT_BEARER_NOT_ATTACH";
}
