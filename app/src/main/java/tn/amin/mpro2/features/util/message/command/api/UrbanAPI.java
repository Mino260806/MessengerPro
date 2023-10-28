package tn.amin.mpro2.features.util.message.command.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import tn.amin.mpro2.features.util.message.formatting.MessageUnicodeConverter;

public class UrbanAPI {
    public static String fetchDefinition(String word) {
        try {
            StringBuilder result = new StringBuilder().append("");
            URL url = new URL("https://www.urbandictionary.com/define.php?term=" + word);
            HttpURLConnection httpURLConnection = null;

            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("GET");
            InputStream inputStream = httpURLConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            String html = scanner.hasNext() ? scanner.next() : "";
            Document document = Jsoup.parse(html);
            Element parent = document.selectFirst(".p-5.md\\:p-8");
            if (parent != null) {
                Elements children = parent.children();
                if (children.size() >= 4) {
                    Element wordDefined = children.get(0);
                    Element meaning = children.get(1);
                    Element example = children.get(2);
                    Element contributor = children.get(3);
                    String meaningStr = extractTextWithNewlines(meaning);
                    String exampleStr = extractTextWithNewlines(example);

                    result.append(MessageUnicodeConverter.underline(wordDefined.text() + ".")).append("\n\n").append("- ").append(meaningStr).append("\n\n")
                            .append(MessageUnicodeConverter.italic("- " + exampleStr)).append("\n\n")
                            .append(MessageUnicodeConverter.bold(contributor.text()));
                }
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String extractTextWithNewlines(Element element) {
        StringBuilder sb = new StringBuilder();
        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                sb.append(((TextNode) node).getWholeText());
            } else if (node instanceof Element childElement) {
                switch (childElement.tagName()) {
                    case "br" -> sb.append("\n");
                    case "a" -> sb.append(childElement.text());
                    default -> {
                    }
                }
            }
        }
        return sb.toString();
    }


}
