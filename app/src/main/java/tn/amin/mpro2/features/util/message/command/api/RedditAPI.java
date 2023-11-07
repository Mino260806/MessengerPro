package tn.amin.mpro2.features.util.message.command.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import tn.amin.mpro2.features.util.message.formatting.MessageUnicodeConverter;

public class RedditAPI extends AbstractAPI {
    static {
        setUrlFormat("https://www.reddit.com/r/%s/%s/.json?limit=1");
    }

    public static String fetchLatestPost(String subreddit, String sort) {
        Post post = fetchLatestPostInternal(subreddit, sort);
        StringBuilder sb = new StringBuilder();
        sb.append(MessageUnicodeConverter.bold(post.title)).append('\n');
        if (!post.description.isEmpty()) // To prevent extra line break
            sb.append(post.description).append('\n');
        sb.append('\n').append(post.url);
        return sb.toString();
    }

    public static class Post {
        String title = "";
        String description = "";
        String url = "";
        public static Post fromJson(JSONObject data) {
            Post post = new Post();
            try {
                post.description = data.getString("selftext");
                post.title = data.getString("title");
                post.url = data.getString("url");
            } catch (JSONException e) {
                XposedBridge.log(e);
            }
            return post;
        }

    }

    private static Post fetchLatestPostInternal(String subreddit, String sort) {
        final List<String> possibleSorts = Arrays.asList("top", "new", "hot");
        if (!possibleSorts.contains(sort))
            sort = "top";
        try {
            JSONObject json = new JSONObject(fetchData(subreddit, sort).responseString);
            JSONObject postJson = json.getJSONObject("data")
                    .getJSONArray("children")
                    .getJSONObject(0)
                    .getJSONObject("data");
            return Post.fromJson(postJson);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
        return new Post();
    }
}
