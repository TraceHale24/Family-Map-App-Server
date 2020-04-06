package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class FileHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        //System.out.println("We made it here... I think");
        try {
            if(exchange.getRequestMethod().toLowerCase().equals("get")) {
                //
                // System.out.println("I am now in the if");
                String urlPath = exchange.getRequestURI().getPath();
                if(urlPath.equals("/") || urlPath.length() == 0) {
                    urlPath = "/index.html";
                }
                String filePath = "/Users/kicke/IdeaProjects/FamilyMap/web" + urlPath;
                File file = new File(filePath);
                if(file.exists()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                else {
                    filePath = "/Users/kicke/IdeaProjects/FamilyMap/web/HTML/404.html/";
                    file = new File(filePath);
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                }

                OutputStream respBody = exchange.getResponseBody();
                Files.copy(file.toPath(), respBody);
                respBody.close();
                success = true;

            }
            if(!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }
}
