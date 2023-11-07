package tn.amin.mpro2.hook.unobfuscation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;

import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
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
import tn.amin.mpro2.constants.OrcaClassNames;
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
    public static final String METHOD_MESSAGES_DECODER_DECODE = "MessagesDecoder/decode";
    public static final String METHOD_THREAD_THEME_INFO_FACTORY_CREATE = "ThreadThemeInfoFactory/create";
    public static final String FIELD_MESSAGE_THREADKEY = "Message-threadKey";
    public static final String FIELD_MESSAGE_ID = "Message-id";
    public static final String FIELD_MESSAGE_TEXT = "Message-text";
    public static final String FIELD_MESSAGE_PARTICIPANT_INFO = "Message-participantInfo";
    public static final String FIELD_SECRET_STRING_CONTENT = "SecretString-content";
    public static final String FIELD_SECRET_STRING_STARS = "SecretString-stars";
    public static final String FIELD_MESSAGES_COLLECTION_MESSAGES = "MessagesCollection-messages";
    public static final String FIELD_PARTICIPANT_INFO_USER_KEY = "ParticipantInfo-userKey";
    public static final String API_NOTIFICATION = "API_NOTIFICATION";
    public static final String API_CONVERSATION_ENTER = "API_CONVERSATION_ENTER";
    public static final String API_CONVERSATION_LEAVE = "API_CONVERSATION_LEAVE";
    public static final String API_MESSAGE_SEEN = "API_MESSAGE_SEEN";
    public static final String METHOD_ADS_SUPPLIER = "Ads/supplier";

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

    private Method loadMessagesDecoderDecode() {
        return loadMethod(METHOD_MESSAGES_DECODER_DECODE,
                new ClassFilter.Builder().setClasses(OrcaClassNames.MESSAGES_DECODER).build(),
                new MethodFilter.Builder()
                        .setReferenceTypes(ReferenceTypes.builder().addString().build())
                        .setReferenceFilter(pool -> pool.contains("messageStreamingState") ||
                                pool.contains("Magic words offsets %s and length %s mismatch.") ||
                                pool.contains("typing_indicator:"))
                        .build());
    }

    private Method LoadAdsSupplierMethod() {
        return loadMethod(METHOD_ADS_SUPPLIER,
                new ClassFilter.Builder()
                        .setClasses("com.facebook.messaging.business.inboxads.plugins.inboxads.itemsupplier.InboxAdsItemSupplierImplementation")
                        .build(),
                new MethodFilter.Builder()
                        .setReferenceTypes(ReferenceTypes.builder().addString().build())
                        .setReferenceFilter(pool ->
                                pool.contains("ads_load_begin") ||
                                        pool.contains("inbox_ads_fetch_start"))
                        .setReturnType("void")
                        .setModifiers(Modifier.PUBLIC | Modifier.STATIC)
                        .build());
    }


    private Method loadThreadThemeInfoCreate() {
        return loadMethod(METHOD_THREAD_THEME_INFO_FACTORY_CREATE,
                new ClassFilter.Builder()
                        .setReferenceTypes(ReferenceTypes.builder().addString().build())
                        .setReferenceFilter(pool -> pool.stringsContain("COLOR_GRADIENT"))
                        .build(),
                new MethodFilter.Builder()
                        .setModifiers(Modifier.STATIC)
                        .setReturnType(OrcaClassNames.THREAD_THEME_INFO)
                        .build());
    }

    public Method loadMessageMethod(String attributeName) {
        return loadMethod("Message/get" + Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1),
                new ClassFilter.Builder().setClasses(OrcaClassNames.MESSAGE).build(),
                new MethodFilter.Builder()
                        .setReferenceTypes(ReferenceTypes.builder().addString().build())
                        .setReferenceFilter(pool -> pool.contains(attributeName))
                        .build());
    }

    public Field loadMessageFieldThreadKey() {
        return loadField(FIELD_MESSAGE_THREADKEY, () -> {
            ClassData NewMessageNotification = mDexplore.findClass(
                    DexFilter.MATCH_ALL, ClassFilter.ofClass(OrcaClassNames.NEW_MESSAGE_NOTIFICATION));
            if (NewMessageNotification == null) {
                Logger.error("NewMessageNotification is null");
                return null;
            }

            List<FieldRefData> refData = NewMessageNotification.getReferencePool().getFieldSection().stream()
                    .filter(fieldRefData ->
                            fieldRefData.getDeclaringClass().equals(OrcaClassNames.MESSAGE) &&
                                    fieldRefData.getType().equals(OrcaClassNames.THREAD_KEY))
                    .collect(Collectors.toList());
            if (refData.size() == 0) {
                Logger.error("Could not find Message.threadKey");
                return null;
            }

            return refData.get(0);
        });
    }

    private Field loadMessageFieldText() {
        return loadField(FIELD_MESSAGE_TEXT, () -> {
            MethodData writeToParcel = mDexplore.findMethod(
                    DexFilter.MATCH_ALL,
                    ClassFilter.ofClass(OrcaClassNames.MESSAGE),
                    MethodFilter.ofMethod("writeToParcel", Arrays.asList(Parcel.class.getName(), "int")));

            if (writeToParcel == null) {
                Logger.error("Could not get ReferencePool for method Message.writeToParcel");
                return null;
            }

            List<FieldRefData> refDataWriteToParcel = writeToParcel.getReferencePool().getFieldSection();

            Method getTextMethod = mUnobfuscatedMethods.get(METHOD_MESSAGE_GETTEXT);
            if (getTextMethod == null) {
                Logger.error("Method.getText cannot be null, skipping field text");
                return null;
            }

            MethodData getText = mDexplore.findMethod(
                    DexFilter.MATCH_ALL,
                    ClassFilter.ofClass(OrcaClassNames.MESSAGE),
                    MethodFilter.ofMethod(getTextMethod.getName()));

            if (getText == null) {
                Logger.error("Could not get ReferencePool for method Message.getText");
                return null;
            }

            List<FieldRefData> refDataGetText = getText.getReferencePool().getFieldSection().stream()
                    .filter(fieldRefData -> {
                        return fieldRefData.getDeclaringClass().equals(OrcaClassNames.MESSAGE) &&
                                fieldRefData.getType().equals(OrcaClassNames.SECRET_STRING) &&
                                refDataWriteToParcel.contains(fieldRefData);
                    })
                    .collect(Collectors.toList());

            if (refDataGetText.size() != 1) {
                Logger.error("Expected to find 1 SecretString in Message.getText, got " + refDataGetText.size());
                return null;
            }

            return refDataGetText.get(0);
        });
    }

    private Field loadMessageParticipantInfo() {
        return loadField(FIELD_MESSAGE_PARTICIPANT_INFO, () -> {
            ClassData MessageRepliedTo = mDexplore.findClass(DexFilter.MATCH_ALL,
                    ClassFilter.ofClass(OrcaClassNames.MESSAGE_REPLIED_TO));

            if (MessageRepliedTo == null) {
                Logger.error("Unable to load class MessageRepliedTo");
                return null;
            }

            List<FieldRefData> refDataList = MessageRepliedTo.getReferencePool().getFieldSection().stream()
                    .filter(fieldRefData -> fieldRefData.getDeclaringClass().equals(OrcaClassNames.MESSAGE) &&
                            fieldRefData.getType().equals(OrcaClassNames.PARTICIPANT_INFO))
                    .collect(Collectors.toList());

            if (refDataList.size() == 0) {
                Logger.error("Expected to find at least 1 ParticipantInfo in MessageRepliedTo, got " + refDataList.size());
                return null;
            }

            return refDataList.get(0);
        });
    }

    private Field loadSecretStringFieldStars() {
        return loadField(FIELD_SECRET_STRING_STARS, () -> {
            MethodData SecretStringToString = mDexplore.findMethod(
                    DexFilter.MATCH_ALL,
                    ClassFilter.ofClass(OrcaClassNames.SECRET_STRING),
                    MethodFilter.ofMethod("toString"));

            if (SecretStringToString == null) {
                Logger.error("Failed to find method SecretString.toString");
                return null;
            }

            List<FieldRefData> refData = SecretStringToString.getReferencePool().getFieldSection().stream()
                    .filter(fieldRefData ->
                            fieldRefData.getDeclaringClass().equals(OrcaClassNames.SECRET_STRING) &&
                                    fieldRefData.getType().equals(String.class.getName()))
                    .collect(Collectors.toList());

            if (refData.size() != 1) {
                Logger.error("Expected to find 1 String in SecretString.toString, got " + refData.size());
                return null;
            }

            return refData.get(0);
        });
    }

    private Field loadSecretStringFieldContent() {
        return loadField(FIELD_SECRET_STRING_CONTENT, () -> {
            Field secretStringStars = mUnobfuscatedFields.get(FIELD_SECRET_STRING_STARS);
            if (secretStringStars == null) {
                Logger.error(FIELD_SECRET_STRING_STARS + " is null, skipping " + FIELD_SECRET_STRING_CONTENT);
            }

            ClassData SecretString = mDexplore.findClass(
                    DexFilter.MATCH_ALL,
                    ClassFilter.ofClass(OrcaClassNames.SECRET_STRING));

            if (SecretString == null) {
                Logger.error("Failed to find class SecretString");
                return null;
            }

            List<FieldRefData> refData = SecretString.getReferencePool().getFieldSection().stream()
                    .filter(fieldRefData -> fieldRefData.getDeclaringClass().equals(OrcaClassNames.SECRET_STRING) &&
                            fieldRefData.getType().equals(String.class.getName()) &&
                            !fieldRefData.getName().equals(secretStringStars.getName()))
                    .collect(Collectors.toList());

            if (refData.size() == 0) {
                Logger.error("Expected to find at least 1 String in SecretString, got " + refData.size());
                return null;
            }

            return refData.get(0);
        });
    }

    private Field loadMessagesCollectionMessages() {
        return loadField(FIELD_MESSAGES_COLLECTION_MESSAGES, () -> {
            ClassData MessagesCollection = mDexplore.findClass(
                    DexFilter.MATCH_ALL,
                    ClassFilter.ofClass(OrcaClassNames.MESSAGES_COLLECTION));

            if (MessagesCollection == null) {
                Logger.error("Failed to find class MessagesCollection");
                return null;
            }

            List<FieldRefData> refData = MessagesCollection.getReferencePool().getFieldSection().stream()
                    .filter(fieldRefData -> fieldRefData.getDeclaringClass().equals(OrcaClassNames.MESSAGES_COLLECTION) &&
                            fieldRefData.getType().contains("ImmutableList"))
                    .collect(Collectors.toList());

            if (refData.size() == 0) {
                Logger.error("Expected to find at least 1 ImmutableList in MessagesCollection, got " + refData.size());
                return null;
            }

            return refData.get(0);
        });
    }

    private Field loadParticipantInfoUserKey() {
        return loadField(FIELD_PARTICIPANT_INFO_USER_KEY, () -> {
            ClassData ParticipantInfo = mDexplore.findClass(
                    DexFilter.MATCH_ALL,
                    ClassFilter.ofClass(OrcaClassNames.PARTICIPANT_INFO));

            if (ParticipantInfo == null) {
                Logger.error("Failed to find class ParticipantInfo");
                return null;
            }

            List<FieldRefData> refData = ParticipantInfo.getReferencePool().getFieldSection().stream()
                    .filter(fieldRefData -> fieldRefData.getDeclaringClass().equals(OrcaClassNames.PARTICIPANT_INFO) &&
                            fieldRefData.getType().equals(OrcaClassNames.USER_KEY))
                    .collect(Collectors.toList());

            if (refData.size() == 0) {
                Logger.error("Expected to find at least 1 UserKey in ParticipantInfo, got " + refData.size());
                return null;
            }

            return refData.get(0);
        });
    }

    private int loadAPINotification() {
        if (!mPref.contains(API_NOTIFICATION))
            mPref.edit().putString(API_NOTIFICATION, "26").apply();
        return Integer.parseInt(mPref.getString(API_NOTIFICATION, "26"));
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
                            fieldRefData.getDeclaringClass().equals(OrcaClassNames.MESSAGE) &&
                                    fieldRefData.getType().equals(String.class.getName()))
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

        Logger.verbose("Loading method " + METHOD_MESSAGE_GETTEXT);
        mUnobfuscatedMethods.put(METHOD_MESSAGE_GETTEXT, loadMessageMethod("text"));
        Logger.verbose("Loading method " + METHOD_MESSAGES_DECODER_DECODE);
        mUnobfuscatedMethods.put(METHOD_MESSAGES_DECODER_DECODE, loadMessagesDecoderDecode());
        Logger.verbose("Loading method " + METHOD_THREAD_THEME_INFO_FACTORY_CREATE);
        mUnobfuscatedMethods.put(METHOD_THREAD_THEME_INFO_FACTORY_CREATE, loadThreadThemeInfoCreate());
        Logger.verbose("Method: " + getMethod(METHOD_THREAD_THEME_INFO_FACTORY_CREATE));
        Logger.verbose("Loading method " + METHOD_ADS_SUPPLIER);
        mUnobfuscatedMethods.put(METHOD_ADS_SUPPLIER, LoadAdsSupplierMethod());


        Logger.verbose("Loading field " + FIELD_MESSAGE_THREADKEY);
        mUnobfuscatedFields.put(FIELD_MESSAGE_THREADKEY, loadMessageFieldThreadKey());
        Logger.verbose("Loading field " + FIELD_MESSAGE_ID);
        mUnobfuscatedFields.put(FIELD_MESSAGE_ID, loadMessageFieldId());
        Logger.verbose("Loading field " + FIELD_MESSAGE_TEXT);
        mUnobfuscatedFields.put(FIELD_MESSAGE_TEXT, loadMessageFieldText());
        Logger.verbose("Loading field " + FIELD_MESSAGE_PARTICIPANT_INFO);
        mUnobfuscatedFields.put(FIELD_MESSAGE_PARTICIPANT_INFO, loadMessageParticipantInfo());
        Logger.verbose("Loading field " + FIELD_SECRET_STRING_STARS);
        mUnobfuscatedFields.put(FIELD_SECRET_STRING_STARS, loadSecretStringFieldStars());
        Logger.verbose("Loading field " + FIELD_SECRET_STRING_CONTENT);
        mUnobfuscatedFields.put(FIELD_SECRET_STRING_CONTENT, loadSecretStringFieldContent());
        Logger.verbose("Loading field " + FIELD_MESSAGES_COLLECTION_MESSAGES);
        mUnobfuscatedFields.put(FIELD_MESSAGES_COLLECTION_MESSAGES, loadMessagesCollectionMessages());
        Logger.verbose("Loading field " + FIELD_PARTICIPANT_INFO_USER_KEY);
        mUnobfuscatedFields.put(FIELD_PARTICIPANT_INFO_USER_KEY, loadParticipantInfoUserKey());

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
