package com.everflow.exercise

import com.everflow.grammar.Rule
import groovy.json.JsonSlurper
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

/**
 * @author Ildar Gafarov
 */
@Service
class ExerciseRepository {

    List<Exercise> exercises = new ArrayList<>();

    @PostConstruct
    private void init() {
        exercises = getAll();
    }

    public Exercise get(Long id) {
        return exercises.find { it.id == id }
    }

    public List<Exercise> getAll(Rule rule) {
        List<Exercise> result = []
        exercises.each { exercise ->
            if (exercise.rules.find { it.rule == rule } != null) {
                result.add(exercise)
            }
        }
        return result
    }

    public List<Exercise> getAll() {
        def result = []
        def jsonSlurper = new JsonSlurper()
        def loaded = jsonSlurper.parse(load())
        loaded.each { e ->
            List<ExerciseRule> rules = []
            e.rules.each { r ->
                def parts = []
                r.parts.each { p ->
                    parts.add(p)
                }
                rules.add(new ExerciseRule(parts, Rule.findByCode(r.rule)))
            }
            result.add(new Exercise(e.id as Long, e.translate as String, e.sentence as String, rules as List))
        }
        return result
    }

    private File load() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("sentences.json").getFile());
        return file
    }

}
