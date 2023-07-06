package tn.amin.mpro2.orca.datatype;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tn.amin.mpro2.util.IntRange;
import tn.amin.mpro2.util.StringUtil;

public class Mention {
    public IntRange range;
    public Long threadKey;
    public Type type;

    public Mention(IntRange range, Long threadKey, Type type) {
        this.range = range;
        this.threadKey = threadKey;
        this.type = type;
    }

    public static String joinRangeStarts(List<Mention> mentions) {
        if (mentions.isEmpty()) return "";

        return mentions.stream().map((mention -> String.valueOf(mention.range.start)))
                .collect(Collectors.joining(","));
    }


    public static String joinRangeEnds(List<Mention> mentions) {
        if (mentions.isEmpty()) return "";

        return mentions.stream().map((mention -> String.valueOf(mention.range.end)))
                .collect(Collectors.joining(","));
    }


    public static String joinThreadKeys(List<Mention> mentions) {
        if (mentions.isEmpty()) return "";

        return mentions.stream().map((mention -> String.valueOf(mention.threadKey)))
                .collect(Collectors.joining(","));
    }

    public static String joinTypes(List<Mention> mentions) {
        if (mentions.isEmpty()) return "";

        return mentions.stream().map((mention -> mention.type.symbol))
                .collect(Collectors.joining(","));
    }

    public static List<Mention> fromDispatchArgs(String message,
                                                 @Nullable String rangeStartsString,
                                                 @Nullable String rangeEndsString,
                                                 @Nullable String threadKeysString,
                                                 @Nullable String typesString) {

        String[] rangeStarts = StringUtil.nullToEmpty(rangeStartsString).split(",");
        String[] rangeEnds = StringUtil.nullToEmpty(rangeEndsString).split(",");
        String[] threadKeys = StringUtil.nullToEmpty(threadKeysString).split(",");
        String[] types = StringUtil.nullToEmpty(typesString).split(",");

        ArrayList<Mention> mentions = new ArrayList<>();
        for (int i=0; i<threadKeys.length; i++) {
            if (!NumberUtils.isDigits(threadKeys[i])) continue;

            long threadKey = Long.parseLong(threadKeys[i]);
            int rangeStart = 0;
            int rangeEnd = message.length();
            Mention.Type type = Type.PERSON;
            if (i < rangeStarts.length && NumberUtils.isDigits(rangeStarts[i])) {
                rangeStart = Integer.parseInt(rangeStarts[i]);
            }
            if (i < rangeEnds.length && NumberUtils.isDigits(rangeEnds[i])) {
                rangeEnd = Integer.parseInt(rangeEnds[i]);
            }
            if (i < types.length && Type.isValidSymbol(types[i])) {
                type = Type.valueOfSymbol(types[i]);
            }

            Mention mention = new Mention(new IntRange(rangeStart, rangeEnd), threadKey, type);
            mentions.add(mention);
        }

        return mentions;
    }

    public enum Type {
        PERSON("p"),
        GROUP("t");

        public String symbol;

        Type(String symbol) {
            this.symbol = symbol;
        }

        public static Type valueOfSymbol(String symbol) {
            for (Type t : values()) {
                if (symbol.equals(t.symbol)) {
                    return t;
                }
            }
            return null;
        }

        public static boolean isValidSymbol(String symbol) {
            for (Type t : values()) {
                if (symbol.equals(t.symbol)) {
                    return true;
                }
            }

            return false;
        }
    }
}
