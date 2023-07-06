package tn.amin.mpro2.text.parser.node;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DelimNode extends ContainerNode {
    private final String mOpeningDelimiter;
    private final String mClosingDelimiter;

    public DelimNode(List<Node> children, String openingDelimiter, String closingDelimiter) {
        super(children);
        mOpeningDelimiter = openingDelimiter;
        mClosingDelimiter = closingDelimiter;
    }

    public String getOpeningDelimiter() {
        return mOpeningDelimiter;
    }

    public String getClosingDelimiter() {
        return mClosingDelimiter;
    }

    @NotNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Delim<" + getOpeningDelimiter() + "," + getClosingDelimiter() + ">([");
        for (Node node: getChildren()) {
            sb.append(node.toString());
            sb.append(",");
        }
        sb.delete(sb.length()-1, sb.length());
        sb.append("])");
        return sb.toString();
    }
}

