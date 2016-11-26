package com.everflow.checker

import com.everflow.exercise.Exercise
import com.everflow.grammar.Rule

/**
 * @author Ildar Gafarov (ildar@skybonds.com)
 */
class Result {
    Map<Rule, Boolean> rules
    Exercise exercise

    Result(Exercise exercise, Map<Rule, Boolean> rules) {
        this.rules = rules
        this.exercise = exercise
    }

    boolean isSuccess() {
        def result = true
        rules.each {rule, status ->
            if(!status) {
                result = false
            }
        }
        return result
    }

}
