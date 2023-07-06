package tn.amin.mpro2.features.util.message.formatting.conversion;

import tn.amin.mpro2.features.util.message.formatting.CharacterTable;

public class ReplaceConversionMethod implements ConversionMethod {
    private final CharacterTable mTable;

    public ReplaceConversionMethod(CharacterTable table) {
        mTable = table;
    }

    @Override
    public String convert(char c) {
        return mTable.replace(c);
    }
}
