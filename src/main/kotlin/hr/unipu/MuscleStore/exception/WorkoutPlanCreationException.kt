package hr.unipu.MuscleStore.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Error Creating Workout Plan")
class WorkoutPlanCreationException(message: String) : RuntimeException(message)