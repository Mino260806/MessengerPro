package tn.amin.mpro2.features.util.message.formatting.conversion;

public class ShiftConversionMethod implements ConversionMethod {
    private int mOffset;

    public ShiftConversionMethod(int offset) {
        mOffset = offset;
    }

    @Override
    public String convert(char c) {
        if (!ConversionMethod.isAscii(c))
            return String.valueOf(c);
        int offset = mOffset;
        if (Character.isUpperCase(c)) {
            offset -= 0x1a;
            c = Character.toLowerCase(c);
        }
        return new String(Character.toChars((c - 'a') + offset));
    }
}
