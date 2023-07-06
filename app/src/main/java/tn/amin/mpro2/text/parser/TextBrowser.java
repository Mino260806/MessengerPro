package tn.amin.mpro2.text.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tn.amin.mpro2.text.parser.node.Node;
import tn.amin.mpro2.text.parser.node.TextNode;
import tn.amin.mpro2.text.parser.node.portal.NodePortal;
import tn.amin.mpro2.text.parser.node.portal.TemporaryNodePortal;
import tn.amin.mpro2.text.parser.node.scanner.NodeScanner;
import tn.amin.mpro2.text.parser.string.NodeDelimitedString;

public class TextBrowser {
    private final List<NodeScanner> mNodeScannerList;
    private final List<Pattern> mProtectedPatterns;

    public TextBrowser(List<NodeScanner> nodeScannerList, List<Pattern> protectedPatterns) {
        this.mNodeScannerList = nodeScannerList;
        this.mProtectedPatterns = protectedPatterns;
    }

    public List<Node> browse(String text) {
        return browse(text, 0, text.length());
    }

    public List<Node> browse(String text, int beginIndex, int endIndex) {
        NodeDelimitedString delimitedText = new NodeDelimitedString(text);
        protectPatterns(text, delimitedText);
        delimitNodes(delimitedText, beginIndex, endIndex);

        List<Node> nodes = extractNodes(text, delimitedText);

        return nodes;
    }

    private void protectPatterns(String text, NodeDelimitedString delimitedText) {
        for (Pattern pattern: mProtectedPatterns) {
            Matcher matcher = pattern.matcher(delimitedText.getContent());
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();

                delimitedText.delimitNode(new TemporaryNodePortal(start, end, text.substring(start, end)));
            }
        }
    }

    public void delimitNodes(NodeDelimitedString string, int beginIndex, int endIndex) {
        int priority = 1;
        do {
            priority = delimitNodesWithPriority(string, beginIndex, endIndex, priority);
        } while (priority != Integer.MAX_VALUE);
    }

    private int delimitNodesWithPriority(NodeDelimitedString string, int beginIndex, int endIndex, int priority) {
        int nextPriority = Integer.MAX_VALUE;
        for (int index=beginIndex; index<endIndex; index++) {
            index = string.lookForChar(index);
            if (index >= endIndex) break;

            for (NodeScanner nodeScanner : mNodeScannerList) {
                if (nodeScanner.getPriority() == priority) {
                    NodePortal nodePortal = nodeScanner.scan(string, index, endIndex, this);
                    if (nodePortal != null) {
                        string.delimitNode(nodePortal);
                        index = nodePortal.getEndIndex() - 1;
                    }
                } else if (nodeScanner.getPriority() > priority) {
                    nextPriority = Math.min(nextPriority, nodeScanner.getPriority());
                }
            }
//            Character c = string.charAt(i);
//                    String openingDelimiter = nodePortal.getOpeningDelimiter();
//                    int delimiterLength = openingDelimiter.length();
//                    if (i <= text.length() - delimiterLength) {
//                        if (text.substring(i, i + delimiterLength).equals(openingDelimiter)) {
//                            NodeParser.Result parseResult =
//                                    nodePortal.getParser().parse(text, i + delimiterLength, endIndex, this);
//                            if (parseResult != null) {
////                                if (i > textCursor)
////                                    browsedNodes.add(new TextNode(text.substring(textCursor, i)));
//                                parseResults.add(parseResult);
//                                processedRanges.add(new IntRange(i, parseResult.endIndex));
//
//                                i = parseResult.endIndex + 1;
//                                textCursor = i;
//                            }
//                        }
//                    }
//                } else if (nodePortal.getPriority() > priority) {
//                    nextPriority = Math.min(nextPriority, nodePortal.getPriority());
//                }
        }

//        if (textCursor < endIndex) {
//            browsedNodes.add(new TextNode(text.substring(textCursor, endIndex)));
//        }

        return nextPriority;
    }

    private List<Node> extractNodes(String text, NodeDelimitedString delimitedText) {
        return extractNodes(text, delimitedText, 0, text.length());
    }

    private List<Node> extractNodes(String text, NodeDelimitedString delimitedText, int beginIndex, int endIndex) {
        ArrayList<Node> nodes = new ArrayList<>();
        int cursor = beginIndex;
        for (int index=beginIndex; index<endIndex; index++) {
            if (delimitedText.isNodePortal(index)) {
                NodePortal portal = delimitedText.nodePortalAt(index);
                if (cursor < index) {
                    nodes.add(new TextNode(text.substring(cursor, index)));
                }
                if (portal != null) {
                    Node node;
                    if (portal.canHaveChildren()) {
                        List<Node> children = extractNodes(text, delimitedText, portal.getContentBeginIndex(), portal.getContentEndIndex());
                        node = portal.getNode(children);
                    } else {
                        node = portal.getNode(null);
                    }

                    if (node instanceof TextNode && nodes.size() > 0 &&
                        nodes.get(nodes.size()-1) instanceof TextNode) {

                        ((TextNode) nodes.get(nodes.size()-1)).mergeWith((TextNode) node);
                    } else {
                        nodes.add(node);
                    }

                    cursor = portal.getEndIndex();

                    // Subtract 1 because it will be added back in the for loop
                    index = portal.getEndIndex() - 1;
                }
                else {
                    cursor = index;
                }
            }
        }

        if (cursor < endIndex) {
            nodes.add(new TextNode(text.substring(cursor, endIndex)));
        }

        return nodes;
    }
}