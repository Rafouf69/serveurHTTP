package http.server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
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
            this.fileName = "/document/index.html";
        } else {
            if(fetchDest.equals("document")){
                this.contentType="text/html";
                this.fileName = requestArray.get(1)+".html";
            }else{
                this.contentType="image/png";
                this.fileName = requestArray.get(1);
            }
        }
        this.HTTPversion = requestArray.get(2);
        this.host = requestArray.get(4);
    }

    public String handleGet() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        System.out.println(fileName);

        String path = "target/classes"+fileName;

        File fileToUpload = new File(path);



        String response = "";
        if(!fileToUpload.exists()){
            response+="HTTP/1.0 404 Not Found\r\ncontent-type: text/html\r\nServer: Bot\r\n\r\n";

            fileToUpload = new File("target/classes/document/error.html");

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToUpload)));
            for (String line; (line = reader.readLine()) != null;) {
                response+=(line);
            }
        } else {
            if(fileName.contains("html")){
                response += this.handleGetText(fileToUpload);
            }else{
                response += this.handleGetImage(fileToUpload);
            }


        }
        return response;
    }

    private String handleGetImage(File file) throws IOException {
        String response = "";
        try {
            BufferedImage image = ImageIO.read(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);

            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();

            String body = byteArrayOutputStream.toString();

            response += "HTTP/1.0 200 OK\r\ncontent-length: "+size.length+"\r\ncontent-type: image/png\r\nServer: Bot\r\n\r\n"+body;
        } catch ( IOException e) {
            response += "HTTP/1.0 400 ERROR\r\nServer: Bot\r\n\r\n";
        }
        return response;
    }

    private String handleGetText(File file) {
        String response="";
        try{
            String body = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for (String line; (line = reader.readLine()) != null;) {
                body+=(line);
            }
            response+="HTTP/1.0 200 OK\r\ncontent-type: text/html\r\nServer: Bot\r\n\r\n"+body;
        }catch(IOException e){
            response+="HTTP/1.0 400 ERROR\r\nServer: Bot\r\n\r\n";
        }

        return response;
    }

    public String handleHead() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        InputStream is = classloader.getResourceAsStream(fileName);
        InputStreamReader streamReader;
        BufferedReader reader;
        String response = "HTTP/1.0 ";
        String endResponse = "Content-type: "+contentType+"\r\nServer: Bot\r\n\r\n";
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
