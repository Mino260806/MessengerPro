package tn.amin.mpro2.text.parser.node.portal;

import tn.amin.mpro2.text.parser.node.LinkNode;

public class LinkNodePortal extends SimpleNodePortal {
    private final String mLink;

    public LinkNodePortal(int beginIndex, int endIndex, int contentBeginIndex, int contentEndIndex, String link) {
        super(beginIndex, endIndex, contentBeginIndex, contentEndIndex, children -> new LinkNode(children, link));

        mLink = link;
    }
}

