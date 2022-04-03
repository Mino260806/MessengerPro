package tn.amin.mpro.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.robv.android.xposed.XposedBridge;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.MainHook;
import tn.amin.mpro.R;
import tn.amin.mpro.builders.MediaResourceBuilder;
import tn.amin.mpro.features.commands.api.FreeDictionaryAPI;
import tn.amin.mpro.features.commands.api.RedditAPI;
import tn.amin.mpro.features.commands.api.WikipediaAPI;

public class CommandsManager {
    public static MainHook mainHook = null;

    private static final CommandDispatcher<Object> mDispatcher = new CommandDispatcher<>();
    private static final ArrayList<CommandFields> mCommands = new ArrayList<>();
    private ParseResults<Object> mCachedParseResults;

    static {
        mDispatcher.register(literal("word")
                .then(literal("pronounce").then(argument("word", word()).executes(c -> comWordDefinition(getString(c, "word"), "pronounce")
        ))));
        mDispatcher.register(literal("reddit")
                .then(argument("subreddit", string()).executes(c -> comLatestPost(getString(c, "subreddit"), ""))
                .then(argument("sort", word()).executes(c -> comLatestPost(getString(c, "subreddit"), getString(c, "sort"))))));
        mDispatcher.register(literal("wikipedia")
                .then(argument("language", word())
                .then(argument("term", greedyString()).executes(c -> comWikipedia(getString(c, "term"), getString(c, "language"))))));

        Resources res = MProMain.getMProResources();
        mCommands.add(new CommandFields("word", res.getString(R.string.command_description_word)));
        mCommands.add(new CommandFields("reddit", res.getString(R.string.command_description_reddit)));
        mCommands.add(new CommandFields("wikipedia", res.getString(R.string.command_description_wikipedia)));
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

    private static int comLatestPost(String subreddit, String sort) {
        new Thread(() -> {
            String postDescription = RedditAPI.fetchLatestPost(subreddit, sort);
            // After getting the result
            MProMain.getActivity().runOnUiThread(() ->
                    MProMain.sendMessage(postDescription, true));
        }).start();
        return 1;
    }

    private static int comWordDefinition(String word, String type) {
        new Thread(() -> {
            switch (type) {
                case "pronounce": {
                    String url = FreeDictionaryAPI.fetchPronunciation(word);
                    Object mediaResource = MediaResourceBuilder.createFromUrl(url)
                            .setType("AUDIO")
                            .build();
                    MProMain.getActivity().runOnUiThread(() ->
                            MProMain.sendAttachment(mediaResource));
                }
            }
        }).start();
        return 1;
    }

    private static int comWikipedia(String term, String language) {
        new Thread(() -> {
            String article = WikipediaAPI.fetchArticle(term, language);
            // After getting the result
            MProMain.getActivity().runOnUiThread(() ->
                    MProMain.sendMessage(article, true));
        }).start();
        return 1;
    }
}
