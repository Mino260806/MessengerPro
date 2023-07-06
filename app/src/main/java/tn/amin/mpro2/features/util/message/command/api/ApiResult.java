package tn.amin.mpro2.features.util.message.command.api;

import tn.amin.mpro2.messaging.MessageSender;
import tn.amin.mpro2.orca.datatype.MediaAttachment;

public interface ApiResult {
    void revealResult(MessageSender messageSender);

    class SendText implements ApiResult {
        private final String mText;

        public SendText(CharSequence text) { mText = text.toString(); }

        @Override
        public void revealResult(MessageSender messageSender) {
            messageSender.sendMessage(mText);
        }
    }

    class SendMedia implements ApiResult {
        private final MediaAttachment mMediaAttachment;

        public SendMedia(MediaAttachment attachment) {
            mMediaAttachment = attachment;
        }

        @Override
        public void revealResult(MessageSender messageSender) {
            messageSender.sendAttachment(mMediaAttachment);
        }
    }
}
