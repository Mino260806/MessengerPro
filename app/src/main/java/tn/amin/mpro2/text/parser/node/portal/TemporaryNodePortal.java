package tn.amin.mpro2.text.parser.node.portal;

import java.util.List;

import tn.amin.mpro2.text.parser.node.Node;
import tn.amin.mpro2.text.parser.node.TextNode;

public class TemporaryNodePortal extends NodePortal {
    private final int mBeginIndex;
    private final int mEndIndex;
    private final String mText;

    public TemporaryNodePortal(int beginIndex, int endIndex, String text) {
        mBeginIndex = beginIndex;
        mEndIndex = endIndex;
        mText = text;
    }

    @Override
    public int getBeginIndex() {
        return mBeginIndex;
    }

    @Override
    public int getEndIndex() {
        return mEndIndex;
    }

    @Override
    public int getContentBeginIndex() {
        return -1;
    }

    @Override
    public int getContentEndIndex() {
        return -1;
    }

    @Override
    public Node getNode(List<Node> children) {
        return new TextNode(mText);
    }
}
