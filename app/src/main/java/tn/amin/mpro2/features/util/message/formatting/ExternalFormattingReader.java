package tn.amin.mpro2.features.util.message.formatting;

import androidx.core.util.Consumer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tn.amin.mpro2.debug.Logger;

public class ExternalFormattingReader {
    private static final Pattern PATTERN_HEADER = Pattern.compile("([^ ]) *([^ ]*)");
    private static final Pattern PATTERN_REPLACEMENT = Pattern.compile("([^ ]+) ([^ ]+)");

    public static void readAndExtend(File folder, Consumer<FormattingDecodeResult> onDecodeFile) {
        File[] files = folder.listFiles();
        if (files == null)
            return;

        for (File file: files) {
            FormattingDecodeResult decodeResult = decodeFile(file);
            if (decodeResult.success) {
                onDecodeFile.accept(decodeResult);
            }
        }
    }

    private static FormattingDecodeResult decodeFile(File file) {
        Logger.info("decoding file " + file.getAbsolutePath());

        Scanner scanner = null;

        char delimiter = '\0';
        String rawOptions = "";
        Map<String, String> charMap = new HashMap<>();

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            return FormattingDecodeResult.FAILED;
        }

        while (delimiter == '\0' && scanner.hasNext()) {
            String line = scanner.nextLine().trim();

            Matcher matcher = PATTERN_HEADER.matcher(line);
            if (matcher.find()) {
                if (matcher.groupCount() >= 1) {
                    delimiter = matcher.group(1).charAt(0);
                    if (matcher.groupCount() >= 2) {
                        rawOptions = matcher.group(2);
                    }
                }
            }
        }
        Logger.info("delimiter is " + delimiter);

        if (delimiter == '\0') {
            return FormattingDecodeResult.FAILED;
        }

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            Matcher matcher = PATTERN_REPLACEMENT.matcher(line);
            if (matcher.find()) {
                if (matcher.groupCount() >= 2) {
                    String character = matcher.group(1);
                    String replacement = matcher.group(2);

                    charMap.put(character, replacement);
                }
            }
        }

        if (charMap.isEmpty()) {
            return FormattingDecodeResult.FAILED;
        }

        Logger.info("decoding success !");
        return new FormattingDecodeResult(true, delimiter, charMap, new FormattingOptions(rawOptions));
    }

    public static class FormattingDecodeResult {
        boolean success;

        public FormattingDecodeResult(boolean success, Character delimiter, Map<String, String> charMap, FormattingOptions options) {
            this.success = success;
            this.delimiter = delimiter;
            this.charMap = charMap;
            this.options = options;
        }

        Character delimiter;
        Map<String, String> charMap;
        FormattingOptions options;

        final static FormattingDecodeResult FAILED = new FormattingDecodeResult(false, null, null, null);
    }

    public static class FormattingOptions {
        boolean reverse = false;

        public FormattingOptions(String raw) {
            for (int i=0; i<raw.length(); i++) {
                switch (raw.charAt(i)) {
                    case 'R':
                    case 'r':
                        reverse = true;
                        break;
                }
            }
        }
    }
}
