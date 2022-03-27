package tn.amin.mpro.features.commands.api;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class AbstractAPI {
    private static String mUrlFormat = null;

    protected static String fetchData(Object... params) throws IOException, JSONException {
        return fetchDataFromUrl(getUrl(params));
    }

    private static String fetchDataFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return jsonText;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static String getUrl(Object... args) {
        String urlFormat = getUrlFormat();
        if (urlFormat == null) {
            throw new NullPointerException("Url format should not be null");
        }
        return String.format(urlFormat, args);
    }

    protected static String getUrlFormat() { return mUrlFormat; }
    protected static void setUrlFormat(String urlFormat) { mUrlFormat = urlFormat; }
}
