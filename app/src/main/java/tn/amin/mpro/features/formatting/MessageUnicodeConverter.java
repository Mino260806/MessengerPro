package tn.amin.mpro.features.formatting;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;

public class MessageUnicodeConverter
{
	private static final HashMap<String, CharUnicodeConverter> mSpecialSymbols = new HashMap<>();
	
	public static String convert(String text) {
		String token = String.valueOf(text.charAt(0));
		String textPortion = text.substring(1, text.length() - 1);
		
		return convert(textPortion, token);
	}

	public static String convert(String text, String token) {
		// Decompose accents
		text = Normalizer.normalize(text, Normalizer.Form.NFD);

		StringBuilder formattedText = new StringBuilder();
		CharUnicodeConverter unicodeConverter = mSpecialSymbols.get(token);
		for (int i=0; i<text.length(); i++) {
			char c = text.charAt(i);
			String formattedChar = unicodeConverter.convert(c);
			formattedText.append(formattedChar);
		}

		return formattedText.toString();
	}
	
	private static class CharUnicodeConverter {
		private ConversionMethod mType;
		private int mOffset;
		private CharacterFilter mCharacterFilter = CharacterFilter.noCharacterFilter;

		public enum ConversionMethod {
			CONVERSION_METHOD_ADD,
			CONVERSION_METHOD_SHIFT
		}

		CharUnicodeConverter(ConversionMethod type, int offset) {
			mType = type;
			mOffset = offset;
		}
		CharUnicodeConverter(ConversionMethod type, int offset, CharacterFilter characterFilter) {
			this(type, offset);
			mCharacterFilter = characterFilter;
		}

		public String convert(char c) {
			// Ignore line feed and carriage return
			if (c == '\n' || c == '\r')
				return String.valueOf(c);
			// CharacterFilter gets executed first
			char filteredChar = mCharacterFilter.filterCharacter(c);
			if (filteredChar != CharacterFilter.NULL_CHAR)
				return String.valueOf(filteredChar);
			// If CharacterFilter returns NULL_CHAR, (ignores the c), proceed to the universal
			// conversion methods.
			switch (mType) {
				case CONVERSION_METHOD_ADD:
					if (Character.isSpaceChar(c))
						break;
					return new StringBuilder()
						.append(c)
						.append((char)mOffset)
						.toString();
				case CONVERSION_METHOD_SHIFT:
					// Shifting will only work with ascii chars
					if (!isAscii(c))
						break;
					int offset = mOffset;
					if (Character.isUpperCase(c)) {
						offset -= 0x1a;
						c = Character.toLowerCase(c);
					}
					return new String(Character.toChars((c - 'a') + offset));
				
				default:
					break;
			}
			return String.valueOf(c);
		}
	}
	
	static {
		mSpecialSymbols.put("*", new CharUnicodeConverter(CharUnicodeConverter.ConversionMethod.CONVERSION_METHOD_SHIFT, 0x1D5EE));
		mSpecialSymbols.put("!", new CharUnicodeConverter(CharUnicodeConverter.ConversionMethod.CONVERSION_METHOD_SHIFT, 0x1D622));
		mSpecialSymbols.put("-", new CharUnicodeConverter(CharUnicodeConverter.ConversionMethod.CONVERSION_METHOD_ADD, 0x336));
		mSpecialSymbols.put("_", new CharUnicodeConverter(CharUnicodeConverter.ConversionMethod.CONVERSION_METHOD_ADD, 0x35F, c -> {
			// Non alphanumeric characters do not use the same "y" level for underline
			// so the underlining line becomes misaligned and ugly. We are obliged to disable
			// for all non alphanumeric chars as a result. Note also that the "y" level for underlining
			// numbers is not same as letters, but it's not very important.
			if (Character.isLetterOrDigit(c))
				return CharacterFilter.NULL_CHAR; // proceed to normal conversion
			return c; // keep c as it is
		}));
	}

	public static String bold(String s) { return MessageUnicodeConverter.convert(s, "*"); }
	public static String italic(String s) { return MessageUnicodeConverter.convert(s, "!"); }
	public static String underline(String s) { return MessageUnicodeConverter.convert(s, "_"); }
	public static String crossOut(String s) { return MessageUnicodeConverter.convert(s, "-"); }

	private static boolean isAscii(char c) {
		return (StandardCharsets.US_ASCII.newEncoder().canEncode(c)) &&
				!Character.isSpaceChar(c);
	}
}
