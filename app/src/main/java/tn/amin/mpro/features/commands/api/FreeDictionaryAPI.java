package tn.amin.mpro.features.commands.api;

import android.app.ApplicationErrorReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import kotlin.NotImplementedError;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.R;
import tn.amin.mpro.features.formatting.MessageUnicodeConverter;

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

    public static String fetchDefinitions(String word) {
        JSONObject json;
        HashMap<String, ArrayList<String>> allDefinitions = new HashMap<>();
        try {
            json = new JSONArray(fetchData(word))
                    .getJSONObject(0);
            JSONArray meanings = json.getJSONArray("meanings");
            for (int i=0; i < meanings.length(); i++) {
                JSONObject meaning = meanings.getJSONObject(i);
                String partOfSpeech = meaning.getString("partOfSpeech");
                if (allDefinitions.get(partOfSpeech) == null) {
                    allDefinitions.put(partOfSpeech, new ArrayList<>());
                }
                ArrayList<String> definitions = allDefinitions.get(partOfSpeech);
                JSONArray definitionsJson = meaning.getJSONArray("definitions");
                for (int j=0; j < definitionsJson.length(); j++) {
                    JSONObject definitionJson = definitionsJson.getJSONObject(j);
                    String definition = definitionJson.getString("definition");
                    definitions.add(definition);
                }
            }
        } catch (Exception e) {
            XposedBridge.log(e);
            return MProMain.getMProResources().getString(R.string.command_error_word_define, word);
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, ArrayList<String>> definitionEntry: allDefinitions.entrySet()) {
            result.append(MessageUnicodeConverter.underline(word));
            result.append(". ");
            result.append(MessageUnicodeConverter.bold(definitionEntry.getKey()));
            result.append("\n");
            ArrayList<String> definitions = definitionEntry.getValue();
            for (String definition: definitions) {
                result.append("- ");
                result.append(definition);
                result.append("\n");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
