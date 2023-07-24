package tn.amin.mpro2.features.util.message.command.api;

import androidx.annotation.NonNull;

public class LatexAPI extends AbstractAPI {
    @NonNull
    public static String getLinkToLatexImage(String latex) {
        setUrlFormat("https://latex.codecogs.com/gif.image?%s");
        return getUrl(latex);
    }
}
