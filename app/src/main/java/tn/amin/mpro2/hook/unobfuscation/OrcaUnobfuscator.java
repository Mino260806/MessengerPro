package tn.amin.mpro2.hook.unobfuscation;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.github.neonorbit.dexplore.DexFactory;
import io.github.neonorbit.dexplore.Dexplore;
import io.github.neonorbit.dexplore.filter.ClassFilter;
import io.github.neonorbit.dexplore.filter.DexFilter;
import io.github.neonorbit.dexplore.filter.MethodFilter;
import io.github.neonorbit.dexplore.filter.ReferenceTypes;
import io.github.neonorbit.dexplore.reference.FieldRefData;
import io.github.neonorbit.dexplore.result.ClassData;
import io.github.neonorbit.dexplore.result.FieldData;
import io.github.neonorbit.dexplore.result.MethodData;
import io.github.neonorbit.dexplore.util.DexLog;
import tn.amin.mpro2.constants.OrcaInfo;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.file.StorageConstants;

public class OrcaUnobfuscator {
    private Dexplore mDexplore;
    private String mPath;
    private ClassLoader mClassLoader;
    private boolean mSearchAgain = true;

    private SharedPreferences.Editor mEditor = null;
    private SharedPreferences mPref = null;

    private final HashMap<String, Class<?>> mUnobfuscatedClasses = new HashMap<>();
    private final HashMap<String, Method> mUnobfuscatedMethods = new HashMap<>();
    private final HashMap<String, Field> mUnobfuscatedFields = new HashMap<>();
    private final HashMap<String, Integer> mUnobfuscatedApis = new HashMap<>();

    public static final String CLASS_TYPING_INDICATOR_DISPATCHER = "TypingIndicatorDispatcher";
    public static final String METHOD_MESSAGE_GETTEXT = "Message/getText";
    public static final String FIELD_MESSAGE_THREADKEY = "Message-threadKey";
    public static final String FIELD_MESSAGE_ID = "Message-id";
    public static final String API_NOTIFICATION = "API_NOTIFICATION";
    public static final String API_CONVERSATION_ENTER = "API_CONVERSATION_ENTER";
    public static final String API_CONVERSATION_LEAVE = "API_CONVERSATION_LEAVE";
    public static final String API_MESSAGE_SEEN = "API_MESSAGE_SEEN";

    public OrcaUnobfuscator(Context context, String path, ClassLoader classLoader, boolean searchAgain) {
        mClassLoader = classLoader;
        mPath = path;
        mSearchAgain = searchAgain;
        mPref = context.getSharedPreferences(StorageConstants.unobfPrefName, Context.MODE_PRIVATE);
        mEditor = mPref.edit();

        if (mSearchAgain) {
            Logger.verbose("Creating Dexplore instance");
            DexLog.enable();
            mDexplore = DexFactory.load(mPath);
        }

        reloadAllInternal();

        Logger.info("Successfully unobfuscated all components !");
        // TODO use QueryBatch
    }

    public Class<?> loadNotificationEngine() {
        return loadClass("NotificationEngine", new ClassFilter.Builder()
                .setReferenceTypes(ReferenceTypes.builder().addString().build())
                .setReferenceFilter(pool ->
                        pool.contains("[Notification engine internal error] it missed one of mandatory fields")
                ).build());
    }

    public Class<?> loadTypingIndicatorDispatcher() {
        return loadClass(CLASS_TYPING_INDICATOR_DISPATCHER, new ClassFilter.Builder()
                .setReferenceTypes(ReferenceTypes.builder().addString().addMethodWithDetails().build())
                .setReferenceFilter(pool ->
                        pool.stringsContain("/t_st") &&
                        pool.methodSignaturesContain("java.lang.System.arraycopy(java.lang.Object,int,java.lang.Object,int,int):void"))
                .build());
    }

    public Method loadMessageMethod(String attributeName) {
        return loadMethod("Message/" + attributeName,
                new ClassFilter.Builder().setClasses("com.facebook.messaging.model.messages.Message").build(),
                new MethodFilter.Builder()
                .setReferenceTypes(ReferenceTypes.builder().addString().build())
                .setReferenceFilter(pool -> pool.contains("text"))
                .build());
    }

    public Field loadMessageFieldThreadKey() {
        return loadField(FIELD_MESSAGE_THREADKEY, () -> {
            ClassData NewMessageNotification = mDexplore.findClass(DexFilter.MATCH_ALL, ClassFilter.ofClass("com.facebook.messaging.notify.type.NewMessageNotification"));
            if (NewMessageNotification == null) {
                Logger.error("NewMessageNotification is null");
                return null;
            }

            List<FieldRefData> refData = NewMessageNotification.getReferencePool().getFieldSection().stream()
                    .filter(fieldRefData ->
                            fieldRefData.getDeclaringClass().equals("com.facebook.messaging.model.messages.Message") &&
                            fieldRefData.getType().equals("com.facebook.messaging.model.threadkey.ThreadKey"))
                    .collect(Collectors.toList());
            if (refData.size() == 0) {
                Logger.error("Could not find Message.threadKey");
                return null;
            }

            return refData.get(0);
        });
    }

    private int loadAPINotification() {
        if (!mPref.contains(API_NOTIFICATION))
            mPref.edit().putString(API_NOTIFICATION, "25").apply();
        return Integer.parseInt(mPref.getString(API_NOTIFICATION, "25"));
    }

