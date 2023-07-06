package tn.amin.mpro2.orca.datatype;

import java.util.List;

public class MediaMessage extends GenericMessage {
    public List<MediaAttachment> mediaAttachments;

    public MediaMessage(List<MediaAttachment> mediaAttachments) {
        this.mediaAttachments = mediaAttachments;
    }

    @Override
    public int getType() {
        return GenericMessage.TYPE_MEDIA;
    }
}
