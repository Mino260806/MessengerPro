package tn.amin.mpro2.features.util.message.formatting;

public interface CharacterFilter {
    char NULL_CHAR = '\0';
    CharacterFilter noCharacterFilter = (CharacterFilter) c -> NULL_CHAR;

    char filterCharacter(char c);
}
