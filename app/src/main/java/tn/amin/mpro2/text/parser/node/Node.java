package tn.amin.mpro2.text.parser.node;

public abstract class Node {
    public abstract String toPlainText(Node parent, int index, StringTransformer transformer);

    public interface StringTransformer {
        String transform(String text, int index, Node parent);
    }
}
