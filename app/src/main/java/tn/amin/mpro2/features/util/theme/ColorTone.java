package tn.amin.mpro2.features.util.theme;

public enum ColorTone {
    TONE_1000(1000),
    TONE_900(900),
    TONE_800(800),
    TONE_700(700),
    TONE_600(600),
    TONE_500(500),
    TONE_400(400),
    TONE_300(300),
    TONE_200(200),
    TONE_100(100),
    TONE_50(50),
    TONE_10(10),
    TONE_0(0),
    ;

    public final int luminescence;

    ColorTone(int luminescence) {
        this.luminescence = luminescence;
    }
}
