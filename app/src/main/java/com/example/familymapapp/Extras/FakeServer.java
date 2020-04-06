package com.example.familymapapp.Extras;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import java.net.URL;

import Request.LoginReq;
import Request.RegisterReq;
import Results.EventResult;
import Results.LoginResult;
import Results.PersonIDResult;
import Results.PersonResult;
import Results.RegisterResult;
import Results.Result;

public class FakeServer {

    public Result login(LoginReq request, String ipAddress, String portNumber) {
        Gson gson = new Gson();
        String unpackedString = gson.toJson(request);

        try {
            URL url = new URL("http://" + ipAddress + ":" + portNumber + "/user/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.connect();
            OutputStream requestBody = connection.getOutputStream();

            try {
                writeString(unpackedString, requestBody);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get response body input stream
                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, LoginResult.class);
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {

                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, Result.class);
            } else {
                Result nullResult = new Result();
                nullResult.setMessage("Server Error");
                nullResult.setSuccess(false);

                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, Result.class);
            }
        } catch (Exception e) {
            Log.e("HttpClient", e.getMessage(), e);
        }

        return null;
    }


    public Result register(RegisterReq request, String ipAddress, String portNumber) {
        Gson gson = new Gson();
        String unpackedString = gson.toJson(request);

        try {
            URL url = new URL("http://" + ipAddress + ":" + portNumber + "/user/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.connect();
            OutputStream requestBody = connection.getOutputStream();

            try {
                writeString(unpackedString, requestBody);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get response body input stream
                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, RegisterResult.class);
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {

                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, RegisterResult.class);
            } else {
                Result nullResult = new Result();
                nullResult.setMessage("Server Error");
                nullResult.setSuccess(false);

                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, RegisterResult.class);
            }
        } catch (Exception e) {
            Log.e("HttpClient", e.getMessage(), e);
        }

        return null;
    }


    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);

        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }

        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();

    }

    public Result persons(String ipAddress, String portNumber, String authToken) {
        Gson gson = new Gson();
        try {
            URL url = new URL("http://" + ipAddress + ":" + portNumber + "/person");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", authToken);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get response body input stream
                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, PersonResult.class);
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {

                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);
                System.out.println("It broke");
                return gson.fromJson(responseBodyData, PersonResult.class);
            } else {
                Result nullResult = new Result();
                nullResult.setMessage("Server Error");
                nullResult.setSuccess(false);

                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, PersonResult.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Result events(String ipAddress, String portNumber, String authToken) {
        Gson gson = new Gson();
        try {
            URL url = new URL("http://" + ipAddress + ":" + portNumber + "/event");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", authToken);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get response body input stream
                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, EventResult.class);
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {

                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);
                System.out.println("It broke");
                return gson.fromJson(responseBodyData, EventResult.class);
            } else {
                Result nullResult = new Result();
                nullResult.setMessage("Server Error");
                nullResult.setSuccess(false);

                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, EventResult.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Result personID(String ipAddress, String portNumber, String authToken, String personID) {
        Gson gson = new Gson();
        try {
            URL url = new URL("http://" + ipAddress + ":" + portNumber + "/person/" + personID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", authToken);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get response body input stream
                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, PersonIDResult.class);
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {

                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);
                System.out.println("It broke");
                return gson.fromJson(responseBodyData, PersonIDResult.class);
            } else {
                Result nullResult = new Result();
                nullResult.setMessage("Server Error");
                nullResult.setSuccess(false);

                InputStream responseBody = connection.getInputStream();
                String responseBodyData = readString(responseBody);

                return gson.fromJson(responseBodyData, PersonIDResult.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
