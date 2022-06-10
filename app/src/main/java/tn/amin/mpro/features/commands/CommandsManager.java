package tn.amin.mpro.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import static com.mojang.brigadier.arguments.IntegerArgumentType.*;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.R;
import tn.amin.mpro.builders.MediaResourceBuilder;
import tn.amin.mpro.builders.dialog.LoadingDialogBuilder;
import tn.amin.mpro.features.commands.api.ApiResult;
import tn.amin.mpro.features.commands.api.FreeDictionaryAPI;
import tn.amin.mpro.features.commands.api.OpenAiAPI;
import tn.amin.mpro.features.commands.api.RedditAPI;
import tn.amin.mpro.features.commands.api.WikipediaAPI;
import tn.amin.mpro.utils.StringUtil;

public class CommandsManager {
    private static LoadingDialogBuilder mLoadingDialogBuilder = null;

    private static final CommandDispatcher<Object> mDispatcher = new CommandDispatcher<>();
    private static final ArrayList<CommandFields> mCommands = new ArrayList<>();
    private ParseResults<Object> mCachedParseResults;

    static {
        mDispatcher.register(literal("word")
                .then(literal("pronounce").then(argument("word", greedyString()).executes(c -> comAPI("word pronounce", c))))
                .then(literal("define").then(argument("word", greedyString()).executes(c -> comAPI("word define", c)))));
        mDispatcher.register(literal("reddit")
                .then(argument("subreddit", string()).executes(c -> comAPI("reddit", c))
                .then(argument("sort", word()).executes(c -> comAPI("reddit", c)))));
        mDispatcher.register(literal("wikipedia")
                .then(argument("language", word())
                .then(argument("term", greedyString()).executes(c -> comAPI("wikipedia", c)))));
        mDispatcher.register(literal("like").executes(c -> comLike(1))
                .then(argument("size", integer(1, 3)).executes(c -> comLike(getInteger(c, "size")))));
        mDispatcher.register(literal("empty").executes(c -> comEmpty(1, 1))
                .then(argument("row", integer(1)).executes(c -> comEmpty(getInteger(c, "row"), 1))
                .then(argument("column", integer(1)).executes(c -> comEmpty(getInteger(c, "row"), getInteger(c, "column"))))));
        mDispatcher.register(literal("openai")
                .then(argument("prompt", greedyString()).executes(c -> comAPI("openai", c))));

        Resources res = MProMain.getMProResources();
        mCommands.add(new CommandFields("word", res.getString(R.string.command_description_word)));
        mCommands.add(new CommandFields("reddit", res.getString(R.string.command_description_reddit)));
        mCommands.add(new CommandFields("wikipedia", res.getString(R.string.command_description_wikipedia)));
        mCommands.add(new CommandFields("like", res.getString(R.string.command_description_like)));
        mCommands.add(new CommandFields("empty", res.getString(R.string.command_description_empty)));
        mCommands.add(new CommandFields("openai", res.getString(R.string.command_description_openai)));
    }

    public void update(String message) {
        if (!message.startsWith("/")) return;
        message = message.substring(1).trim();

        Object source = new Object();

        mCachedParseResults = mDispatcher.parse(message, source);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            final Suggestions result = mDispatcher.getCompletionSuggestions(mCachedParseResults).join();
            MProMain.showCommandsAutoComplete(suggestionsToCommands(result.getList()));
        }
    }

    public void execute() {
        try {
            // No need to reparse command since CommandsManager.update always gets called before it
            mDispatcher.execute(mCachedParseResults);
        } catch (CommandSyntaxException e) {
            XposedBridge.log(e);
        }
    }

    public boolean isLastCommandValid() {
        return !mCachedParseResults.getReader().canRead();
    }

    private static List<Object> suggestionsToCommands(List<Suggestion> suggestions) {
        ArrayList<Object> commands = new ArrayList<>(suggestions.size());
        for (Suggestion suggestion: suggestions) {
            CommandFields command = findCommand(suggestion.getText());
            if (command == null) continue;
            commands.add(CommandData.newInstance(command));
        }
        return commands;
    }

    private static CommandFields findCommand(String name) {
        for (CommandFields cf: mCommands) {
            if (cf.name.equals(name))
                return cf;
        }
        return null;
    }

    private static ApiResult comReddit(String subreddit, String sort) {
        if (sort == null) sort = "";

        String postDescription = RedditAPI.fetchLatestPost(subreddit, sort);
        return new ApiResult.SendText(postDescription);
    }

    private static ApiResult comWordDefinition(String word, String type) {
        switch (type) {
            case "pronounce": {
                String url = FreeDictionaryAPI.fetchPronunciation(word);
                if (url.isEmpty()) {
                    Resources res = MProMain.getMProResources();
                    return new ApiResult.SendText(res.getString(R.string.command_error_word_pronounce, word));
                }
                Object mediaResource = MediaResourceBuilder.createFromUrl(url)
                        .setType("AUDIO")
                        .build();
                return new ApiResult.SendMedia(mediaResource);
            }
            case "define": {
                String defintions = FreeDictionaryAPI.fetchDefinitions(word);
                return new ApiResult.SendText(defintions);
            }
            default:
                XposedBridge.log(new UnknownError());
                return new ApiResult.SendText("An unexpected error occurred");
        }
    }

    private static ApiResult comWikipedia(String term, String language) {
        if (language == null || language.isEmpty()) language = "en";
        String article = WikipediaAPI.fetchArticle(term, language);
        return new ApiResult.SendText(article);
    }

    private static int comLike(int likeSize) {
        MProMain.sendLike(likeSize - 1);
        return 1;
    }

    private static int comEmpty(int row, int column) {
        final String delim = "\u0020\u200D\u0020";
        String rowMessage = delim + StringUtil.multiply(" ", column);
        if (column > 1) rowMessage += delim;
        rowMessage += '\n';
        String message = StringUtil.multiply(rowMessage, row);;
        MProMain.sendMessage(message);
        return 1;
    }

    private static ApiResult comOpenAI(String prompt) {
        String response = OpenAiAPI.getCompletion(prompt);
        return new ApiResult.SendText(response);
    }

    private static int comAPI(String api, CommandContext c) {
        mLoadingDialogBuilder = new LoadingDialogBuilder()
                .setText("Fetching command result...");
        mLoadingDialogBuilder.show();
        new Thread(() -> {
            final ApiResult apiResult;
            switch (api) {
                case "openai":
                    apiResult = comOpenAI(getString(c, "prompt"));
                    break;
                case "reddit":
                    String sort;

                    apiResult = comReddit(getString(c, "subreddit"), getStringOrNull(c, "sort"));
                    break;
                case "word define":
                    apiResult = comWordDefinition(getString(c, "word"), "define");
                    break;
                case "word pronounce":
                    apiResult = comWordDefinition(getString(c, "word"), "pronounce");
                    break;
                case "wikipedia":
                    apiResult = comWikipedia(getString(c, "term"), getStringOrNull(c, "language"));
                    break;
                default:
                    XposedBridge.log(new UnknownError());
                    apiResult = new ApiResult.SendText("An unexpected error occurred");
            }
            MProMain.getActivity().runOnUiThread(() -> {
                apiResult.revealResult();
                mLoadingDialogBuilder.dismiss();
            });
        }).start();
        return 1;
    }

    private static String getStringOrNull(CommandContext c, String key) {
        String s;
        try {
            s = getString(c, key);
        } catch (IllegalArgumentException e) {
            s = null;
        }
        return s;
    }
}
