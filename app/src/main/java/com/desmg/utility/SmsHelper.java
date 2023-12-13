/**
 * Copyright (C) 2023 DESMG
 * All Rights Reserved.
 */

package com.desmg.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SmsHelper {
    public static String getAllSms(Context ctx) {
        JSONArray jsonArray = new JSONArray();
        try {
            Uri uri = Telephony.Sms.Inbox.CONTENT_URI;
            Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
            if (Objects.requireNonNull(cursor).moveToFirst()) {
                do {
                    int intDate = cursor.getColumnIndex(Telephony.Sms.DATE);
                    String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)).format(new Date(cursor.getLong(intDate)));

                    int intAddress = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
                    String address = cursor.getString(intAddress);

                    int intBody = cursor.getColumnIndex(Telephony.Sms.BODY);
                    String body = cursor.getString(intBody);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("date", date);
                        jsonObject.put("address", address);
                        jsonObject.put("body", body);
                    } catch (JSONException e) {
                        Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                    }
                    jsonArray.put(jsonObject);
                } while (cursor.moveToNext());
            } else {
                return null;
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        } catch (SQLiteException e) {
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
        }
        return jsonArray.toString().replace("\\/", "/");
    }
}
