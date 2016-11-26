package com.everflow.language;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Ildar Gafarov on 14.09.16.
 */
public enum PartOfSpeech {
    NOUN("сущ."),
    PRONOUN("мест."),
    ARTICLE("част."),
    VERB("гл."),
    ADJECTIVE("прил."),
    ADVERB("нар."),
    NUMERAL("числ."),
    PREPOSITION("предл."),
    CONJUNCTION("союз"),
    INTERJECTION("межд"),
    PARTICIPLE("прич.");

    PartOfSpeech(String title) {
        this.title = title;
    }

    private final String title;

    @JsonValue
    public String getTitle() {
        return title;
    }
}
