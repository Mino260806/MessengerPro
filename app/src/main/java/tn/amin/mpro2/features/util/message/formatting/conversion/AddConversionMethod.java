package tn.amin.mpro2.features.util.message.formatting.conversion;

public class AddConversionMethod implements ConversionMethod {
    private int mOffset;
    public AddConversionMethod(int offset) {
        mOffset = offset;
    }

    @Override
    public String convert(char c) {
        if (Character.isSpaceChar(c))
            return String.valueOf(c);
        return new StringBuilder()
                .append(c)
                .append((char)mOffset)
                .toString();
    }
}
