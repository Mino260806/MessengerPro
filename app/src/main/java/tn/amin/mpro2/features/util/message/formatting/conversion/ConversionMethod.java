package tn.amin.mpro2.features.util.message.formatting.conversion;

import java.nio.charset.StandardCharsets;

public interface ConversionMethod {
    String convert(char c);

    static boolean isAscii(char c) {
        return (StandardCharsets.US_ASCII.newEncoder().canEncode(c)) &&
                Character.isLetter(c);
    }
}
