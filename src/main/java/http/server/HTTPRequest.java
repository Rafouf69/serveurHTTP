package http.server;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Base64;
import java.util.zip.Deflater;

public class HTTPRequest {
    private String typeMethode;
    private String fileName;
    private String HTTPversion;
    private String host;
    private String fetchDest;
    private String contentType;

    public HTTPRequest(String requestHeader){
        List<String> requestArray = Arrays.asList(requestHeader.split(" "));
        this.typeMethode = requestArray.get(0);

        int index = requestArray.indexOf("Sec-Fetch-Dest:");

        fetchDest = requestArray.get(index+1);
// faudrait faire un switch ici
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
            // modifier pour /?
            if(requestArray.get(1).contains("/?")){
                this.fileName = requestArray.get(1);
            }
            if(requestArray.get(0).equals("PUT")){
                this.fileName = requestArray.get(1)+".html";
            }
            if(requestArray.get(0).equals("DELETE")){
                this.fileName = requestArray.get(1)+".html";
            }

        }
        this.HTTPversion = requestArray.get(2);
        this.host = requestArray.get(4);
    }

    public void handleGet(OutputStream out) throws IOException {
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
                this.handleGetText(fileToUpload, out);
            }else{
                this.handleGetImage(fileToUpload, out);
            }
        }
    }

    private void handleGetImage(File file, OutputStream out) throws IOException {
        String response = "";
        try {
            FileInputStream imageInFile = new FileInputStream(file);
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
            imageInFile.close();
            System.out.println("Image Successfully Manipulated!");

            response = "HTTP/1.0 200 OK\r\ncontent-length: "+file.length()/8+"\r\ncontent-type: image/png\r\nServer: Bot\r\n\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));

            out.write(imageData);
        } catch ( IOException e) {
            response = "HTTP/1.0 400 ERROR\r\nServer: Bot\r\n\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
        }
        System.out.println("image : \n"+response);
    }

    private void handleGetText(File file, OutputStream out) throws IOException {
        String response="";
        try{
            String body = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for (String line; (line = reader.readLine()) != null;) {
                body+=(line);
            }
            response="HTTP/1.0 200 OK\r\ncontent-type: text/html\r\nServer: Bot\r\n\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.write(body.getBytes(StandardCharsets.UTF_8));
        }catch(IOException e){
            response="HTTP/1.0 400 ERROR\r\nServer: Bot\r\n\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
        }

    }

    public void handlePost(OutputStream out) throws IOException{
        String[] data = fileName.split("/\\?");

        String path = "target/classes"+data[0];
        File fileToUpload = new File(path);

        String response = "";
        if(!fileToUpload.exists()) {
            response += "HTTP/1.0 404 Not Found\r\ncontent-type: text/html\r\nServer: Bot\r\n\r\n";

            fileToUpload = new File("target/classes/document/error.html");

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToUpload)));
            for (String line; (line = reader.readLine()) != null; ) {
                response += (line);
            }
        } else{
            try{
                String body = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToUpload)));
                for (String line; (line = reader.readLine()) != null;) {
                    body+=(line);
                }
                response="HTTP/1.0 201 OK\r\ncontent-type: text/html\r\nServer: Bot\r\n\r\n";
                out.write(response.getBytes(StandardCharsets.UTF_8));
                out.write(body.getBytes(StandardCharsets.UTF_8));
            }catch(IOException e){
                response="HTTP/1.0 400 ERROR\r\nServer: Bot\r\n\r\n";
                out.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }



    }
    public void handlePut(OutputStream out) throws IOException{
        String path = "target/classes"+fileName;
        File fileToCreate = new File(path);

        String response = "";
        if(fileToCreate.exists()){
            fileToCreate.delete();
            fileToCreate.createNewFile();
            response="HTTP/1.0 204 No Content\r\nContent-Location: "+ path +"\r\nServer: Bot\r\n\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } else{
            fileToCreate.createNewFile();
            response="HTTP/1.0 201 Created\r\nContent-Location: "+ path +"\r\nServer: Bot\r\n\r\n";
            System.out.println(response);
            out.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void handleDelete(OutputStream out) throws IOException{
        String path = "target/classes"+fileName;
        File fileToDelete = new File(path);

        String response = "";
        if(fileToDelete.exists()){
            fileToDelete.delete();
            response="HTTP/1.0 204 No Content\r\nServer: Bot\r\n\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } else {
            response += "HTTP/1.0 404 Not Found\r\ncontent-type: text/html\r\nServer: Bot\r\n\r\n";

            fileToDelete = new File("target/classes/document/error.html");

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToDelete)));
            for (String line; (line = reader.readLine()) != null; ) {
                response += (line);
            }
        }
    }

    public void handleHead(OutputStream out) throws IOException {
        String path = "target/classes"+fileName;

        File fileToUpload = new File(path);

        String response = "HTTP/1.0 ";
        String endResponse = "Content-type: "+contentType+"\r\nServer: Bot\r\n\r\n";
        if(!fileToUpload.exists()){
            response+="404 Not Found\r\n"+endResponse;
        } else {
            response+="200 OK\r\n"+endResponse;
        }
        out.write(response.getBytes(StandardCharsets.UTF_8));
    }

    public String getMethodeType(){
        return typeMethode;
    }
}
