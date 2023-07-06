package tn.amin.mpro2.text.parser.node.scanner;

import tn.amin.mpro2.text.parser.TextBrowser;
import tn.amin.mpro2.text.parser.node.portal.NodePortal;
import tn.amin.mpro2.text.parser.string.NodeDelimitedString;

abstract public class NodeScanner {
    public abstract int getPriority();
    public abstract NodePortal scan(NodeDelimitedString string, int beginIndex, int limitEndIndex, TextBrowser browser);
}
