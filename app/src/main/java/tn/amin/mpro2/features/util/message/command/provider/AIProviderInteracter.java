package tn.amin.mpro2.features.util.message.command.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import tn.amin.mpro2.debug.Logger;

public class AIProviderInteracter {
    public static String getCompletion(Context context, String model, String provider, String authData, String prompt) {
        Uri CONTENT_URI = Uri.parse("content://tn.amin.mproai.AI/data");

        // Query the content provider
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, new String[] {
                model,
                provider,
                prompt,
                authData,
        }, null, null, null);

        Logger.verbose("Cursor: " + cursor);

        // Process the result if the cursor is not null
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("message");
            if (columnIndex != -1) {
                String message = cursor.getString(columnIndex);
                Logger.verbose("Received: " + message);
                return message;
            }
            cursor.close();
        }

        return null;
    }
}
