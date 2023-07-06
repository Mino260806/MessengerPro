package tn.amin.mpro2.features.util.message;

public class Mocker {
    public static String mock(String message) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;
        for (int i=0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (Character.isLetter(message.charAt(i))) {
                if (upper) result.append(Character.toUpperCase(c));
                else result.append(Character.toLowerCase(c));

                upper = !upper;
            }

            else result.append(c);
        }
        return result.toString();
    }
}
