package tn.amin.mpro2.orca.connector;

import androidx.core.util.Consumer;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.constants.OrcaClassNames;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.hook.all.MessageSentHook;
import tn.amin.mpro2.orca.builder.AttachmentBuilder;
import tn.amin.mpro2.orca.datatype.GenericMessage;
import tn.amin.mpro2.orca.datatype.MediaAttachment;
import tn.amin.mpro2.orca.datatype.MediaMessage;
import tn.amin.mpro2.orca.datatype.Mention;
import tn.amin.mpro2.orca.datatype.TextMessage;
import tn.amin.mpro2.orca.wrapper.AuthDataWrapper;
import tn.amin.mpro2.util.XposedHilfer;

public class MailboxConnector {
    public final WeakReference<Object> mailbox;
    private final AuthDataWrapper authData;
    private final ClassLoader classLoader;

    public MailboxConnector(Object mailbox, AuthDataWrapper authData, ClassLoader classLoader) {
        this.mailbox = new WeakReference<>(mailbox);
        this.authData = authData;
        this.classLoader = classLoader;
    }

    public void sendMessage(GenericMessage messageToSend, final long threadKey, final int delay) {
        switch (messageToSend.getType()) {
            case GenericMessage.TYPE_TEXT:
                sendText((TextMessage) messageToSend, threadKey, delay);
                break;

            case GenericMessage.TYPE_MEDIA:
                sendMedia((MediaMessage) messageToSend, threadKey, delay);
                break;
        }
    }

    public void sendText(final TextMessage textMessage, final long threadKey, final int delay) {
        final Class<?> MailboxCoreJNI = XposedHelpers.findClass(OrcaClassNames.MAILBOX_CORE_JNI, classLoader);
        final Set<Method> disptachList = XposedHilfer.findAllMethods(MailboxCoreJNI, MessageSentHook.DISPATCH_METHOD);
        if (disptachList.size() != 1)
            Logger.error(new RuntimeException("dispatchList size (" + disptachList.size() + ") != 1"));
        final Method disptach = disptachList.iterator().next();

        preDispatch(notificationScope -> {
            long time = System.currentTimeMillis() * 1000;
            Object[] disptachParams = new Object[] {
                    8, 65540, threadKey, mailbox.get(), String.valueOf(authData.getFacebookUserKey()), textMessage.content, null, null, null, null, null, null, null, 0, null, null, null, time, null, null, null, null, null, null, notificationScope
            };

            disptachParams[7] = Mention.joinRangeStarts(textMessage.mentions);
            disptachParams[8] = Mention.joinRangeEnds(textMessage.mentions);
            disptachParams[9] = Mention.joinThreadKeys(textMessage.mentions);
            disptachParams[10] = Mention.joinTypes(textMessage.mentions);
            disptachParams[11] = textMessage.replyMessageId;
            if (textMessage.replyMessageId != null)
                disptachParams[12] = 1;
            try {
                XposedBridge.invokeOriginalMethod(disptach, null, disptachParams);
            } catch (Throwable t) {
                Logger.error(t);
            }
        }, delay);
    }

    public void reactToMessage(final String reaction, final String messageId, final long threadKey, final int delay) {
        final Class<?> MailboxCoreJNI = XposedHelpers.findClass(OrcaClassNames.MAILBOX_SDK_JNI, classLoader);
        final Set<Method> disptachList = XposedHilfer.findAllMethods(MailboxCoreJNI, "dispatchVJOOOOOOOO");
        if (disptachList.size() != 1)
            Logger.error(new RuntimeException("dispatchList size (" + disptachList.size() + ") != 1"));
        final Method disptach = disptachList.iterator().next();

        preDispatch(notificationScope -> {
            long time = System.currentTimeMillis();
            Object[] disptachParams = new Object[] {
                    46, threadKey, mailbox.get(), reaction, messageId, time, null, null, null, notificationScope
            };

            try {
                XposedBridge.invokeOriginalMethod(disptach, null, disptachParams);
            } catch (Throwable t) {
                Logger.error(t);
            }
        }, delay);
    }
    public void sendSticker(final long stickerId, final long threadKey, final int delay) {
        sendSticker(stickerId, threadKey, delay, null);
    }

