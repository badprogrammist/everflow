package com.everflow.schedule

import com.everflow.exercise.Exercise
import com.everflow.exercise.ExerciseRepository
import com.everflow.grammar.Rule
import com.everflow.profile.ExerciseLog
import com.everflow.profile.Profile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

/**
 * @author Ildar Gafarov (ildar@skybonds.com)
 */
@Service
class Schedule {

    @Autowired
    Profile profile;

    @Autowired
    ExerciseRepository exerciseRepository;

    public Exercise next() {
        Map<Long, ExerciseLog> exercisesLogs = profile.getExercisesTs()
        if(!exercisesLogs.isEmpty()) {
            Map<Long, Long> candidates = [:]
            long now = new Date().time
            exercisesLogs.each {id, exerciseLog ->
                long tsDiff = now - exerciseLog.ts
                long daysLast = TimeUnit.DAYS.convert(tsDiff, TimeUnit.MILLISECONDS)
                if(exerciseLog.day < daysLast) {
                    candidates[id] = exerciseLog.day - daysLast
                }
            }
            if(!candidates.isEmpty()) {
                Map<Long, Long> sorted = candidates.sort { a, b -> b.value <=> a.value }
                return exerciseRepository.get(sorted.entrySet().iterator().next().key)
            } else {
                Set<Rule> usedRules = []
                exercisesLogs.each {id, exerciseLog ->
                    Exercise exercise = exerciseRepository.get(id)
                    List<Rule> exerciseRules = []
                    exercise.rules.each {er ->
                        exerciseRules.add(er.rule)
                    }
                    usedRules.addAll(exerciseRules)
                }
                List<Rule> orderedUsedRules = Rule.order(usedRules)
                Rule last = orderedUsedRules.last()
                List<Exercise> cands = exerciseRepository.getAll(last)
                List<Exercise> notUsed = []
                cands.each { exercise ->
                    if(!exercisesLogs.keySet().contains(exercise.id)) {
                        notUsed.add(exercise)
                    }
                }
                if(notUsed.isEmpty()) {
                    if(orderedUsedRules.size() < Rule.values().size()) {
                        return exerciseRepository.getAll(Rule.getOrdered().get(orderedUsedRules.size())).get(0)
                    } else {
                        return null
                    }
                } else {
                    return notUsed.get(0)
                }
            }

        } else {
            return exerciseRepository.getAll(Rule.getOrdered().get(0)).get(0)
        }

    }

}
