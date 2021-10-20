///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 *
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 *
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

    /**
     * WebServer constructor.
     */
    protected void start() {
        ServerSocket s;

        System.out.println("Webserver starting up on port 80");
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(3000);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        for (;;) {
            try {
                // wait for a connection
                Socket remote = s.accept();
                // remote is now the connected socket
                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        remote.getInputStream()));
                OutputStream out = remote.getOutputStream();

                // read the data sent. We basically ignore it,
                // stop reading once a blank line is hit. This
                // blank line signals the end of the client HTTP
                // headers.
                String header="";
                String str = ".";
                HashMap<String, String> body = new HashMap<>();

                while (str!=null && !str.equals("")){
                    str = in.readLine();
                    header += str+" ";
                }

                if(header.contains("Content-Length")){
                    String[]content = header.split(" ");
                    String boundary = "";
                    for(int i=0; i<content.length; i++){
                        if(content[i].contains("boundary")){
                            String[] boundaryArray = content[i].split("boundary=");
                            boundary = boundaryArray[1];
                        }
                    }
                    str = in.readLine();
                    while(!str.equals("--" + boundary + "--")){
                        assert boundary.equals(str);
                        String key = in.readLine().split("=")[1].replace('"',' ').strip();
                        in.readLine();
                        String value = in.readLine();
                        body.put(key, value);
                        str = in.readLine();
                    }
                }

                HTTPRequest myRequest = new HTTPRequest(header, body);

                switch (myRequest.getMethodeType()) {
                    case "GET":
                        myRequest.handleGet(out);
                        break;
                    case "HEAD":
                        myRequest.handleHead(out);
                        break;
                    case "POST":
                        myRequest.handlePost(out);
                        break;
                    case "PUT":
                        myRequest.handlePut(out);
                        break;
                    case "DELETE":
                        myRequest.handleDelete(out);
                        break;
                }

                out.flush();
                remote.close();
            } catch (Exception e) {
                System.out.println("Error WebServer: " + e);
            }
        }
    }

    /**
     * Start the application.
     *
     * @param args
     *            Command line parameters are not used.
     */
    public static void main(String args[]) {
        WebServer ws = new WebServer();
        ws.start();
    }
}
