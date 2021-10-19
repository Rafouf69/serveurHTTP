package http.server;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class ActionBridge {


    public ActionBridge(HTTPResponse response, HTTPRequest request){
        switch (request.getMethod()){
            case GET:
                doGet(request, response);
                break;
            case HEAD:
                doHead(request, response);
                break;
            default:
                break;
        }
    }

    private void onGet(HTTPRequest request, HTTPResponse response) {
        String path = request.getPath();

    }

    private void doHead(HTTPRequest request, HTTPResponse response){
        String path = request.getPath();
        if((new File(path)).exists()){
            response.setResponseHeader("Content-Type", URLConnection.guessContentTypeFromName(path));
            response.setStatus(HTTPResponseStatus.NO_CONTENT);
        }else{
            response.setStatus(HTTPResponseStatus.NOT_FOUND);
        }
    }
}
