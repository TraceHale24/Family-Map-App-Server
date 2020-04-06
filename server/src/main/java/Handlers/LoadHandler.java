package Handlers;

import Dao.DataAccessException;
import Request.LoadReq;
import Request.RegisterReq;
import Results.Result;
import Services.ClearService;
import Services.LoadService;
import Services.RegisterService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import result.LoadResult;

import java.io.*;
import java.net.HttpURLConnection;
import java.sql.SQLException;

public class LoadHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        Gson gson = new Gson();
        LoadService service = new LoadService();

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



                LoadReq request = gson.fromJson(sb.toString(), LoadReq.class);
                Result response = service.load(request);
                String data = gson.toJson(response);
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
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
