package com.everflow.language;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Ildar Gafarov
 */
public enum Gender {
    FEMALE("ж"),
    MALE("м"),
    NEUTER("ср");

    Gender(String title) {
        this.title = title;
    }

    private final String title;

    @JsonValue
    public String getTitle() {
        return title;
    }


}
