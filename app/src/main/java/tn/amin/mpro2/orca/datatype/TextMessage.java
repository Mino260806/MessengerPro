package tn.amin.mpro2.orca.datatype;

import java.util.Collections;
import java.util.List;

public class TextMessage extends GenericMessage {
    public String content;
    public List<Mention> mentions;

    public TextMessage(String content, List<Mention> mentions, String replyMessageId) {
        this.content = content;
        this.mentions = mentions;
        this.replyMessageId = replyMessageId;
    }

    @Override
    public int getType() {
        return GenericMessage.TYPE_TEXT;
    }

    public static class Builder {
        private String mContent;
        private List<Mention> mMentions = Collections.emptyList();
        private String mReplyMessageId = null;

        public Builder(String content) {
            mContent = content;
        }

        public Builder setMentions(List<Mention> mentions) {
            mMentions = mentions;
            return this;
        }

        public Builder setReplyMessageId(String replyMessageId) {
            mReplyMessageId = replyMessageId;
            return this;
        }

        public TextMessage build() {
            return new TextMessage(mContent, mMentions, mReplyMessageId);
        }
    }
}
