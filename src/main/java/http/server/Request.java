package http.server;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Base64;
import java.util.zip.Deflater;

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
            if(fetchDest.equals("document") && !requestArray.get(1).toLowerCase(Locale.ROOT).contains("png")){
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
                response = this.handleGetText(fileToUpload);
            }else{
                response = this.handleGetImage(fileToUpload);
            }


        }
        return response;
    }

    private String handleGetImage(File file) throws IOException {
        String response = "";
        try {
            /*BufferedImage image = ImageIO.read(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);*/

            FileInputStream imageInFile = new FileInputStream(file);
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
            imageData = Base64.getEncoder().encode(imageData);
            Deflater compresser = new Deflater();
            compresser.deflate(imageData);
            String imageDataString = new String(imageData, StandardCharsets.UTF_8);;
            imageInFile.close();
            System.out.println("Image Successfully Manipulated!");

            response += "HTTP/1.0 200 OK\r\ncontent-length: "+file.length()/8+"\r\ncontent-type: image/png\r\nServer: Bot\r\n\r\n"+"data:image/png;base64,"+imageDataString;
        } catch ( IOException e) {
            response += "HTTP/1.0 400 ERROR\r\nServer: Bot\r\n\r\n";
        }
        System.out.println("image : \n"+response);
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

    public String handleHead() {
        String path = "target/classes"+fileName;

        File fileToUpload = new File(path);

        String response = "HTTP/1.0 ";
        String endResponse = "Content-type: "+contentType+"\r\nServer: Bot\r\n\r\n";
        if(!fileToUpload.exists()){
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
