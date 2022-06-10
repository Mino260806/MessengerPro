package tn.amin.mpro.features.commands.api;

import android.content.res.Resources;

import tn.amin.mpro.MProMain;
import tn.amin.mpro.R;

public interface ApiResult {
    public void revealResult();

    class SendText implements ApiResult {
        private final String mText;

        public SendText(String text) { mText = text; }

        @Override
        public void revealResult() {
            MProMain.sendMessage(mText, true);
        }
    }

    class SendMedia implements ApiResult {
        private final Object mMediaResource;

        public SendMedia(Object mediaResource) { mMediaResource = mediaResource; }

        @Override
        public void revealResult() {
            MProMain.sendAttachment(mMediaResource);
        }
    }
}
