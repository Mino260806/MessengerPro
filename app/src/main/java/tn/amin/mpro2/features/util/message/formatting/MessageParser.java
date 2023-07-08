package tn.amin.mpro2.features.util.message.formatting;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.orca.datatype.GenericMessage;
import tn.amin.mpro2.orca.datatype.MediaAttachment;
import tn.amin.mpro2.orca.datatype.MediaMessage;
import tn.amin.mpro2.orca.datatype.Mention;
import tn.amin.mpro2.orca.datatype.TextMessage;
import tn.amin.mpro2.text.parser.TextParser;
import tn.amin.mpro2.text.parser.node.DelimNode;
import tn.amin.mpro2.text.parser.node.LinkNode;
import tn.amin.mpro2.text.parser.node.Node;
import tn.amin.mpro2.text.parser.node.scanner.LinkNodeScanner;
import tn.amin.mpro2.text.parser.node.scanner.SimpleNodeScanner;
import tn.amin.mpro2.util.IntRange;

public class MessageParser {
    private final TextParser mParser;

    public MessageParser() {
        mParser = new TextParser.Builder()
                .setNodeScanners(Arrays.asList(
                        new SimpleNodeScanner("*", "*", 5),
                        new SimpleNodeScanner("-", "-", 5),
                        new SimpleNodeScanner("!", "!", 5),
                        new SimpleNodeScanner("_", "_", 5),
                        new SimpleNodeScanner("#", "#", 5),
                        new SimpleNodeScanner("{", "}", 4),
                        new LinkNodeScanner()
                ))
                .setProtectedPatterns(Arrays.asList(
                        Pattern.compile("(https?:\\/\\/(www\\.)?)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
                ))
                .build();;
    }

    public List<GenericMessage> parse(String message, long conversationThreadKey, boolean mediaAllowed) {
        Node node = mParser.parse(message);
        Logger.info("Node: " + node);

        ArrayList<GenericMessage> messages = new ArrayList<>();

        ArrayList<Mention> mentions = new ArrayList<>();
        String content = node.toPlainText(null, 0, (text, index, parent) -> {
            String plainText;
            if (parent instanceof DelimNode) {
                String delim = ((DelimNode) parent).getOpeningDelimiter();
                if (delim.equals("{")) {
                    plainText = "";
                    if (mediaAllowed) {
                        messages.add(new MediaMessage(Collections.singletonList(
                                new MediaAttachment(new File(text), FilenameUtils.getBaseName(text)))));
                    }
                } else {
                    plainText = MessageUnicodeConverter.convert(delim + text + delim);
                }
            } else if (parent instanceof LinkNode) {
                Logger.info("text is " + text + ", index is " + index);

                plainText = text;
                String link = ((LinkNode) parent).getLink();
                if ("everyone".equals(link)) {
                    mentions.add(new Mention(new IntRange(index, index + text.length()), conversationThreadKey, Mention.Type.GROUP));
                }
                else if (NumberUtils.isDigits(link)) {
                    long threadKey = NumberUtils.toLong(link, -1);
                    if (threadKey >= 0) {
                        mentions.add(new Mention(new IntRange(index, index + text.length()), threadKey, Mention.Type.PERSON));
                    }
                }
            } else {
                plainText = text;
            }
            return plainText;
        });

        if (!content.trim().isEmpty()) {
            messages.add(new TextMessage.Builder(content)
                    .setMentions(mentions)
                    .build());
        }
        return messages;
    }
}
