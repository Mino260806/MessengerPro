package tn.amin.mpro2.text.parser.string;

import java.util.HashMap;
import java.util.Map;

import tn.amin.mpro2.text.parser.node.portal.NodePortal;

public class NodeDelimitedString {
    private static final char NULL_CHAR = '\0';

    private final StringBuilder mContent;
    private final Map<Integer, NodePortal> nodePortalMap = new HashMap<>();

    public NodeDelimitedString(String content) {
        mContent = new StringBuilder(content);
    }

    public void delimitNode(NodePortal nodePortal) {
        // Loop here is unnecessary, we could just replace at beginIndex and endIndex
        mContent.setCharAt(nodePortal.getBeginIndex(), NULL_CHAR);
        mContent.setCharAt(nodePortal.getEndIndex() - 1, NULL_CHAR);
        nodePortalMap.put(nodePortal.getBeginIndex(), nodePortal);
    }

    public int lookForChar(int index) {
        if (!isChar(index)) {
            NodePortal portal = nodePortalAt(index);
            if (portal != null) {
                // If string.charAt(index) is not a char, then it's the beginning of a node portal,
                // we jump at the end of it as we cannot scan it
                index = portal.getEndIndex();
            }
        }
        return index;
    }

    public boolean isChar(int index) {
        return !isNodePortal(index);
    }

    public boolean isNodePortal(int index) {
        return mContent.charAt(index) == NULL_CHAR;
    }

    public NodePortal nodePortalAt(int i) {
        return nodePortalMap.get(i);
    }

    public char charAt(int i) {
        return mContent.charAt(i);
    }

    public String substring(int start, int end) {
        return mContent.substring(start, end);
    }

    public String getContent() {
        return mContent.toString();
    }
}
