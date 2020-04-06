package Handlers;

import Dao.DataAccessException;
import Request.LoginReq;
import Results.LoginResult;
import Results.Result;
import Services.LoginService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import request.LoginRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.sql.SQLException;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        Gson gson = new Gson();
        LoginService service = new LoginService();

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                InputStream reqBody = exchange.getRequestBody();
                InputStreamReader sr = new InputStreamReader(reqBody);

                StringBuilder sb = new StringBuilder();
                char[] buf = new char[1024];
                int len;
                while ((len = sr.read(buf)) > 0) {
                    sb.append(buf, 0, len);
                }

                LoginReq request = gson.fromJson(sb.toString(), LoginReq.class);
                Result result = service.login(request);


                if(!result.isSuccess()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }


                String responseData = gson.toJson(result);
                OutputStream respBody = exchange.getResponseBody();
                OutputStreamWriter sw = new OutputStreamWriter(respBody);
                sw.write(responseData);
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
