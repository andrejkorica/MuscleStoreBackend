package hr.unipu.MuscleStore.resources

import hr.unipu.MuscleStore.Services.workoutPlanService
import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.domain.WorkoutPlan
import hr.unipu.MuscleStore.entity.PlanSection
import hr.unipu.MuscleStore.entity.Exercise
import hr.unipu.MuscleStore.exception.WorkoutPlanCreationException
import hr.unipu.MuscleStore.exception.WorkoutPlanNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/api/workout-plans")
class WorkoutPlanResource @Autowired constructor(
    private val workoutPlanService: workoutPlanService
) {

    // Create a Logger instance
    private val logger: Logger = LoggerFactory.getLogger(WorkoutPlanResource::class.java)

    @PostMapping
    fun createWorkoutPlan(
        @RequestBody request: CreateWorkoutPlanRequest
    ): ResponseEntity<Map<String, Any>> {
        logger.debug("Received request to create workout plan: $request") // Log the entire request

        return try {
            // Create or retrieve the User entity
            val user = User(request.userId)

            // Map DTOs to entities
            val sections = request.sections.map { sectionDTO ->
                // Create PlanSection
                val planSection = PlanSection(
                    sectionId = sectionDTO.sectionId ?: 0, // Default value for new sections
                    title = sectionDTO.title,
                    exercises = mutableListOf() // Initialize with an empty list
                )

                // Create and associate Exercises with the PlanSection
                val exercises = sectionDTO.exercises.map { exerciseDTO ->
                    Exercise(
                        exerciseId = exerciseDTO.exerciseId ?: 0, // Default value for new exercises
                        title = exerciseDTO.title,
                        reps = exerciseDTO.reps,
                        planSection = planSection // Set the relationship
                    )
                }.toMutableList() // Ensure MutableList for Hibernate

                // Set the exercises to the PlanSection
                planSection.exercises.addAll(exercises)

                planSection
            }

            // Log sections and their exercises
            sections.forEach { section ->
                logger.debug("Section: ${section.title}, Exercises: ${section.exercises.map { it.title }}")
            }

            // Create workout plan
            val workoutPlan = workoutPlanService.createWorkoutPlan(
                user = user,
                title = request.title,
                sections = sections
            )

            // Return success response
            ResponseEntity(
                mapOf(
                    "message" to "Workout Plan created successfully",
                    "workoutPlanId" to workoutPlan.planId!!
                ), HttpStatus.CREATED
            )
        } catch (e: WorkoutPlanCreationException) {
            // Handle the exception and return a proper response
            val errorResponse = mapOf("error" to (e.message ?: "Unknown error"))
            ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/user/{userId}")
    fun getWorkoutPlansByUserId(
        @PathVariable userId: Int
    ): ResponseEntity<Any> {
        logger.debug("Received request to get workout plans for userId: $userId") // Log the userId

        return try {
            val workoutPlans = workoutPlanService.getWorkoutPlansByUserId(userId)

            // Map to DTOs
            val response = workoutPlans.map { plan ->
                WorkoutPlanResponse(
                    planId = plan.planId ?: 0,  // Provide default value if null
                    title = plan.title ?: "",  // Provide default value if null
                    userId = userId,  // Non-nullable
                    sections = plan.sections.map { section ->
                        PlanSectionDTO(
                            sectionId = section.sectionId ?: 0,  // Provide default value if null
                            title = section.title ?: "",  // Provide default value if null
                            exercises = section.exercises.map { exercise ->
                                ExerciseDTO(
                                    exerciseId = exercise.exerciseId ?: 0,  // Provide default value if null
                                    title = exercise.title ?: "",  // Provide default value if null
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
        val userId: Int,
        val title: String,
        val sections: List<PlanSectionDTO>
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
        val userId: Int,
        val sections: List<PlanSectionDTO>
    )
}
