package hr.unipu.MuscleStore.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Workout Plan Not Found")
class WorkoutPlanNotFoundException(message: String) : RuntimeException(message)