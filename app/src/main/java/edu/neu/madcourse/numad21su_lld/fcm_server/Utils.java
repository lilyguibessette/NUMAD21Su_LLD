package edu.neu.madcourse.numad21su_lld.fcm_server;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Reference: Firebase Demo 3 from classwork

public class Utils {

    public static String convertStreamToString(InputStream inputStream) {
        // creating a new string builder
        StringBuilder stringBuilder = new StringBuilder();

        // try to read the stream
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String len;

            // while there is still content, add to the string builder
            while ((len = bufferedReader.readLine()) != null) {
                stringBuilder.append(len);
            }

            // then close and return the string builder
            bufferedReader.close();
            return stringBuilder.toString().replace(",", ",\n");

        // look for any exceptions and print them out
        } catch (Exception e) {
            e.printStackTrace();
        }

        // otherwise return an empty string
        return "";
    }

    public static String fcmHttpConnection(String serverToken, JSONObject jsonObject) {

        // try to open the HTTP connection
        try {
            // Open the HTTP connection and send the payload
            HttpURLConnection conn = (HttpURLConnection) new URL("https://fcm.googleapis.com/fcm/send").openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", serverToken);
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            return convertStreamToString(inputStream);

        // look for IOExceptions - if they happen, return NULL
        } catch (IOException e) {
            return "NULL";
        }

    }
}
