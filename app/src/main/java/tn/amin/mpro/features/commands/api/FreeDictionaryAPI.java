package tn.amin.mpro.features.commands.api;

import android.app.ApplicationErrorReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import kotlin.NotImplementedError;

public class FreeDictionaryAPI extends AbstractAPI {
    static {
        setUrlFormat("https://api.dictionaryapi.dev/api/v2/entries/en/%s");
    }

    // TODO: return all pronounciations (not just first one)
    public static String fetchPronunciation(String word) {
        JSONArray json;
        try {
            json = new JSONArray(fetchData(word));
            for (int i=0; i<json.length(); i++) {
                try {
                    JSONArray jsonPhonetics = json
                            .getJSONObject(i)
                            .getJSONArray("phonetics");
                    for (int j=0; j<jsonPhonetics.length(); j++) {
                        try {
                            String audio = jsonPhonetics
                                    .getJSONObject(j)
                                    .getString("audio");
                            if (!audio.equals("")) {
                                return audio;
                            }
                        } catch (JSONException ignored) {
                        }
                    }
                } catch (JSONException ignored) {
                }
            }
        } catch (Exception e) {
            XposedBridge.log(e);
        }
        return "";
    }

    public static String fetchSynonyms(String word) {
        throw new NotImplementedError();
    }
}
