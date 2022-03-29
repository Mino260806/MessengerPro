package tn.amin.mpro.features.formatting;

public interface CharacterFilter {
    char NULL_CHAR = '\0';
    CharacterFilter noCharacterFilter = (CharacterFilter) c -> NULL_CHAR;

    char filterCharacter(char c);
}
