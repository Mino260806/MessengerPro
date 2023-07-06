package tn.amin.mpro2.text.parser.node.portal;

import java.util.List;

import tn.amin.mpro2.text.parser.node.Node;

abstract public class NodePortal {
    public abstract int getBeginIndex();

    public abstract int getEndIndex();

    public abstract int getContentBeginIndex();

    public abstract int getContentEndIndex();

    public abstract Node getNode(List<Node> children);

    public interface NodeProvider {
        Node get(List<Node> children);
    }

    public boolean canHaveChildren() {
        return getContentBeginIndex() >= 0 && getContentEndIndex() >= 0;
    }
}
