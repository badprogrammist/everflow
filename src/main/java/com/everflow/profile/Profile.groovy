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

    def reset() {
        Set<Long> ids = template.opsForSet().members(EXERCISES)
        template.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi()
                ids.each { id ->
                    operations.delete(EXERCISE_DATA(id))
                }
                operations.delete(EXERCISES)
                Rule.values().each {rule ->
                    operations.delete(RULES_STATS(rule.code))
                }
                return operations.exec()
            }
        })

    }

    def update(Result result) {
        Map<Rule, RuleStats> rulesStats = [:]
        result.rules.each { rule, status ->
            RuleStats ruleStats = getRuleStats(rule)
            rulesStats[rule] = ruleStats
            if (status) {
                rulesStats[rule].succeed += 1
            } else {
                rulesStats[rule].failure += 1
            }
        }
        template.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi()
                rulesStats.each { rule, ruleStats ->
                    operations.opsForHash().putAll(
                            RULES_STATS(rule.code),
                            [
                                    'succeed': ruleStats.succeed as String,
                                    'failure': ruleStats.failure as String
                            ]
                    )
                }
                return operations.exec()
            }
        })

        Map<Rule, Double> progresses = calculateProgress(result)
        progresses.each { rule, progress ->
            rulesStats[rule].progress = progress
        }
        ExerciseLog exerciseLog = getExerciseTs(result.exercise.id)
        exerciseLog.ts = new Date().time
        if (result.success) {
            exerciseLog.day = 2 * exerciseLog.day + 1
        } else {
            exerciseLog.day = 1
        }
        exerciseLog.day = 2 * exerciseLog.day + 1
        template.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi()
                rulesStats.each { rule, ruleStats ->
                    operations.opsForHash().putAll(
                            RULES_STATS(rule.code),
                            [
                                    'progress': ruleStats.progress as String
                            ]
                    )
                }
                operations.opsForSet().add(EXERCISES, result.exercise.id as String)
                operations.opsForHash().putAll(
                        EXERCISE_DATA(result.exercise.id),
                        [
                                'ts' : exerciseLog.ts as String,
                                'day': exerciseLog.day as String
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
//            if (ruleThreshold > THRESHOLD) {
            progress = (100.0 / (ruleStats.succeed + ruleStats.failure)) * ruleStats.succeed
//            }
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
        ids.eachWithIndex {id, i ->
            results[id as Long] = new ExerciseLog(ts:txResults[i]['ts']  as Long, day:txResults[i]['day'] as Long)
        }
        return results
    }

    ExerciseLog getExerciseTs(long id) {
        Map map = template.opsForHash().entries(EXERCISE_DATA(id))
        if (map != null) {
            return new ExerciseLog(ts: map['ts'] == null ? 0 : map['ts'] as Long, day: map['day'] == null ? 0 : map['day'] as Long)
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
            progress[rule] = txResults[i] == null ? 0.0 : txResults[i] as Double
        }
        return progress
    }

    RuleStats getRuleStats(Rule rule) {
        Map map = template.opsForHash().entries(RULES_STATS(rule.code))
        if (map != null) {
            return new RuleStats(
                    progress: map['progress'] == null ? 0 : map['progress'] as Double,
                    succeed: map['succeed'] == null ? 0 : map['succeed']  as Long,
                    failure: map['failure'] == null ? 0 : map['failure'] as Long
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
