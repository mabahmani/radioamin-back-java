package ir.mab.radioamin.config;

public class ApiBaseEndpoints {
    public final static String BASE = "/api";

    public static class VersionOne {
        public final static String DEVELOPER = BASE + "/v1/developer";
        public final static String ADMIN = BASE + "/v1/admin";
        public final static String CONSUMER = BASE + "/v1/consumer";
        public final static String ANONYMOUS = BASE + "/v1/anonymous";
    }

}
