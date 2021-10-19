package http.server;

public enum HTTPResponseStatus {
    NOT_FOUND(404, "Not Found"),
    OK(200, "OK"),
    NO_CONTENT(201, "No Content");

    int code;
    String value;

    HTTPResponseStatus(int code, String value){
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
