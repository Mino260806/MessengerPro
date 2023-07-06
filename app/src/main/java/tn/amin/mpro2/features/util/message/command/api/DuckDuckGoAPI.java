package tn.amin.mpro2.features.util.message.command.api;

import org.json.JSONObject;

import tn.amin.mpro2.debug.Logger;

public class DuckDuckGoAPI extends AbstractAPI {
    public static String fetchSearchResult(String term, String language) {
        try {
            setUrlFormat("https://api.duckduckgo.com/?callback=&format=json&no_html=1&no_redirect=1&q=%s&skip_disambig=1&l=%s");
            JSONObject query = new JSONObject(fetchData(term, language).responseString);

            String result = query.getString("Abstract");
            if (result.isEmpty())
                return "No results.";

            return result;
        } catch (Throwable t) {
            Logger.error(t);
        }
        return "No results.";
    }
}
