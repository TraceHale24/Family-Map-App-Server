package Handlers;

import Dao.*;
import Model.Person;
import Results.Result;
import Services.PersonIDService;
import Services.PersonService;
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

public class PersonHandler implements HttpHandler {
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
        PersonDAO pDao = new PersonDAO(conn);
        UserDAO uDao = new UserDAO(conn);
        String data = "";
        Result result = new Result();

        try {
            if(exchange.getRequestMethod().toLowerCase().equals("get")) {
                Headers required = exchange.getRequestHeaders();
                if(required.containsKey("Authorization")) {
                    String authToken = required.getFirst("Authorization");
                    String userName = aDao.getUserName(authToken);
                    URI uri = exchange.getRequestURI();
                    String[] partsOfMessage = uri.getPath().split("/");

                    if(aDao.validate(authToken, userName) && partsOfMessage.length == 2) {
                        PersonService service = new PersonService();
                        result = service.personService(userName);
                        result.setSuccess(true);
                    }

                    else if(aDao.validate(authToken, userName) && partsOfMessage.length == 3 && pDao.getPerson(partsOfMessage[2]).getUsername().equals(userName)) {
                        PersonIDService service = new PersonIDService();
                        Person temp = pDao.getPerson(partsOfMessage[2]);
                        if(temp == null) {
                            result.setMessage("error: Invalid Person ID, try again!");
                        }
                        else {
                            result = service.run(temp);
                            result.setSuccess(true);
                        }
                    }

                    else {
                        result.setMessage("error: Authorization Token Failed; You can not view this data!");
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
