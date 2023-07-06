package tn.amin.mpro2.orca.datatype;

abstract public class GenericMessage {
    public String replyMessageId;

    public final static int TYPE_TEXT = 1;
    public final static int TYPE_MEDIA = 2;

    public abstract int getType();
}
