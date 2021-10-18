package http.server;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private String typeMethode;
    private String fileName;
    private String HTTPversion;
    private String host;
    private String fetchDest;
    private String contentType;

    public Request(String requestHeader){
        List<String> requestArray = Arrays.asList(requestHeader.split(" "));
        this.typeMethode = requestArray.get(0);

        int index = requestArray.indexOf("Sec-Fetch-Dest:");

        fetchDest = requestArray.get(index+1);

        if(requestArray.get(1).equals("/")){
            this.contentType="text/html";
            this.fileName = "document/index.html";
        } else {
            if(fetchDest.equals("document")){
                this.contentType="text/html";
                this.fileName = requestArray.get(1).substring(1)+".html";
            }else{
                this.contentType="image/png";
                this.fileName = requestArray.get(1).substring(1);
            }
        }
        this.HTTPversion = requestArray.get(2);
        this.host = requestArray.get(4);
    }

    public String handleGet() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        File fileToUpload = new File(String.valueOf(classloader.getResource(fileName)).substring(6));

        String response = "HTTP/1.0 ";
        String endResponse = "Content-Type: "+contentType+"\r\nServer: Bot\r\n\r\n";
        if(!fileToUpload.exists()){
            response+="404 Not Found\r\n"+endResponse;

            fileToUpload = new File(String.valueOf(classloader.getResource("document/error.html")).substring(6));

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToUpload)));
            for (String line; (line = reader.readLine()) != null;) {
                response+=(line);
            }
        } else {
            response+="200 OK\r\n"+ endResponse;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToUpload)));
            for (String line; (line = reader.readLine()) != null;) {
                response+=(line);
            }
        }
        return response;
    }

    public String handleHead() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        InputStream is = classloader.getResourceAsStream(fileName);
        InputStreamReader streamReader;
        BufferedReader reader;
        String response = "HTTP/1.0 ";
        String endResponse = "Content-type: text/html\r\nServer: Bot\r\n\r\n";
        if(is==null){
            response+="404 Not Found\r\n"+endResponse;
        } else {
            response+="200 OK\r\n"+endResponse;
        }
        return response;
    }

    public String getMethodeType(){
        return typeMethode;
    }
}
