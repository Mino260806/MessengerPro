package tn.amin.mpro2.text.parser.node.portal;

import java.util.List;

import tn.amin.mpro2.text.parser.node.Node;

public class SimpleNodePortal extends NodePortal {
    private final int mBeginIndex;
    private final int mEndIndex;
    private final int mContentBeginIndex;
    private final int mContentEndIndex;
    private final NodeProvider mNodeProvider;

    public SimpleNodePortal(int beginIndex, int endIndex, int contentBeginIndex, int contentEndIndex, NodeProvider nodeProvider) {
        mBeginIndex = beginIndex;
        mEndIndex = endIndex;
        mNodeProvider = nodeProvider;
        mContentBeginIndex = contentBeginIndex;
        mContentEndIndex = contentEndIndex;
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
        return mContentBeginIndex;
    }

    @Override
    public int getContentEndIndex() {
        return mContentEndIndex;
    }

    @Override
    public Node getNode(List<Node> children) {
        return mNodeProvider.get(children);
    }
}
