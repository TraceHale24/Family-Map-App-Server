package Handlers;

import Dao.DataAccessException;
import Request.RegisterReq;
import Results.Result;
import Services.RegisterService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.sql.SQLException;


public class RegisterHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        Gson gson = new Gson();
        RegisterService service = new RegisterService();

        try {
            if(exchange.getRequestMethod().toLowerCase().equals("post")) {
                InputStream reqBody = exchange.getRequestBody();
                //Read in the String
                StringBuilder sb = new StringBuilder();
                InputStreamReader sr = new InputStreamReader(reqBody);
                char[] buf = new char[1024];
                int len;
                while((len = sr.read(buf)) > 0) {
                    sb.append(buf, 0, len);
                }

                RegisterReq request = gson.fromJson(sb.toString(), RegisterReq.class);
                Result response = service.register(request);
                String data = gson.toJson(response);

                if(response.isSuccess()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }


                OutputStream respBody = exchange.getResponseBody();
                OutputStreamWriter sw = new OutputStreamWriter(respBody);
                sw.write(data);
                sw.flush();
                respBody.close();
                exchange.getResponseBody().close();
                success = true;
            }

            if(!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (IOException | DataAccessException | SQLException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }
}
