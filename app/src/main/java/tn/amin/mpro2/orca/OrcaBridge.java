package tn.amin.mpro2.orca;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.Map;

import tn.amin.mpro2.constants.OrcaInfo;
import tn.amin.mpro2.debug.Logger;

public class OrcaBridge {
    public static final String ACTION_SEND_MESSAGE = "tn.amin.mpro2.action.SEND_MESSAGE";
    public static final String ACTION_REACT_TO_MESSAGE = "tn.amin.mpro2.action.REACT_TO_MESSAGE";
    public static final String ACTION_PREFERENCES_RELOAD = "tn.amin.mpro2.action.PREFERENCES_RELOAD";

    public static final String PARAM_ACTION = "tn.amin.mpro2.extra.ACTION";
    public static final String PARAM_MESSAGE = "tn.amin.mpro2.extra.MESSAGE";
    public static final String PARAM_MESSAGE_ID = "tn.amin.mpro2.extra.MESSAGE_ID";
    public static final String PARAM_REPLY_MESSAGE_ID = "tn.amin.mpro2.extra.REPLY_MESSAGE_ID";
    public static final String PARAM_REACTION = "tn.amin.mpro2.extra.REACTION";
    public static final String PARAM_THREAD_KEY = "tn.amin.mpro2.extra.THREAD_KEY";
    public static final String PARAM_PREFERENCES_MAP = "tn.amin.mpro2.extra.PREFERENCES_MAP";

    public static void reloadPreferences(Context context, Map<String, Map<String, ?>> prefMap) {
        Intent intent = getIntent(context, ACTION_PREFERENCES_RELOAD);
        intent.putExtra(PARAM_PREFERENCES_MAP, (Serializable) prefMap);
        context.sendBroadcast(intent);
    }

    public static void sendMessage(Context context, String message, long threadKey) {
        sendMessage(context, message, null, threadKey);
    }

    public static void sendMessage(Context context, String message, String replyMessageId, long threadKey) {
        Logger.logNoXposed("Sending message \"" + message + "\" to user " + threadKey);

        Intent intent = getIntent(context, ACTION_SEND_MESSAGE);
        intent.putExtra(PARAM_MESSAGE, message);
        intent.putExtra(PARAM_THREAD_KEY, threadKey);
        if (replyMessageId != null)
            intent.putExtra(PARAM_REPLY_MESSAGE_ID, threadKey);
        context.sendBroadcast(intent);
    }

    public static void reactToMessage(Context context, String reaction, String messageId, long threadKey) {
        Logger.logNoXposed("Reacting with \"" + reaction + "\" to user " + threadKey);

        Intent intent = getIntent(context, ACTION_REACT_TO_MESSAGE);
        intent.putExtra(PARAM_REACTION, reaction);
        intent.putExtra(PARAM_MESSAGE_ID, messageId);
        intent.putExtra(PARAM_THREAD_KEY, threadKey);

        context.sendBroadcast(intent);
    }

    @SuppressWarnings("unchecked")
    public static void handleIntent(Intent intent, ActionCallback callback) {
        if (intent != null) {
            String action = intent.getStringExtra(PARAM_ACTION);
            if (action != null) {
                intent.removeExtra(PARAM_ACTION);

                long threadKey;
                String message;
                String messageId;
                String reaction;
                String replyMessageId;
                switch (action) {
                    case ACTION_SEND_MESSAGE:
                        message = intent.getStringExtra(PARAM_MESSAGE);
                        replyMessageId = intent.getStringExtra(PARAM_REPLY_MESSAGE_ID);
                        threadKey = intent.getLongExtra(PARAM_THREAD_KEY, -1);

                        if (replyMessageId != null && replyMessageId.trim().isEmpty()) {
                            replyMessageId = null;
                        }

                        if (message != null && threadKey != -1) {
                            callback.sendMessage(message, replyMessageId, threadKey);
                        }

                        break;

                    case ACTION_REACT_TO_MESSAGE:
                        reaction = intent.getStringExtra(PARAM_REACTION);
                        messageId = intent.getStringExtra(PARAM_MESSAGE_ID);
                        threadKey = intent.getLongExtra(PARAM_THREAD_KEY, -1);

                        if (reaction != null && messageId != null && threadKey != -1) {
                            callback.reactToMessage(reaction, messageId, threadKey);
                        }

                        break;

                    case ACTION_PREFERENCES_RELOAD:
                        Map<String, Map<String, ?>> preferences = (Map<String, Map<String, ?>>) intent.getSerializableExtra(PARAM_PREFERENCES_MAP);

                        if (preferences != null) {
                            callback.reloadPreferences(preferences);
                        }

                        break;
                }
            }
        }
    }

    private static Intent getIntent(Context context, String action) {
        Intent intent = new Intent();
        intent.putExtra(PARAM_ACTION, action);
        intent.setComponent(new ComponentName(OrcaInfo.ORCA_PACKAGE_NAME, OrcaInfo.ORCA_SAMPLE_EXPORTED_RECEIVER));
        return intent;
    }

    public interface ActionCallback {
        void sendMessage(String message, String replyMessageId, long threadKey);
        void reactToMessage(String reaction, String messageId, long threadKey);
        void reloadPreferences(Map<String, Map<String, ?>> prefMap);
    }
}
