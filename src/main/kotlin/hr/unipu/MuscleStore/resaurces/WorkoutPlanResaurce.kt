package hr.unipu.MuscleStore.resources

import hr.unipu.MuscleStore.Services.workoutPlanService
import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.entity.PlanSection
import hr.unipu.MuscleStore.entity.Exercise
import hr.unipu.MuscleStore.exception.WorkoutPlanCreationException
import hr.unipu.MuscleStore.exception.WorkoutPlanNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/workout-plans")
class WorkoutPlanResource @Autowired constructor(
    private val workoutPlanService: workoutPlanService,
) {

    @PostMapping
    fun createWorkoutPlan(
        @RequestBody request: CreateWorkoutPlanRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {

        return try {
            val user = User(httpRequest.getAttribute("userId") as Int)

            val sections = request.sections.map { sectionDTO ->
                val planSection = PlanSection(
                    sectionId = sectionDTO.sectionId ?: 0,
                    title = sectionDTO.title,
                    exercises = mutableListOf()
                )

                val exercises = sectionDTO.exercises.map { exerciseDTO ->
                    Exercise(
                        exerciseId = exerciseDTO.exerciseId ?: 0,
                        title = exerciseDTO.title,
                        reps = exerciseDTO.reps,
                        planSection = planSection
                    )
                }.toMutableList()

                planSection.exercises.addAll(exercises)
                planSection
            }

            val timestamp = request.timestamp ?: LocalDateTime.now()

            val workoutPlan = workoutPlanService.createWorkoutPlan(
                user = user,
                title = request.title,
                timestamp = timestamp,  // Set the timestamp
                sections = sections
            )

            ResponseEntity(
                mapOf(
                    "message" to "Workout Plan created successfully",
                    "workoutPlanId" to workoutPlan.planId!!,
                    "timestamp" to workoutPlan.timestamp.toString()  // Convert timestamp to String
                ), HttpStatus.CREATED
            )
        } catch (e: WorkoutPlanCreationException) {
            val errorResponse = mapOf("error" to (e.message ?: "Unknown error"))
            ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
        }
    }




    @GetMapping("/user")
    fun getWorkoutPlansForUser(
        httpRequest: HttpServletRequest
    ): ResponseEntity<Any> {
        val userId = httpRequest.getAttribute("userId") as Int

        return try {
            val workoutPlans = workoutPlanService.getWorkoutPlansByUserId(userId)

            val response = workoutPlans.map { plan ->
                WorkoutPlanResponse(
                    planId = plan.planId ?: 0,
                    title = plan.title ?: "",
                    timestamp = plan.timestamp,  // Include timestamp in response
                    user = UserDTO(
                        userId = plan.user?.userId ?: 0,
                        email = plan.user?.email,
                        firstName = plan.user?.firstName,
                        lastName = plan.user?.lastName,
                        profilePicture = plan.user?.profilePicture
                    ),
                    sections = plan.sections.map { section ->
                        PlanSectionDTO(
                            sectionId = section.sectionId ?: 0,
                            title = section.title ?: "",
                            exercises = section.exercises.map { exercise ->
                                ExerciseDTO(
                                    exerciseId = exercise.exerciseId ?: 0,
                                    title = exercise.title ?: "",
                                    reps = exercise.reps ?: ""
                                )
                            }
                        )
                    }
                )
            }
            ResponseEntity.ok(response)
        } catch (e: WorkoutPlanNotFoundException) {
            val errorResponse = mapOf("error" to (e.message ?: "No workout plans found"))
            ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
        }
    }


    @GetMapping
    fun getAllWorkoutPlans(): ResponseEntity<Any> {
        return try {
            val workoutPlans = workoutPlanService.getAllWorkoutPlans()

            val response = workoutPlans.map { plan ->
                WorkoutPlanResponse(
                    planId = plan.planId ?: 0,
                    title = plan.title ?: "",
                    timestamp = plan.timestamp,  // Include timestamp in response
                    user = UserDTO(
                        userId = plan.user?.userId ?: 0,
                        email = plan.user?.email,
                        firstName = plan.user?.firstName,
                        lastName = plan.user?.lastName,
                        profilePicture = plan.user?.profilePicture
                    ),
                    sections = plan.sections.map { section ->
                        PlanSectionDTO(
                            sectionId = section.sectionId ?: 0,
                            title = section.title ?: "",
                            exercises = section.exercises.map { exercise ->
                                ExerciseDTO(
                                    exerciseId = exercise.exerciseId ?: 0,
                                    title = exercise.title ?: "",
                                    reps = exercise.reps ?: ""
                                )
                            }
                        )
                    }
                )
            }
            ResponseEntity.ok(response)
        } catch (e: WorkoutPlanNotFoundException) {
            val errorResponse = mapOf("error" to (e.message ?: "No workout plans found"))
            ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
        }
    }
    @GetMapping("/{planId}")
    fun getWorkoutPlanById(
        @PathVariable planId: Int
    ): ResponseEntity<Any> {
        return try {
            val workoutPlan = workoutPlanService.getWorkoutPlanById(planId)

            val response = WorkoutPlanResponse(
                planId = workoutPlan.planId ?: 0,
                title = workoutPlan.title ?: "",
                timestamp = workoutPlan.timestamp,  // Include timestamp in response
                user = UserDTO(
                    userId = workoutPlan.user?.userId ?: 0,
                    email = workoutPlan.user?.email,
                    firstName = workoutPlan.user?.firstName,
                    lastName = workoutPlan.user?.lastName,
                    profilePicture = workoutPlan.user?.profilePicture
                ),
                sections = workoutPlan.sections.map { section ->
                    PlanSectionDTO(
                        sectionId = section.sectionId ?: 0,
                        title = section.title ?: "",
                        exercises = section.exercises.map { exercise ->
                            ExerciseDTO(
                                exerciseId = exercise.exerciseId ?: 0,
                                title = exercise.title ?: "",
                                reps = exercise.reps ?: ""
                            )
                        }
                    )
                }
            )
            ResponseEntity.ok(response)
        } catch (e: WorkoutPlanNotFoundException) {
            val errorResponse = mapOf("error" to (e.message ?: "Workout plan not found"))
            ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/{planId}")
    fun deleteWorkoutPlanById(
        @PathVariable planId: Int
    ): ResponseEntity<Any> {
        return try {
            workoutPlanService.deleteWorkoutPlanById(planId)
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()  // Return 204 No Content status
        } catch (e: WorkoutPlanNotFoundException) {
            val errorResponse = mapOf("error" to (e.message ?: "Workout plan not found"))
            ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            val errorResponse = mapOf("error" to "Failed to delete workout plan")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }




    // Define a request DTO for creating a workout plan
    data class CreateWorkoutPlanRequest(
        val title: String,
        val sections: List<PlanSectionDTO>,
        val timestamp: LocalDateTime? = null
    )

    data class UserDTO(
        val userId: Int,
        val email: String?,
        val firstName: String?,
        val lastName: String?,
        val profilePicture: String?
    )

    data class ExerciseDTO(
        val exerciseId: Int?,
        val title: String,
        val reps: String
    )

    data class PlanSectionDTO(
        val sectionId: Int?,
        val title: String,
        val exercises: List<ExerciseDTO>
    )

    data class WorkoutPlanResponse(
        val planId: Int?,
        val title: String?,
        val timestamp: LocalDateTime?,
        val user: UserDTO, // Changed from userId to UserDTO
        val sections: List<PlanSectionDTO>
    )
}
