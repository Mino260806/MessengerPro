package tn.amin.mpro.features.formatting;
import java.util.regex.*;
import android.text.*;

public class MessageFormatter 
{
	final static String COMMAND_PREFIX = "/";
	final static Pattern pattern = Pattern.compile("[_\\*\\-\\!][^_\\*\\-\\!]+[_\\*\\-\\!]");
	
	public static String commandify(String commandName) {
		return COMMAND_PREFIX + commandName;
	}
	
	public static void format (Editable text) {
		Matcher matcher = pattern.matcher(text.toString());
		int shift = 0; // In case formattedMatch size is different from original match
		while (matcher.find()) {
			String match = matcher.group();
			String formattedMatch = MessageUnicodeConverter.convert(match);
			
			text.delete(matcher.start() + shift, matcher.end() + shift);
			text.insert(matcher.start() + shift, formattedMatch);
			shift += formattedMatch.length() - match.length();
		}
	}
} 
