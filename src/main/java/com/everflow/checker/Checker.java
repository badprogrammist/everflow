package com.everflow.checker;

import com.everflow.exercise.Exercise;
import com.everflow.exercise.ExerciseRepository;
import com.everflow.exercise.ExerciseRule;
import com.everflow.grammar.Rule;
import com.everflow.parser.Parser;
import com.everflow.sentence.Sentence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ildar Gafarov
 */
@Service
public class Checker {

    @Autowired
    Parser parser;

    @Autowired
    DifferenceFinder differenceFinder;

    @Autowired
    ExerciseRepository exerciseRepository;

    public Result check(Entered entered) {
        Exercise exercise = exerciseRepository.get(entered.getId());
        Sentence enteredSentence = parser.parseSentence(entered.getText());
        Sentence rightSentence = parser.parseSentence(exercise.getSentence());
        List<Difference> differences = differenceFinder.find(rightSentence, enteredSentence);
        Map<Rule, Boolean> rulesResult = new HashMap<>();

        if(exercise.getSentence().equalsIgnoreCase(entered.getText())) {
            for(ExerciseRule exerciseRule: exercise.getRules()) {
                rulesResult.put(exerciseRule.getRule(), true);
            }
        } else {
            for(ExerciseRule exerciseRule: exercise.getRules()) {
                rulesResult.put(exerciseRule.getRule(), false);
            }
        }

        for(Difference difference : differences) {
            for(ExerciseRule exerciseRule: exercise.getRules()) {
                for(String part : exerciseRule.getParts()) {
                    if(part.equalsIgnoreCase(difference.getRight().getSource())) {
                        rulesResult.put(exerciseRule.getRule(), false);
                    }
                }
            }
        }
        return new Result(exercise, rulesResult);
    }

}
