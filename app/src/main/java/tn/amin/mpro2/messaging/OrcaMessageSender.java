package tn.amin.mpro2.messaging;

import tn.amin.mpro2.orca.connector.MailboxConnector;
import tn.amin.mpro2.orca.datatype.MediaAttachment;
import tn.amin.mpro2.orca.datatype.TextMessage;

public class OrcaMessageSender implements MessageSender {
    private final MailboxConnector mailbox;
    private final Long mThreadKey;

    public OrcaMessageSender(MailboxConnector mailbox, Long threadKey) {
        this.mailbox = mailbox;
        mThreadKey = threadKey;
    }

    @Override
    public void sendMessage(String message) {
        mailbox.sendText(new TextMessage.Builder(message).build(), mThreadKey, 0);
    }

    @Override
    public void sendAttachment(MediaAttachment attachment) {
        mailbox.sendAttachment(attachment, mThreadKey, 0);
    }

    @Override
    public void sendSticker(long stickerId) {
        mailbox.sendSticker(stickerId, mThreadKey, 0);
    }
}
