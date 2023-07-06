package tn.amin.mpro2.text.parser.node.scanner;

import tn.amin.mpro2.text.parser.TextBrowser;
import tn.amin.mpro2.text.parser.node.DelimNode;
import tn.amin.mpro2.text.parser.node.portal.NodePortal;
import tn.amin.mpro2.text.parser.node.portal.SimpleNodePortal;
import tn.amin.mpro2.text.parser.string.NodeDelimitedString;

public class SimpleNodeScanner extends NodeScanner {
    private final String mOpeningDelimiter;
    private final String mClosingDelimiter;
    private final int mPriority;

    public SimpleNodeScanner(String openingDelimiter, String closingDelimiter, int priority) {
        mOpeningDelimiter = openingDelimiter;
        mClosingDelimiter = closingDelimiter;
        mPriority = priority;
    }

    @Override
    public int getPriority() {
        return mPriority;
    }

    @Override
    public NodePortal scan(NodeDelimitedString string, int beginIndex, int limitEndIndex, TextBrowser browser) {
        int openingDelimiterLength = mOpeningDelimiter.length();
        int closingDelimiterLength = mClosingDelimiter.length();

        if (beginIndex + openingDelimiterLength >= limitEndIndex ||
            !string.substring(beginIndex, beginIndex + openingDelimiterLength).equals(mOpeningDelimiter)) {

            return null;
        }

        for (int index=beginIndex + openingDelimiterLength; index<limitEndIndex-closingDelimiterLength+1; index++) {
            index = string.lookForChar(index);
            if (index >= limitEndIndex-closingDelimiterLength+1) break;

            if (string.substring(index, index + closingDelimiterLength).equals(mClosingDelimiter)) {
                browser.delimitNodes(string, beginIndex + openingDelimiterLength, index);
                return new SimpleNodePortal(beginIndex, index + closingDelimiterLength,
                        beginIndex + openingDelimiterLength, index,
                        children -> new DelimNode(children, mOpeningDelimiter, mClosingDelimiter));
//                return new NodeParser.Result(
//                        new DelimNode(browser.browse(text, beginIndex, index), mOpeningDelimiter, mClosingDelimiter),
//                        beginIndex, index+closingDelimiterLength-1);
            }
        }

        return null;
    }
}
