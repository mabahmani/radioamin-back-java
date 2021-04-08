package ir.mab.radioamin.security;

public class SecurityConstants {
    public static final long ActivationCodeExpireTime = 5 * 60 * 1000;
    public static final long JWT_EXPIRATION_TIME = 60 * 60 * 1000;
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

    public static final String JWT_SECRET = "JWT_SECRET_KEY";
    public static final String REFRESH_TOKEN_SECRET = "REFRESH_TOKEN_SECRET_KEY";

    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_STRING = "Authorization";

    public static final String JWT_EXPIRED_ATTRIBUTE = "JWT_EXP";
    public static final String JWT_NOT_ATTACHED_ATTRIBUTE = "JWT_NOT_ATTACH";
    public static final String JWT_BEARER_NOT_ATTACHED_ATTRIBUTE = "JWT_BEARER_NOT_ATTACH";


    public static final String JWT_ISSUER = "AC_ISSUER";
    public static final String REFRESH_TOKEN_ISSUER = "REF_ISSUER";
}
