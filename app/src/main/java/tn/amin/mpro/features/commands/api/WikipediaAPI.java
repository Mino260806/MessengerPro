package tn.amin.mpro.features.commands.api;

import android.net.Uri;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import de.robv.android.xposed.XposedBridge;

public class WikipediaAPI extends AbstractAPI {
    public static String fetchArticle(String term, String language) {
        try {
            setUrlFormat("https://%s.wikipedia.org/w/api.php?action=opensearch&search=%s&limit=1&format=json");
            JSONArray query = new JSONArray(fetchData(language, term));
            String articleUrl = query.getJSONArray(query.length()-1).getString(0);
            String articleTitle = Uri.parse(articleUrl).getLastPathSegment();
            setUrlFormat("https://%s.wikipedia.org/w/api.php?action=query&prop=extracts&exlimit=1&titles=%s&explaintext=1&exsectionformat=plain&format=json");
            JSONObject article = new JSONObject(fetchData(language, articleTitle));
            JSONObject pages = article
                    .getJSONObject("query")
                    .getJSONObject("pages");
            String s =  pages
                    .getJSONObject(pages.keys().next())
                    .getString("extract")
                    .split("\\n\\n\\n")[0];
            if (s.isEmpty()) throw new Throwable();
            return s;
        } catch (Throwable ignored) {
            XposedBridge.log(ignored);
        }
        return "No articles were found.";
    }
}
