package tn.amin.mpro2.features.util.message.command;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

import android.os.Handler;
import android.os.Looper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.io.File;
import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;
import tn.amin.mpro2.R;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.util.message.command.api.ApiResult;
import tn.amin.mpro2.features.util.message.command.api.DuckDuckGoAPI;
import tn.amin.mpro2.features.util.message.command.api.FreeDictionaryAPI;
import tn.amin.mpro2.features.util.message.command.api.RedditAPI;
import tn.amin.mpro2.features.util.message.command.api.WikipediaAPI;
import tn.amin.mpro2.file.FileHelper;
import tn.amin.mpro2.messaging.MessageSender;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.OrcaStickers;
import tn.amin.mpro2.orca.datatype.MediaAttachment;
import tn.amin.mpro2.util.StringUtil;

public class CommandsManager {
    private final OrcaGateway gateway;
    private static final CommandDispatcher<Object> mDispatcher = new CommandDispatcher<>();
    private static final ArrayList<CommandFields> mCommands = new ArrayList<>();

    private ParseResults<Object> mCachedParseResults;

    static {
        mCommands.add(new CommandFields("word", "stub"));
        mCommands.add(new CommandFields("reddit", "stub"));
        mCommands.add(new CommandFields("wikipedia", "stub"));
        mCommands.add(new CommandFields("like", "stub"));
        mCommands.add(new CommandFields("empty", "stub"));
    }

    public CommandsManager(OrcaGateway gateway) {
        this.gateway = gateway;

        mDispatcher.register(literal("word")
                .then(literal("pronounce").then(argument("word", greedyString()).executes(c -> comAPI("word pronounce", c))))
                .then(literal("define").then(argument("word", greedyString()).executes(c -> comAPI("word define", c)))));
        mDispatcher.register(literal("reddit")
                .then(argument("subreddit", string()).executes(c -> comAPI("reddit", c))
                        .then(argument("sort", word()).executes(c -> comAPI("reddit", c)))));
        mDispatcher.register(literal("wikipedia")
                .then(argument("language", word())
                        .then(argument("term", greedyString()).executes(c -> comAPI("wikipedia", c)))));
        mDispatcher.register(literal("search")
                .then(argument("term", greedyString()).executes(c -> comAPI("search", c))));
        mDispatcher.register(literal("like").executes(c -> comLike(1, c))
                .then(argument("size", integer(1, 3)).executes(c -> comLike(getInteger(c, "size"), c))));
        mDispatcher.register(literal("empty").executes(c -> comEmpty(1, 1, c))
                .then(argument("row", integer(1)).executes(c -> comEmpty(getInteger(c, "row"), 1, c))
                        .then(argument("column", integer(1)).executes(c -> comEmpty(getInteger(c, "row"), getInteger(c, "column"), c)))));
    }

    private void update(String message, CommandBundle source) {
        if (!message.startsWith("/")) return;
        message = message.substring(1).trim();

        mCachedParseResults = mDispatcher.parse(message, source);
    }

    public boolean execute(String message, MessageSender messageSender) {
        try {
            CommandBundle bundle = new CommandBundle(messageSender);

            update(message, bundle);
            if (mCachedParseResults.getReader().canRead()) {
                return false;
            }

            mDispatcher.execute(mCachedParseResults);
            return true;
        } catch (CommandSyntaxException e) {
            Logger.error(e);
            return false;
        }
    }

    private ApiResult comReddit(String subreddit, String sort) {
        if (sort == null) sort = "";

        String postDescription = RedditAPI.fetchLatestPost(subreddit, sort);
        return new ApiResult.SendText(postDescription);
    }

    private ApiResult comWordDefinition(String word, String type) {
        switch (type) {
            case "pronounce": {
                String url = FreeDictionaryAPI.fetchPronunciation(word);
                if (url.isEmpty()) {
                    return new ApiResult.SendText(gateway.res.getText(R.string.no_pronunciation));
                }

                File pronunciation = FileHelper.downloadFromUrl(url);
                if (pronunciation != null) {
                    return new ApiResult.SendMedia(new MediaAttachment(pronunciation));
                }
            }
            case "define": {
                String defintions = FreeDictionaryAPI.fetchDefinitions(word);
                if (defintions != null) {
                    return new ApiResult.SendText(defintions);
                }
            }
        }
        XposedBridge.log(new UnknownError());
        return new ApiResult.SendText(gateway.res.getText(R.string.unexpected_error));
    }

    private ApiResult comWikipedia(String term, String language) {
        if (language == null || language.isEmpty()) language = "en";
        String article = WikipediaAPI.fetchArticle(term, language);
        return new ApiResult.SendText(article);
    }

    private ApiResult comSearch(String term) {
        String result = DuckDuckGoAPI.fetchSearchResult(term, "en-us");
        return new ApiResult.SendText(result);
    }

    private int comLike(int likeSize, CommandContext c) {
        MessageSender messageSender = ((CommandBundle)c.getSource()).messageSender;

        switch (likeSize) {
            case 1:
                messageSender.sendSticker(OrcaStickers.LIKE_SMALL);
                break;
            case 2:
                messageSender.sendSticker(OrcaStickers.LIKE_MEDIUM);
                break;
            case 3:
                messageSender.sendSticker(OrcaStickers.LIKE_BIG);
                break;
        }
        return 1;
    }

    private int comEmpty(int row, int column, CommandContext c) {
        MessageSender messageSender = ((CommandBundle) c.getSource()).messageSender;

        final String delim = "\u0020\u200D\u0020";
        String rowMessage = delim + StringUtil.multiply(" ", column);
        if (column > 1) rowMessage += delim;
        rowMessage += '\n';
        String message = StringUtil.multiply(rowMessage, row);;

        messageSender.sendMessage(message);

        return 1;
    }

    private int comAPI(String api, CommandContext c) {
        CommandBundle bundle = (CommandBundle) c.getSource();
        MessageSender messageSender = bundle.messageSender;

        new Thread(() -> {
            final ApiResult apiResult;
            switch (api) {
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
                case "search":
                    apiResult = comSearch(getString(c, "term"));
                    break;
                default:
                    XposedBridge.log(new UnknownError());
                    apiResult = new ApiResult.SendText(gateway.res.getText(R.string.unexpected_error));
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                apiResult.revealResult(messageSender);
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
