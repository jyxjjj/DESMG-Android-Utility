/**
 * Copyright (C) 2023 DESMG
 * All Rights Reserved.
 */

package com.desmg.utility;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;


public class Sms2Telegram {
    public static void sendJson(String deviceId, String json) {
        Thread t = new Thread(() -> run(deviceId, json));
        t.start();
    }

    private static void run(String deviceId, String json) {
        try {
            Log.d("Sms2Telegram", "Sending JSON: " + json);
            byte[] data = compress(json);
            URL url = new URL(String.format("%s?key=%s&device_id=%s", BuildConfig.SERVER_URL, BuildConfig.API_KEY, deviceId));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Encoding", "gzip");
            connection.setRequestProperty("User-Agent", "com.desmg.utility/" + BuildConfig.VERSION_NAME);
            connection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(data);
            dataOutputStream.flush();
            dataOutputStream.close();
            Log.d("Sms2Telegram", "Response code: " + connection.getResponseCode());
            connection.disconnect();
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }

    private static byte[] compress(String data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(data.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return out.toByteArray();
    }

}
