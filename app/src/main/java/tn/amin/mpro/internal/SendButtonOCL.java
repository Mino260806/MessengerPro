package tn.amin.mpro.internal;


import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro.MProMain;

public class SendButtonOCL implements View.OnClickListener {

    final private View.OnClickListener mDefaultOCL;

    final private EditText mAssociatedEditText;
    public SendButtonOCL(View sendButton, EditText associatedEditText) {
        Object listenerInfo = XposedHelpers.callMethod(sendButton, "getListenerInfo");
        mDefaultOCL = (View.OnClickListener)XposedHelpers.getObjectField(listenerInfo, "mOnClickListener");
        mAssociatedEditText = associatedEditText;
    }
    @Override
    public void onClick(View view) {
        if (!MProMain.getPrefReader().isMProEnabled()) {
            mDefaultOCL.onClick(view);
            return;
        }

        final Editable messageEditable = mAssociatedEditText.getText();
        final String messageString = mAssociatedEditText.getText().toString();
        final boolean mustSendMessage = MProMain.getConversationMapper().beforeMessageSent(messageEditable);
        if (mustSendMessage) {
            mDefaultOCL.onClick(view);
        }
        MProMain.getConversationMapper().afterMessageSent(messageString, !mustSendMessage);
    }
}

