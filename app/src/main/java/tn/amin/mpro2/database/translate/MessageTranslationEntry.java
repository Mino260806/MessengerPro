package tn.amin.mpro2.database.translate;

public class MessageTranslationEntry {
    public String conversationId;
    public String messageId;
    public String translation;
    public Long sentTime;

    public MessageTranslationEntry(String conversationId, String messageId, String translation, Long sentTime) {
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.translation = translation;
        this.sentTime = sentTime;
    }

    public static class DbInfo {
        public static final String TABLE_NAME = "translation";

        public final static String COLUMN_CONV_ID = "conv_id";
        public final static String COLUMN_INDEX = "index";
        public final static String COLUMN_MSG_ID = "msg_id";
        public final static String COLUMN_TRANSLATION = "translation";

        public static String getTableColumns() {
            return COLUMN_CONV_ID + "TEXT," +
                    COLUMN_INDEX + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_MSG_ID + "TEXT," +
                    COLUMN_TRANSLATION + "TEXT";
        }
    }
}
