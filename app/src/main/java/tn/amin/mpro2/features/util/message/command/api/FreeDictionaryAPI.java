package tn.amin.mpro2.features.util.message.command.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;
import tn.amin.mpro2.features.util.message.formatting.MessageUnicodeConverter;

public class FreeDictionaryAPI extends AbstractAPI {
    static {
        setUrlFormat("https://api.dictionaryapi.dev/api/v2/entries/en/%s");
    }

    // TODO: return all pronounciations (not just first one)
    public static String fetchPronunciation(String word) {
        try {
            Object jsonRaw = new JSONTokener(fetchData(word).responseString).nextValue();
            if (jsonRaw instanceof JSONArray json) {
                for (int i = 0; i < json.length(); i++) {
                    try {
                        JSONArray jsonPhonetics = json
                                .getJSONObject(i)
                                .getJSONArray("phonetics");
                        for (int j = 0; j < jsonPhonetics.length(); j++) {
                            try {
                                String audio = jsonPhonetics
                                        .getJSONObject(j)
                                        .getString("audio");
                                if (!audio.isEmpty()) {
                                    return audio;
                                }
                            } catch (JSONException ignored) {
                            }
                        }
                    } catch (JSONException ignored) {
                    }
                }
            } else {
                JSONObject json = (JSONObject) jsonRaw;
                return json
                        .getString("message");
            }
        } catch (Exception e) {
            XposedBridge.log(e);
        }
        return "";
    }

    public static String fetchSynonyms(String word) {
        throw new UnsupportedOperationException();
    }

    public static String fetchDefinitions(String word) {

        HashMap<String, ArrayList<String>> allDefinitions = new HashMap<>();
        try {
            Object jsonRaw = new JSONTokener(fetchData(word).responseString).nextValue();
            if (jsonRaw instanceof JSONArray) {
                JSONObject json = ((JSONArray) jsonRaw)
                        .getJSONObject(0);
                JSONArray meanings = json.getJSONArray("meanings");
                for (int i = 0; i < meanings.length(); i++) {
                    JSONObject meaning = meanings.getJSONObject(i);
                    String partOfSpeech = meaning.getString("partOfSpeech");
                    allDefinitions.computeIfAbsent(partOfSpeech, k -> new ArrayList<>());
                    ArrayList<String> definitions = allDefinitions.get(partOfSpeech);
                    JSONArray definitionsJson = meaning.getJSONArray("definitions");
                    for (int j = 0; j < definitionsJson.length(); j++) {
                        JSONObject definitionJson = definitionsJson.getJSONObject(j);
                        String definition = definitionJson.getString("definition");
                        assert definitions != null;
                        definitions.add(definition);
                    }
                }
            } else {
                JSONObject json = (JSONObject) jsonRaw;
                return json
                        .getString("message");
            }


        } catch (Exception e) {
            XposedBridge.log(e);
            return null;
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, ArrayList<String>> definitionEntry : allDefinitions.entrySet()) {
            result.append(MessageUnicodeConverter.underline(word));
            result.append(". ");
            result.append(MessageUnicodeConverter.bold(definitionEntry.getKey()));
            result.append("\n");
            ArrayList<String> definitions = definitionEntry.getValue();
            for (String definition : definitions) {
                result.append("- ");
                result.append(definition);
                result.append("\n");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
