package Handlers;

import Results.Result;
import Services.FillService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import result.FillResult;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;

public class FillHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        int generations = 0;
        Gson gson = new Gson();
        FillService service = new FillService();
        URI uri = exchange.getRequestURI();
        String[] partsOfMessage = uri.getPath().split("/");

        if(partsOfMessage.length == 3) {
            generations = 4;
        }
        else {
            generations = Integer.parseInt(partsOfMessage[3]);
            if(generations < 4) {
                generations = 4;
            }
        }

        try {
            if(exchange.getRequestMethod().toLowerCase().equals("post")) {
                Result result = service.fill(partsOfMessage[2], generations);
                String response = gson.toJson(result);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBody = exchange.getResponseBody();
                OutputStreamWriter sw = new OutputStreamWriter(respBody);
                sw.write(response);
                sw.flush();
                respBody.close();
                exchange.getResponseBody().close();
                success = true;

            }
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }
}
