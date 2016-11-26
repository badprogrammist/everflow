package com.everflow.api

import com.everflow.checker.Checker
import com.everflow.checker.Entered
import com.everflow.checker.Result
import com.everflow.grammar.Rule
import com.everflow.profile.Profile
import com.everflow.schedule.Schedule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * @author Ildar Gafarov on 16.05.16.
 */
@RestController
@RequestMapping("/api")
class ExerciseController {

    @Autowired
    Schedule schedule

    @Autowired
    Checker checker

    @Autowired
    Profile profile

    @RequestMapping(method = RequestMethod.GET, value = "/next")
    def getNextExercise() {
        def exercise = schedule.next()
        return ["id": exercise.id, "translate": exercise.translate]
    }

    @RequestMapping(method = RequestMethod.POST, value = "/check/{id}", consumes = "application/json")
    def check(@PathVariable Long id, @RequestBody Map<String, String> entered) {
        Result result = checker.check(new Entered(id: id, text:entered['text']))
        profile.update(result)
        return ['result': result.isSuccess(), 'correct': result.exercise.sentence]
    }

    @RequestMapping(method = RequestMethod.GET, value = "/stats")
    def getStats() {
        Map<Rule, Double> progress = profile.getRulesProgress()
        def result = []
        progress.each {r, p ->
            result.add(['rule': r.title, 'progress': p])
        }
        return result
    }

    @RequestMapping(method = RequestMethod.GET, value = "/reset")
    def getReset() {
        profile.reset()
        return "reseted"
    }

}
