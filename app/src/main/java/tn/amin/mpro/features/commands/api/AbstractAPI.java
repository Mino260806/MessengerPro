package tn.amin.mpro.features.commands.api;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import tn.amin.mpro.builders.dialog.LoadingDialogBuilder;

public abstract class AbstractAPI {
    private static String mUrlFormat = null;
    private static OnHttpsURLConnectionCreated mOnHttpsURLConnectionCreated = (u) -> {};

    protected static HttpResponse fetchData(Object... params) throws IOException, JSONException {
        return fetchDataFromUrl(getUrl(params));
    }

    private static HttpResponse fetchDataFromUrl(String url) throws IOException, JSONException {
        URLConnection urlConnection = new URL(url).openConnection();
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;
        mOnHttpsURLConnectionCreated.onCreated(httpsURLConnection);
        int responseCode = httpsURLConnection.getResponseCode();
        InputStream is = responseCode == 200 ? httpsURLConnection.getInputStream() : httpsURLConnection.getErrorStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String responseString = readAll(rd);
            HttpResponse response = new HttpResponse();
            response.responseCode = responseCode;
            response.responseString = responseString;
            return response;
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
    protected static void setOnHttpsURLConnectionCreated(OnHttpsURLConnectionCreated onHttpsURLConnectionCreated) {
        mOnHttpsURLConnectionCreated = onHttpsURLConnectionCreated;
    }

    public interface OnHttpsURLConnectionCreated {
        void onCreated(HttpsURLConnection httpsURLConnection) throws JSONException;
    }

    public static class HttpResponse {
        int responseCode = 200;
        String responseString = null;
    }
}
