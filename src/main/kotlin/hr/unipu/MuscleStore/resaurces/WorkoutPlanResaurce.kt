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

@RestController
@RequestMapping("/api/workout-plans")
class WorkoutPlanResource @Autowired constructor(
    private val workoutPlanService: workoutPlanService,
) {

    private val logger: Logger = LoggerFactory.getLogger(WorkoutPlanResource::class.java)

    @PostMapping
    fun createWorkoutPlan(
        @RequestBody request: CreateWorkoutPlanRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        logger.debug("Received request to create workout plan: $request")

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

            sections.forEach { section ->
                logger.debug("Section: ${section.title}, Exercises: ${section.exercises.map { it.title }}")
            }

            val workoutPlan = workoutPlanService.createWorkoutPlan(
                user = user,
                title = request.title,
                sections = sections
            )

            ResponseEntity(
                mapOf(
                    "message" to "Workout Plan created successfully",
                    "workoutPlanId" to workoutPlan.planId!!
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

    // Define a request DTO for creating a workout plan
    data class CreateWorkoutPlanRequest(
        val title: String,
        val sections: List<PlanSectionDTO>
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
        val user: UserDTO, // Changed from userId to UserDTO
        val sections: List<PlanSectionDTO>
    )
}
