package org.openhab.binding.mopidy.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import org.openhab.binding.mopidy.api.model.JSONRPC;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class HttpConnectionHelper {

    public static String executePost(String url, JSONRPC o) {
        Type type = new TypeToken<JSONRPC>() {
        }.getType();
        Gson gson = new Gson();
        String jsonStr = null;
        if (o != null) {
            jsonStr = gson.toJson(o, type);
        }
        // jsonStr =
        // "{\"method\":\"core.library.search\",\"params\":{\"query\":{\"any\":[\"Dire\"]}},\"jsonrpc\":\"2.0\",\"id\":\"2\"}";

        System.out.println("Sending " + jsonStr);
        String result = HttpConnectionHelper.executePostRaw(url, jsonStr);

        return result;

    }

    public static JsonObject executePostJson(String url, JSONRPC json) {
        String result = executePost(url, json);

        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(result).getAsJsonObject();
        return o;
    }

    public static String executePostRaw(String url, String body) {
        return execute(url, "POST", body);
    }

    public static String executeGetRaw(String url, String body) {
        return execute(url, "GET", body);
    }

    public static String execute(String targetURL, String method, String body) {
        HttpURLConnection connection = null;

        try {
            // Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // connection.setRequestProperty("Content-Length",
            // Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            if (body != null) {
                connection.setDoOutput(true);
            }

            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            if (body != null) {
                wr.writeBytes(body);
            }
            wr.close();

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
