package http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HTTPResponse {
    private HTTPResponseStatus status = HTTPResponseStatus.NOT_FOUND;
    private HashMap<String, String> responseHeader = new HashMap<>();
    private byte[] body = null;

    public HTTPResponse(){
        setResponseHeader("Connexion", "close");
        setResponseHeader("Server", "ourBot");
    }

    public void emitHTTPResponse(OutputStream out) throws IOException {
        StringBuilder header = new StringBuilder();

        header.append("HTTP/1.1 ").append(status.getCode()).append(" ").append(status.getValue()).append("\r\n");

        String endHeader = responseHeader.entrySet().stream().map(data -> data.getKey() +": " + data.getValue()).collect(Collectors.joining("\r\n"));

        header.append(endHeader).append("\r\n");

        out.write(header.toString().getBytes());

        if(body!=null){
            out.write(body);
        }

        System.out.println("HTTP response \r\n"+ header.toString());
    }

    public void send(String text){
        this.body = text.getBytes(StandardCharsets.UTF_8);
        setStatus(HTTPResponseStatus.OK);
        setResponseHeader("Content-Length", String.valueOf(body.length));
    }

    public void send(byte[] text){
        this.body = text;
        setStatus(HTTPResponseStatus.OK);
        setResponseHeader("Content-Length", String.valueOf(body.length));
    }

    public void setResponseHeader(String key, String value) {
        this.responseHeader.put(key, value);
    }

    public byte[] getBody() {
        return body;
    }

    public HTTPResponseStatus getStatus() {
        return status;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setStatus(HTTPResponseStatus status) {
        this.status = status;
    }


}
