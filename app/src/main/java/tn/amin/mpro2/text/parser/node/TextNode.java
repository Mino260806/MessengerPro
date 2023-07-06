package tn.amin.mpro2.text.parser.node;

import org.jetbrains.annotations.NotNull;

public class TextNode extends Node {
    private String mText;

    public TextNode(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public void mergeWith(TextNode textNode) {
        mText += textNode.getText();
    }

    @Override
    public String toPlainText(Node parent, int index, StringTransformer transformer) {
        return getText();
    }

    @NotNull
    @Override
    public String toString() {
        return "\"" + mText + "\"";
    }
}
