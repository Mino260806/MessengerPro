package tn.amin.mpro2.text.parser.node;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LinkNode extends ContainerNode {
    private String mLink;

    public LinkNode(List<Node> children, String link) {
        super(children);
        this.mLink = link;
    }

    public String getLink() {
        return mLink;
    }

    @NotNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Link(\"" + mLink + "\", [");
        for (Node node: getChildren()) {
            sb.append(node.toString());
            sb.append(",");
        }
        sb.delete(sb.length()-1, sb.length());
        sb.append("])");
        return sb.toString();
    }
}
