package tn.amin.mpro2.features.util.message.formatting;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tn.amin.mpro2.features.util.message.formatting.conversion.AddConversionMethod;
import tn.amin.mpro2.features.util.message.formatting.conversion.ConversionMethod;
import tn.amin.mpro2.features.util.message.formatting.conversion.ReplaceConversionMethod;
import tn.amin.mpro2.features.util.message.formatting.conversion.ShiftConversionMethod;
import tn.amin.mpro2.file.StorageConstants;

public class MessageUnicodeConverter
{
	private static final HashMap<String, CharUnicodeConverter> mSpecialSymbols = new HashMap<>();
	public static List<Character> importedDelimiters = null;

	public static String convert(String text) {
		String token = String.valueOf(text.charAt(0));
		String textPortion = text.substring(1, text.length() - 1);
		
		return convert(textPortion, token);
	}

	public static String convert(String text, String token) {
		// Decompose accents
		text = Normalizer.normalize(text, Normalizer.Form.NFD);

		StringBuilder formattedTextBuilder = new StringBuilder();
		CharUnicodeConverter unicodeConverter = mSpecialSymbols.get(token);

		String formattedText;
		if (unicodeConverter != null) {
			for (int i=0; i<text.length(); i++) {
				char c = text.charAt(i);
				String formattedChar = unicodeConverter.convert(c);
				formattedTextBuilder.append(formattedChar);
			}

			formattedText = unicodeConverter.finishingTouches(formattedTextBuilder);
		} else {
			formattedText = text;
		}

//		Logger.log("Formatted sb is " + formattedTextBuilder);
//		Logger.log("Formatted string is " + formattedText);
		return formattedText;
	}
	
	static class CharUnicodeConverter {
		private ConversionMethod mConversionMethod;
		private CharacterFilter mCharacterFilter = CharacterFilter.noCharacterFilter;

		private boolean mReverse = false;

		CharUnicodeConverter(ConversionMethod method) {
			mConversionMethod = method;
		}

		public CharUnicodeConverter withFilter(CharacterFilter characterFilter) {
			mCharacterFilter = characterFilter;
			return this;
		}

		public CharUnicodeConverter reverse(boolean reverse) {
			mReverse = reverse;
			return this;
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

			return mConversionMethod.convert(c);
		}

		public String finishingTouches(StringBuilder formattedString) {
			if (mReverse)
				formattedString.reverse();
			return formattedString.toString();
		}
	}
	
	static {
		mSpecialSymbols.put("*", new CharUnicodeConverter(new ShiftConversionMethod( 0x1D5EE)));
		mSpecialSymbols.put("!", new CharUnicodeConverter(new ShiftConversionMethod( 0x1D622)));
		mSpecialSymbols.put("~", new CharUnicodeConverter(new AddConversionMethod( 0x336)));
		mSpecialSymbols.put("_", new CharUnicodeConverter(new AddConversionMethod( 0x35F)).withFilter(c -> {
			// Non alphanumeric characters do not use the same "y" level for underline
			// so the underlining line becomes misaligned and ugly. We are obliged to disable
			// for all non alphanumeric chars as a result. Note also that the "y" level for underlining
			// numbers is not same as letters, but it's not very important.
			if (Character.isLetterOrDigit(c))
				return CharacterFilter.NULL_CHAR; // proceed to normal conversion
			return c; // keep c as it is
		}));

		ArrayList<Character> delimiters = new ArrayList<>();
		ExternalFormattingReader.readAndExtend(new File(StorageConstants.moduleFiles, "formatting"), (result) -> {
			mSpecialSymbols.put(result.delimiter.toString(),
					new CharUnicodeConverter(new ReplaceConversionMethod(new CharacterTable(result.charMap)))
						.reverse(result.options.reverse));
			delimiters.add(result.delimiter);
		});

		importedDelimiters = delimiters;
	}

	public static String bold(String s) { return MessageUnicodeConverter.convert(s, "*"); }
	public static String italic(String s) { return MessageUnicodeConverter.convert(s, "!"); }
	public static String underline(String s) { return MessageUnicodeConverter.convert(s, "_"); }
	public static String crossOut(String s) { return MessageUnicodeConverter.convert(s, "~"); }

	private static boolean isAscii(char c) {
		return (StandardCharsets.US_ASCII.newEncoder().canEncode(c)) &&
				Character.isLetter(c);
	}
}
