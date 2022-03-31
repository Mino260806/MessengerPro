package tn.amin.mpro.features.formatting;
import java.util.regex.*;
import android.text.*;

public class MessageFormatter 
{
	final static String COMMAND_PREFIX = "/";
	final static Pattern PATTERN_TOKENIZER = Pattern.compile("[_*\\-!][^_*\\-!]+[_*\\-!]");
	final static Pattern PATTERN_URL = Pattern.compile("(?:https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

	static {
		PatternProtector.setPattern(PATTERN_URL);
	}

	public static String commandify(String commandName) {
		return COMMAND_PREFIX + commandName;
	}
	
	public static void format(Editable text) {
		PatternProtector patternProtector = new PatternProtector();
		patternProtector.protect(text);
		Matcher tokensMatcher = PATTERN_TOKENIZER.matcher(text.toString());
		int shift = 0; // In case formattedMatch size is different from original match
		while (tokensMatcher.find()) {
			String match = tokensMatcher.group();
			String formattedMatch = MessageUnicodeConverter.convert(match);
			
			text.delete(tokensMatcher.start() + shift, tokensMatcher.end() + shift);
			text.insert(tokensMatcher.start() + shift, formattedMatch);
			shift += formattedMatch.length() - match.length();
		}
		patternProtector.unprotect(text);
	}
} 
