package commons;

import java.util.Map;

public class ApiResponse {
    boolean success;
    Map<String, Object> body;

    public ApiResponse(boolean success, Map<String, Object> body) {
        this.success = success;
        this.body = body;
    }
}
