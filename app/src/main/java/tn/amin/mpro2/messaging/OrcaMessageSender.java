package tn.amin.mpro2.messaging;

import tn.amin.mpro2.orca.connector.MailboxConnector;
import tn.amin.mpro2.orca.datatype.MediaAttachment;
import tn.amin.mpro2.orca.datatype.TextMessage;

public class OrcaMessageSender implements MessageSender {
    private final MailboxConnector mailbox;
    private final Long mThreadKey;
    private final String mReplyId;

    public OrcaMessageSender(MailboxConnector mailbox, Long threadKey) {
        this(mailbox, threadKey, null);
    }

    public OrcaMessageSender(MailboxConnector mailbox, Long threadKey, String replyMessageId) {
        this.mailbox = mailbox;
        mThreadKey = threadKey;
        mReplyId = replyMessageId;
    }

    @Override
    public void sendMessage(String message) {
        mailbox.sendText(new TextMessage.Builder(message)
                .setReplyMessageId(mReplyId)
                .build(), mThreadKey, 0);
    }

    @Override
    public void sendAttachment(MediaAttachment attachment) {
        mailbox.sendAttachment(attachment, mThreadKey, 0, mReplyId);
    }

    @Override
    public void sendSticker(long stickerId) {
        mailbox.sendSticker(stickerId, mThreadKey, 0, mReplyId);
    }
}
