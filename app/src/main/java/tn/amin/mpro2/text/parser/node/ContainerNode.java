package tn.amin.mpro2.text.parser.node;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import tn.amin.mpro2.debug.Logger;

public class ContainerNode extends Node {
    private final List<Node> mChildren;

    public ContainerNode(List<Node> children) {
        mChildren = children;
    }

    public List<Node> getChildren() {
        return mChildren;
    }

    @Override
    public String toPlainText(Node parent, final int index, StringTransformer transformer) {
        StringBuilder sb = new StringBuilder();
        int cumulativeIndex = index;
        for (Node child: getChildren()) {
            String plainText = child.toPlainText(this, cumulativeIndex, transformer);
            String transformedPlainText = transformer.transform(plainText, index, this);
            sb.append(transformedPlainText);

            cumulativeIndex += transformedPlainText.length();

            Logger.info("\"" + transformedPlainText + "\", " +
                    Arrays.toString(transformedPlainText.toCharArray()) + ", (length = " + transformedPlainText.length() + ", cumulativeIndex = " + cumulativeIndex + ")");
        }

        return sb.toString();
    }

    @NotNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Container([");
        for (Node node: getChildren()) {
            sb.append(node.toString());
            sb.append(",");
        }
        sb.delete(sb.length()-1, sb.length());
        sb.append("])");
        return sb.toString();
    }
}
