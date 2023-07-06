package tn.amin.mpro2.features.util.message.formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;

public class FormattingMatcher implements MatchResult {
    private String mString;
    private List<Character> mSymbols;

    private int mStart = -1;
    private int mEnd = -1;

    public FormattingMatcher(String text, Character[] symbols) {
        mString = text;
        mSymbols = Arrays.asList(symbols);
    }

    public boolean next() {
        return next(new ArrayList<>(mSymbols));
    }

    private boolean next(ArrayList<Character> symbols) {
        Character lockedSymbol = '\0';
        int potentialStart = Integer.MIN_VALUE;
        for (int i = mEnd+1; i < mString.length(); i++) {
            if (lockedSymbol == '\0') {
                for (Character symbol : symbols) {
                    if (mString.charAt(i) == symbol) {
                        lockedSymbol = symbol;
                        potentialStart = i;
                    }
                }
            } else {
                if (mString.charAt(i) == lockedSymbol) {
                    mEnd = i;
                    mStart = potentialStart;
                    return true;
                }
            }
        }

        if (lockedSymbol != '\0' && symbols.size() > 1) {
            symbols.remove(lockedSymbol);
            return next(symbols);
        }

        return false;
    }

    @Override
    public int start() {
        return mStart;
    }

    @Override
    public int start(int i) {
        return start();
    }

    @Override
    public int end() {
        return mEnd;
    }

    @Override
    public int end(int i) {
        return end();
    }

    @Override
    public String group() {
        return mString.substring(mStart, mEnd + 1);
    }

    @Override
    public String group(int i) {
        return group();
    }

    @Override
    public int groupCount() {
        return 1;
    }
}