    public void sendSticker(final long stickerId, final long threadKey, final int delay, final String replyId) {
        Logger.info("Sending sticker " + stickerId + "!");

        final Class<?> MailboxCoreJNI = XposedHelpers.findClass(OrcaClassNames.MAILBOX_CORE_JNI, classLoader);
        final Set<Method> disptachList = XposedHilfer.findAllMethods(MailboxCoreJNI, "dispatchVIIIJJOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
        if (disptachList.size() != 1)
            Logger.error(new RuntimeException("dispatchList size (" + disptachList.size() + ") != 1"));
        final Method disptach = disptachList.iterator().next();

        preDispatch(notificationScope -> {
            long time = System.currentTimeMillis() * 1000;
            try {
                XposedBridge.invokeOriginalMethod(disptach, null, new Object[] {
                        11, 0, 0, 65540, threadKey, stickerId, mailbox.get(), null, null, null, null, null, null, "", null, null, "", null, null, null, null, "You sent a sticker.", null, null, null, null, null, null, replyId, replyId != null? 1: 0, null, null, time, null, null, null, null, null, notificationScope
                });
            } catch (Throwable t) {
                Logger.error(t);
            }
        }, delay);
    }

    public void sendMedia(MediaMessage message, final long threadKey, final int delay) {
        // TODO use proper method for multiple files

        for (MediaAttachment attachment: message.mediaAttachments) {
            sendAttachment(attachment, threadKey, delay, message.replyMessageId);
        }
    }


    public void sendAttachment(MediaAttachment attachment, final long threadKey, final int delay) {
        sendAttachment(attachment, threadKey, delay, null);
    }

    public void sendAttachment(MediaAttachment attachment, final long threadKey, final int delay, final String replyId) {
        final Class<?> MailboxSDKJNI = XposedHelpers.findClass(OrcaClassNames.MAILBOX_SDK_JNI, classLoader);
        final Set<Method> disptachList = XposedHilfer.findAllMethods(MailboxSDKJNI, "dispatchVIJOOOOOOOOOOOOZ");
        if (disptachList.size() != 1)
            Logger.error(new RuntimeException("dispatchList size (" + disptachList.size() + ") != 1"));
        final Method disptach = disptachList.iterator().next();

        if (!attachment.path.canRead()) {
            Logger.warn("Messenger does not have permission to read \"" + attachment.path.getAbsolutePath() + "\"");
            return;
        }

        Object orcaAttachment = new AttachmentBuilder(classLoader)
                .setType(attachment.type)
                .setFile(attachment.path)
                .setFileName(attachment.fileName)
                .build();

        preDispatch(notificationScope -> {
            long time = System.currentTimeMillis() * 1000;
            try {
                XposedBridge.invokeOriginalMethod(disptach, null, new Object[] {
//                        53, threadKey, mailbox.get(), orcaAttachment, "", "You sent a file.", null, null, time, null, notificationScope
                        58, 65540, threadKey, mailbox.get(), orcaAttachment, "You sent a file.", replyId, replyId != null? 1: 0, null, null, null, time, null, null,  notificationScope, true
                });
            } catch (Throwable t) {
                Logger.error(t);
            }
        }, delay);

    }

    private void executeAsync(Runnable runnable) {
        final Class<?> Execution = XposedHelpers.findClass(OrcaClassNames.MCI_EXECUTION, classLoader);
        final Method nativeScheduleTask = XposedHelpers.findMethodExact(Execution, "nativeScheduleTask", Runnable.class, int.class, int.class, double.class, String.class);

        try {
            nativeScheduleTask.invoke(null, runnable, 1, 0, 0 / 1000.0d, "MPro2Thread");
        } catch (Throwable t) {
            Logger.error(t);
        }
    }

    private void preDispatch(Consumer<Object> dispatchExecutor, final int delay) {
        final Class<?> NotificationScope = XposedHelpers.findClass(OrcaClassNames.NOTIFICATION_SCOPE, classLoader);

        new Thread(() -> {
            try {
                Logger.info("Sending message in " + delay + " milliseconds...");
                Thread.sleep(delay);
                executeAsync(() -> {
                    try {
                        Logger.info("Inside async");

                        final Object notificationScope = XposedHelpers.findConstructorExact(NotificationScope).newInstance();
                        dispatchExecutor.accept(notificationScope);
                    } catch (Throwable t) {
                        Logger.error(t);
                    }
                });
            } catch (Throwable t) {
                Logger.error(t);
            }
        }).start();
    }
}
