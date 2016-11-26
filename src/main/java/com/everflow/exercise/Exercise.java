package com.everflow.exercise;

import java.util.List;

/**
 * @author Ildar Gafarov
 */
public class Exercise {
    private Long id;
    private String sentence;
    private String translate;
    private List<ExerciseRule> rules;

    public Exercise(Long id, String translate, String sentence, List<ExerciseRule> rules) {
        this.id = id;
        this.translate = translate;
        this.sentence = sentence;
        this.rules = rules;
    }

    public Long getId() {
        return id;
    }

    public String getSentence() {
        return sentence;
    }

    public String getTranslate() {
        return translate;
    }

    public List<ExerciseRule> getRules() {
        return rules;
    }
}
