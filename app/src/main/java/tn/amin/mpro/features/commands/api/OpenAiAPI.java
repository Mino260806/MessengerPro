package tn.amin.mpro.features.commands.api;

import android.os.Messenger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import de.robv.android.xposed.XposedBridge;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.builders.dialog.LoadingDialogBuilder;
import tn.amin.mpro.builders.dialog.MessengerDialogBuilder;

public class OpenAiAPI extends AbstractAPI {
    private static String mPrompt = "";

    static {
        setUrlFormat("https://api.openai.com/v1/engines/text-davinci-002/completions");
        setOnHttpsURLConnectionCreated(httpsURLConnection -> {
            JSONObject body = new JSONObject()
                    .put("prompt", mPrompt)
                    .put("max_tokens", 3000)
                    .put("top_p", 1)
                    .put("frequency_penalty", 0)
                    .put("presence_penalty", 0)
                    .put("temperature", 0.6);

            httpsURLConnection.setRequestProperty("Content-Type", "application/json");
            httpsURLConnection.setRequestProperty("Authorization", "Bearer " + MProMain.getPrefReader().getOpenAiApiToken());

            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setDoInput(true);
            try {
                OutputStream os = httpsURLConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                osw.write(body.toString());
                osw.flush();
                osw.close();
                os.close();
                httpsURLConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static String getCompletion(String prompt) {
        String apiToken = MProMain.getPrefReader().getOpenAiApiToken();
        if (apiToken.isEmpty()) {
            showDialogInvalidApi();
            return "";
        }

        mPrompt = prompt;
        try {
            HttpResponse response = fetchData();

            JSONObject fetchedData = new JSONObject(response.responseString);
            if (response.responseCode != 200) {
                if (fetchedData.has("error")) {
                    String message = fetchedData
                            .getJSONObject("error")
                            .getString("message");
                    String errorType = fetchedData
                            .getJSONObject("error")
                            .getString("type");
                    if (errorType.equals("invalid_request_error")) {
                        if (message.contains("API key")) {
                            showDialogInvalidApi();
                            return "";
                        }
                        else if (message.contains("maximum context length")) {
                            showDialogError("Maximum length exceeded", "Please shorten your input.");
                            return "";
                        }
                    }

                    XposedBridge.log("[E] " + message);
                    return "An unexpected error of type \"" + errorType + "\" occured.";
                }
                XposedBridge.log("[E] " + fetchedData.toString());
                return "An unexpected error occured.";
            }
            String completion = fetchedData
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getString("text");
            return completion;
        } catch (IOException | JSONException e) {
            XposedBridge.log(e);
        }
        return "An unexpected error occured.";
    }

    public static void showDialogInvalidApi() {
        MProMain.getActivity().runOnUiThread(() -> {
            new MessengerDialogBuilder()
                    .setTitle("Invalid API Token")
                    .setMessage("Make sure to configure your api token in settings.")
                    .setPositiveButton("Settings", (param1, param2) -> { MProMain.startSettings(); })
                    .setNeutralButton(MProMain.getMProResources().getString(android.R.string.cancel), null)
                    .build()
                    .show();
        });
    }

    public static void showDialogError(CharSequence title, CharSequence errorMessage) {
        MProMain.getActivity().runOnUiThread(() -> {
            new MessengerDialogBuilder()
                    .setTitle(title)
                    .setMessage(errorMessage)
                    .setPositiveButton(MProMain.getMProResources().getString(android.R.string.ok), null)
                    .build()
                    .show();
        });
    }
}
