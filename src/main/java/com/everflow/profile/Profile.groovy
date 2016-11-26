package com.everflow.profile

import com.everflow.checker.Result
import com.everflow.exercise.Exercise
import com.everflow.exercise.ExerciseRepository
import com.everflow.grammar.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.dao.DataAccessException
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.SessionCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

/**
 * @author Ildar Gafarov (ildar@skybonds.com)
 */
@Service
class Profile {

    static Double THRESHOLD = 35

    private static def RULES_STATS = { ruleCode -> "profile:rules:${ruleCode}" as String }
    private static def EXERCISE_DATA = { id -> "profile:exercise:${id}" as String }
    private static def EXERCISES = 'profile:exercises'

    @Autowired
    @Qualifier("dataRedisTemplate")
    StringRedisTemplate template

    @Autowired
    ExerciseRepository exerciseRepository

    def update(Result result) {
        Map<Rule, Double> progresses = calculateProgress(result)
        Map<Rule, RuleStats> rulesStats = [:]
        progresses.each { rule, progress ->
            RuleStats ruleStats = getRuleStats(rule)
            ruleStats.progress = progress
            rulesStats[rule] = ruleStats
        }
        result.rules.each { rule, status ->
            if (status) {
                rulesStats[rule].succeed += 1
            } else {
                rulesStats[rule].failure += 1
            }
        }
        ExerciseLog exerciseLog = getExerciseTs(result.exercise.id)
        exerciseLog.ts = new Date().time
        exerciseLog.day = 2 * exerciseLog.day + 1
        template.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi()
                rulesStats.each { rule, ruleStats ->
                    operations.opsForHash().putAll(
                            RULES_STATS(rule.code),
                            [
                                    'progress': ruleStats.progress,
                                    'succeed' : ruleStats.succeed,
                                    'failure' : ruleStats.failure
                            ]
                    )
                }
                operations.opsForSet().add(EXERCISES, result.exercise.id)
                operations.opsForHash().putAll(
                        EXERCISE_DATA(result.exercise.id),
                        [
                                'ts' : exerciseLog.ts,
                                'day': exerciseLog.day
                        ]

                )
                return operations.exec()
            }
        })
    }

    Map<Rule, Double> calculateProgress(Result result) {
        Map<Rule, Double> results = [:]
        result.exercise.rules.each { exerciseRule ->
            List<Exercise> allExercises = exerciseRepository.getAll(exerciseRule.rule)
            RuleStats ruleStats = getRuleStats(exerciseRule.rule)

            double ruleThreshold = (100.0 / allExercises.size()) * (ruleStats.succeed + ruleStats.failure)
            double progress = 0.0
            if (ruleThreshold > THRESHOLD) {
                progress = (100.0 / (ruleStats.succeed + ruleStats.failure)) * ruleStats.succeed
            }
            results[exerciseRule.rule] = progress
        }
        return results
    }

    Map<Long, ExerciseLog> getExercisesTs() {
        Map<Long, ExerciseLog> results = [:]
        Set<Long> ids = template.opsForSet().members(EXERCISES)
        List<Object> txResults = template.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi()
                ids.each { id ->
                    operations.opsForHash().entries(EXERCISE_DATA(id))
                }
                return operations.exec()
            }
        })
        ids.eachWithIndex { long id, int i ->
            results[id] = new ExerciseLog(ts: txResults[i]['ts'], day: txResults[i]['day'])
        }
        return results
    }

    ExerciseLog getExerciseTs(long id) {
        Map map = template.opsForHash().entries(EXERCISE_DATA(id))
        if (map != null && map.size() == 2) {
            return new ExerciseLog(ts: map['ts'], day: map['day'])
        } else {
            return new ExerciseLog(ts: null, day: 0)
        }
    }

    Map<Rule, Double> getRulesProgress() {
        Map<Rule, Double> progress = [:]
        List<Object> txResults = template.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi()
                Rule.values().each { rule ->
                    operations.opsForHash().get(RULES_STATS(rule.code), 'progress')
                }
                return operations.exec()
            }
        })
        Rule.values().eachWithIndex { rule, i ->
            progress[rule] = txResults[i]
        }
        return progress
    }

    RuleStats getRuleStats(Rule rule) {
        Map map = template.opsForHash().entries(RULES_STATS(rule.code))
        if (map != null && map.size() == 3) {
            return new RuleStats(
                    progress: map['progress'],
                    succeed: map['succeed'],
                    failure: map['failure']
            )
        } else {
            return new RuleStats(
                    progress: 0,
                    succeed: 0,
                    failure: 0
            )
        }
    }


}
