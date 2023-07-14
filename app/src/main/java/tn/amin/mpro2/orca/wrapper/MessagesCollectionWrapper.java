package tn.amin.mpro2.orca.wrapper;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

import tn.amin.mpro2.hook.unobfuscation.OrcaUnobfuscator;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.WrapperHelper;

public class MessagesCollectionWrapper {
    private final WeakReference<Object> mObject;

    private final Field mMessagesListField;

    public MessagesCollectionWrapper(OrcaGateway gateway, Object messagesCollection) {
        mObject = new WeakReference<>(messagesCollection);

        mMessagesListField = gateway.unobfuscator.getField(OrcaUnobfuscator.FIELD_MESSAGES_COLLECTION_MESSAGES);
    }

    public List<?> getList() {
        return (List<?>) WrapperHelper.fieldGet(mMessagesListField, mObject.get());
    }
}
