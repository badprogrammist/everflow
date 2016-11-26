package com.everflow.language;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Ildar Gafarov on 14.09.16.
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
