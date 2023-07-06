package tn.amin.mpro2.text.parser.node.scanner;

import tn.amin.mpro2.text.parser.TextBrowser;
import tn.amin.mpro2.text.parser.node.portal.LinkNodePortal;
import tn.amin.mpro2.text.parser.node.portal.NodePortal;
import tn.amin.mpro2.text.parser.string.NodeDelimitedString;

public class LinkNodeScanner extends NodeScanner {
    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public NodePortal scan(NodeDelimitedString string, int beginIndex, int limitEndIndex, TextBrowser browser) {
        String openingDelimiter = "[";
        int openingDelimiterLength = openingDelimiter.length();

        if (beginIndex + openingDelimiterLength >= limitEndIndex ||
                !string.substring(beginIndex, beginIndex + openingDelimiterLength).equals(openingDelimiter)) {

            return null;
        }

        final String descriptionClosingDelimiter = "](";
        final int descriptionClosingDelimiterLength = descriptionClosingDelimiter.length();
        final String linkClosingDelimiter = ")";
        final int linkClosingDelimiterLength = linkClosingDelimiter.length();

        int descriptionEnd = -1;
        int linkBegin = -1;
        String link;
        for (int index = beginIndex + openingDelimiterLength; index < limitEndIndex; index++) {
            index = string.lookForChar(index);
            if (index >= limitEndIndex) break;

            if (descriptionEnd == -1) {
                if (index + descriptionClosingDelimiterLength - 1 < limitEndIndex &&
                        string.substring(index, index + descriptionClosingDelimiterLength).equals(descriptionClosingDelimiter)) {
                    descriptionEnd = index;
                    linkBegin = index + descriptionClosingDelimiterLength;
                }
            } else {
                if (index + linkClosingDelimiterLength - 1 < limitEndIndex &&
                        string.substring(index, index + linkClosingDelimiterLength).equals(linkClosingDelimiter)) {
                    link = string.substring(linkBegin, index);

                    browser.delimitNodes(string, beginIndex + openingDelimiterLength, descriptionEnd);
                    return new LinkNodePortal(beginIndex, index + linkClosingDelimiterLength,
                            beginIndex + openingDelimiterLength, descriptionEnd, link);
//                    return new NodeParser.Result(new LinkNode(description, content),
//                            beginIndex, index + contentClosingDelimiterLength - 1);
                }
            }
        }

        return null;
    }
}
