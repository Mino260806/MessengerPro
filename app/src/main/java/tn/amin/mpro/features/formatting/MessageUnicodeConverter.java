package tn.amin.mpro.features.formatting;
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
		
		public enum ConversionMethod {
			CONVERSION_METHOD_ADD,
			CONVERSION_METHOD_SHIFT
		}
		
		CharUnicodeConverter(ConversionMethod type, int offset) {
			mType = type;
			mOffset = offset;
		}

		public String convert(char c) {
			switch (mType) {
				case CONVERSION_METHOD_ADD:
					return new StringBuilder()
						.append(c)
						.append((char)mOffset)
						.toString();
				case CONVERSION_METHOD_SHIFT:
					if (!Character.isLetter(c))
						return String.valueOf(c);
					int offset = mOffset;
					if (Character.isUpperCase(c)) {
						offset -= 0x1a;
						c = Character.toLowerCase(c);
					}
					return new String(Character.toChars((c - 'a') + offset));
				
				default:
					return String.valueOf(c);
			}
		}
	}
	
	static {
		mSpecialSymbols.put("_", new CharUnicodeConverter(CharUnicodeConverter.ConversionMethod.CONVERSION_METHOD_ADD, 0x35F));
		mSpecialSymbols.put("-", new CharUnicodeConverter(CharUnicodeConverter.ConversionMethod.CONVERSION_METHOD_ADD, 0x336));
		mSpecialSymbols.put("*", new CharUnicodeConverter(CharUnicodeConverter.ConversionMethod.CONVERSION_METHOD_SHIFT, 0x1D5EE));
		mSpecialSymbols.put("!", new CharUnicodeConverter(CharUnicodeConverter.ConversionMethod.CONVERSION_METHOD_SHIFT, 0x1D622));
	}

	public static String bold(String s) { return MessageUnicodeConverter.convert(s, "*"); }
	public static String italic(String s) { return MessageUnicodeConverter.convert(s, "!"); }
	public static String underline(String s) { return MessageUnicodeConverter.convert(s, "_"); }
	public static String crossOut(String s) { return MessageUnicodeConverter.convert(s, "-"); }
}
