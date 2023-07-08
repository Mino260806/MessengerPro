package tn.amin.mpro2.orca.datatype;

import java.io.File;

import tn.amin.mpro2.orca.builder.AttachmentBuilder;

public class MediaAttachment {
    public File path;
    public String fileName;

    public long type;

    public MediaAttachment(File path) {
        this.path = path;
        this.fileName = path.getName();
        this.type = AttachmentBuilder.FILETYPE_UNKNOWN;
    }

    public MediaAttachment(File path, String fileName) {
        this.path = path;
        this.fileName = fileName;
        this.type = AttachmentBuilder.FILETYPE_UNKNOWN;
    }

    public MediaAttachment(File path, String fileName, long type) {
        this.path = path;
        this.fileName = fileName;
        this.type = type;
    }
}
