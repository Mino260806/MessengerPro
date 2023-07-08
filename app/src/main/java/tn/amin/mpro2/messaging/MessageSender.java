package tn.amin.mpro2.messaging;

import tn.amin.mpro2.orca.datatype.MediaAttachment;

public interface MessageSender {
    void sendMessage(String message);
    void sendAttachment(MediaAttachment attachment);
    void sendSticker(long stickerId);
}
