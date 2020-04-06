package Handlers;

import Dao.AuthorizationTokenDAO;
import Dao.DataAccessException;
import Dao.Database;
import Dao.EventDAO;
import Results.Result;
import Services.EventIDService;
import Services.EventService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.Connection;

public class EventHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Database db = new Database();
        Connection conn = null;
        try {
            conn = db.openConnection();
        } catch (DataAccessException e) {
            System.out.println("Boo");
        }

        boolean success = false;
        Gson gson = new Gson();
        AuthorizationTokenDAO aDao = new AuthorizationTokenDAO(conn);
        EventDAO eDao = new EventDAO(conn);
        String data = "";
        Result result = new Result();
        URI uri = exchange.getRequestURI();
        String[] partsOfMessage = uri.getPath().split("/");

        try {
            if(exchange.getRequestMethod().toLowerCase().equals("get")) {
                Headers required = exchange.getRequestHeaders();
                if(required.containsKey("Authorization")) {
                    String authToken = required.getFirst("Authorization");
                    String userName = aDao.getUserName(authToken);
                    if(aDao.validate(authToken, userName) && partsOfMessage.length == 2) {
                        EventService service = new EventService();
                        result = service.run(userName);
                    }
                    else if(aDao.validate(authToken, userName) &&  partsOfMessage.length == 3&& eDao.getEvent(partsOfMessage[2]).getAssociatedUsername().equals(userName)) {
                        EventIDService idService = new EventIDService();
                        result = idService.run(eDao.getEvent(partsOfMessage[2]));
                    }
                    else {
                        result.setMessage("error: Invalid auth token");
                    }


                    if(result.isSuccess()) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    }
                    else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    }

                    data = gson.toJson(result);
                    OutputStream responseBody = exchange.getResponseBody();
                    OutputStreamWriter sw = new OutputStreamWriter(responseBody);
                    sw.write(data);
                    sw.flush();
                    responseBody.close();
                    exchange.getResponseBody().close();
                    success = true;
                    db.closeConnection(true);
                    conn.close();
                }
                if(!success) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    exchange.getResponseBody().close();
                }
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }
}