    private int loadAPIConversationEnter() {
        if (!mPref.contains(API_CONVERSATION_ENTER))
            mPref.edit().putString(API_CONVERSATION_ENTER, "5").apply();
        return Integer.parseInt(mPref.getString(API_CONVERSATION_ENTER, "5"));
    }

    private int loadAPIConversationLeave() {
        if (!mPref.contains(API_CONVERSATION_LEAVE))
            mPref.edit().putString(API_CONVERSATION_LEAVE, "7").apply();
        return Integer.parseInt(mPref.getString(API_CONVERSATION_LEAVE, "7"));
    }

    private int loadAPIMessageSeen() {
        return NumberUtils.toInt(mPref.getString(API_MESSAGE_SEEN, null), -1);
    }

    public Field loadMessageFieldId() {
        return loadField(FIELD_MESSAGE_ID, () -> {
            ClassData MsysThreadsCache = mDexplore.findClass(DexFilter.MATCH_ALL,
                    ClassFilter.builder()
                            .setReferenceTypes(ReferenceTypes.builder().addString().build())
                            .setReferenceFilter(pool -> pool.stringsContain("MsysThreadsCache"))
                            .build());
            if (MsysThreadsCache == null) {
                Logger.error("MsysThreadsCache is null");
                return null;
            }

            List<FieldRefData> refData = MsysThreadsCache.getReferencePool().getFieldSection().stream()
                    .filter(fieldRefData ->
                            fieldRefData.getDeclaringClass().equals("com.facebook.messaging.model.messages.Message") &&
                                    fieldRefData.getType().equals("java.lang.String"))
                    .collect(Collectors.toList());

            if (refData.size() == 0) {
                Logger.error("Could not find Message.id");
                return null;
            }

            return refData.get(0);
        });
    }

    private Class<?> loadClass(String simpleName, ClassFilter classFilter) {
        ClassData classData;
        if (mSearchAgain) {
            classData = mDexplore.findClass(DexFilter.MATCH_ALL, classFilter);

            if (classData == null)
                return null;

            mEditor.putString(simpleName, classData.serialize());
        } else {
            String raw = mPref.getString(simpleName, null);

            if (raw == null)
                return null;

            classData = ClassData.deserialize(raw);
        }

        return classData.loadClass(mClassLoader);
    }

    private Method loadMethod(String simpleName, ClassFilter classFilter, MethodFilter methodFilter) {
        MethodData methodData;
        if (mSearchAgain) {
            methodData = mDexplore.findMethod(DexFilter.MATCH_ALL, classFilter, methodFilter);

            if (methodData == null)
                return null;

            mEditor.putString(simpleName, methodData.serialize());
        } else {
            String raw = mPref.getString(simpleName, null);

            if (raw == null)
                return null;

            methodData = MethodData.deserialize(raw);
        }

        return methodData.loadMethod(mClassLoader);
    }

    private Field loadField(String simpleName, Supplier<FieldRefData> supplier) {
        FieldData fieldData;
        if (mSearchAgain) {
            FieldRefData refData = supplier.get();

            if (refData == null)
                return null;

            String serialized = "f:" + refData.getDeclaringClass() + ":" + refData.getName() + ":" + refData.getType();
            mEditor.putString(simpleName, serialized);
            fieldData = FieldData.deserialize(serialized);
        } else {
            String raw = mPref.getString(simpleName, null);

            if (raw == null)
                return null;

            fieldData = FieldData.deserialize(raw);
        }

        return fieldData.loadField(mClassLoader);
    }

    public void save() {
        if (mEditor != null) mEditor.apply();
    }

    public Class<?> getClass(String name) {
        return mUnobfuscatedClasses.get(name);
    }

    public Method getMethod(String name) {
        return mUnobfuscatedMethods.get(name);
    }

    public Field getField(String name) {
        return mUnobfuscatedFields.get(name);
    }

    public Integer getAPICode(String name) {
        return mUnobfuscatedApis.get(name);
    }

    public SharedPreferences getPreferences() {
        return mPref;
    }

    public void reloadAll() {
        mSearchAgain = false;

        reloadAllInternal();
    }

    private void reloadAllInternal() {
        Logger.verbose("Loading class " + CLASS_TYPING_INDICATOR_DISPATCHER);
        mUnobfuscatedClasses.put(CLASS_TYPING_INDICATOR_DISPATCHER, loadTypingIndicatorDispatcher());
//        mUnobfuscatedMethods.put(METHOD_MESSAGE_GETTEXT, loadMessageMethod("getText"));
//        mUnobfuscatedFields.put(FIELD_MESSAGE_THREADKEY, loadMessageFieldThreadKey());
//        mUnobfuscatedFields.put(FIELD_MESSAGE_ID, loadMessageFieldId());

        Logger.verbose("Loading api " + API_NOTIFICATION);
        mUnobfuscatedApis.put(API_NOTIFICATION, loadAPINotification());
        Logger.verbose("Loading api " + API_CONVERSATION_ENTER);
        mUnobfuscatedApis.put(API_CONVERSATION_ENTER, loadAPIConversationEnter());
        Logger.verbose("Loading api " + API_CONVERSATION_LEAVE);
        mUnobfuscatedApis.put(API_CONVERSATION_LEAVE, loadAPIConversationLeave());
        Logger.verbose("Loading api " + API_MESSAGE_SEEN);
        mUnobfuscatedApis.put(API_MESSAGE_SEEN, loadAPIMessageSeen());
    }
}
