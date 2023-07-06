package tn.amin.mpro2.text.parser;

import java.util.List;
import java.util.regex.Pattern;

import tn.amin.mpro2.text.parser.node.ContainerNode;
import tn.amin.mpro2.text.parser.node.Node;
import tn.amin.mpro2.text.parser.node.scanner.NodeScanner;

public class TextParser {
    private final List<NodeScanner> mNodeScanners;
    private final List<Pattern> mProtectedPatterns;

    TextParser(List<NodeScanner> nodeScanners, List<Pattern> protectedPatterns) {
        mNodeScanners = nodeScanners;
        mProtectedPatterns = protectedPatterns;
    }

    public Node parse(String text) {
        return new ContainerNode(new TextBrowser(mNodeScanners, mProtectedPatterns).browse(text));
    }

    public static class Builder {
        public List<NodeScanner> nodeScanners;
        private List<Pattern> protectedPatterns;

        public Builder setNodeScanners(List<NodeScanner> nodeScanners) {
            this.nodeScanners = nodeScanners;
            return this;
        }

        public Builder setProtectedPatterns(List<Pattern> protectedPatterns) {
            this.protectedPatterns = protectedPatterns;
            return this;
        }

        public TextParser build() {
            return new TextParser(nodeScanners, protectedPatterns);
        }
    }
}
