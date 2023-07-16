package tn.amin.mpro2.database.translate;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tn.amin.mpro2.debug.Logger;

public class MessageTranslationDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mpro_translation.db";
    private static final int DATABASE_VERSION = 1;

    public MessageTranslationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MessageTranslationEntry.DbInfo.TABLE_NAME + " (" + MessageTranslationEntry.DbInfo.getTableColumns() + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MessageTranslationEntry.DbInfo.TABLE_NAME);
        onCreate(db);
    }

    public Map<String, String> getMessageTranslations(String conversationId) {
        Map<String, String> messageTranslations = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    MessageTranslationEntry.DbInfo.TABLE_NAME,
                    null,
                    MessageTranslationEntry.DbInfo.COLUMN_CONV_ID + " = ?",
                    new String[] { conversationId },
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String msgId = cursor.getString(cursor.getColumnIndex(MessageTranslationEntry.DbInfo.COLUMN_MSG_ID));
                    @SuppressLint("Range") String translation = cursor.getString(cursor.getColumnIndex(MessageTranslationEntry.DbInfo.COLUMN_TRANSLATION));
                    messageTranslations.put(msgId, translation);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return messageTranslations;
    }

    public void addTranslation(String conversationId, String messageId, String translation) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(MessageTranslationEntry.DbInfo.COLUMN_MSG_ID, messageId);
            values.put(MessageTranslationEntry.DbInfo.COLUMN_CONV_ID, conversationId);
            values.put(MessageTranslationEntry.DbInfo.COLUMN_TRANSLATION, translation);
            db.insert(MessageTranslationEntry.DbInfo.TABLE_NAME, null, values);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            db.close();
        }

        // TODO delete messages with least id until translation count in conversation id < 10
    }
}
