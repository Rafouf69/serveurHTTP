package http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Request {
    private String typeMethode;
    private String fileName;
    private String HTTPversion;
    private String host;

    public Request(String requestHeader){
        String[] requestArray = requestHeader.split(" ");
        this.typeMethode = requestArray[0];
        if(requestArray[1].equals("/")){
            this.fileName = "index.html";
        } else {
            this.fileName = requestArray[1].substring(1)+".html";
        }
        this.HTTPversion = requestArray[2];
        this.host = requestArray[4];
    }

    public String handleGet() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        InputStream is = classloader.getResourceAsStream(fileName);
        InputStreamReader streamReader;
        BufferedReader reader;
        String response = "HTTP/1.0 ";
        String endResponse = "Content-type: text/html\r\nServer: Bot\r\n\r\n";
        if(is==null){
            response+="404 Not Found\r\n"+endResponse;

            is = classloader.getResourceAsStream("error.html");
            streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            reader = new BufferedReader(streamReader);
            for (String line; (line = reader.readLine()) != null;) {
                // Process line
                response+=line;
            }

        } else {
            response+="200 OK\r\n"+endResponse;

            streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            reader = new BufferedReader(streamReader);
            for (String line; (line = reader.readLine()) != null;) {
                // Process line
                response+=line;
            }

        }
        return response;
    }
}
