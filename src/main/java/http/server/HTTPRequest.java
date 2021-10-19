package http.server;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;

public class HTTPRequest {
    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> queryParams = new HashMap<>();

    private HTTPMethod method;
    private String fileName;
    private String fetchDest;
    private String contentType;
    private String rawPath;
    private String path;
    private String body;

    public HTTPRequest(String requestHeader){
        parse(requestHeader);
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

    private void parse(String rawRequest) {
        //  Regex to extract info from req. :      method      path  protocol    headers          body
        //Pattern regex = Pattern.compile("^([A-Za-z]+)\\s(.+?)\\s(.+?)\\r\\n(.*?)(?:\\r\\n\\r\\n(.*))?$", Pattern.DOTALL);


        String [] headerBody = rawRequest.split("\\r\\n\\r\\n");

        String [] line = headerBody[0].split("\\r\\n");

        try {
            String[] firstLine = line[0].split(" ");
            this.method = HTTPMethod.valueOf(firstLine[0]);
            this.rawPath = firstLine[1];

            String[] rawPathSplit = this.rawPath.split("\\?");
            this.path = rawPathSplit[0];
            /*if (rawPathSplit.length > 1) {
                    String[] rawQueryParamsSplit = rawPathSplit[1].split("&");

                    for (String rawQueryParams : rawQueryParamsSplit) {
                        String[] keyValues = rawQueryParams.split("=");

                        if (keyValues.length > 1) {
                            this.queryParams.put(keyValues[0], keyValues[1]);
                        } else {
                            this.queryParams.put(keyValues[0], "");
                        }
                    }
                }*/

            if(headerBody.length>1){
                this.body = headerBody[2];
            }

            ArrayList<String> rawHeaders = new ArrayList<>();

            for(int i=1; i<line.length; i++){
                rawHeaders.add(line[i]);
            }

            for (String rawHeader : rawHeaders) {
                String[] rowSplit = rawHeader.split(": ");
                this.headers.put(rowSplit[0].toLowerCase(), rowSplit[1].toLowerCase());
            }
        } catch (Exception e) {
            System.out.println("Error in parser: "+e);
        }
    }

    public String getPath(){
        return path;
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public String getHeaders(String key) {
        return headers.get(key.toLowerCase(Locale.ROOT));
    }

    public String getRawPath() {
        return rawPath;
    }

    public String getBody() {
        return body;
    }

    public String getQueryParams(String key) {
        return queryParams.get(key.toLowerCase(Locale.ROOT));
    }
}
