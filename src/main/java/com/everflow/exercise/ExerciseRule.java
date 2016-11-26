package com.everflow.exercise;

import com.everflow.grammar.Rule;

import java.util.List;

/**
 * @author Ildar Gafarov (ildar@skybonds.com)
 */
public class ExerciseRule {

    List<String> parts;
    Rule rule;

    public ExerciseRule(List<String> parts, Rule rule) {
        this.parts = parts;
        this.rule = rule;
    }

    public List<String> getParts() {
        return parts;
    }

    public Rule getRule() {
        return rule;
    }
}
