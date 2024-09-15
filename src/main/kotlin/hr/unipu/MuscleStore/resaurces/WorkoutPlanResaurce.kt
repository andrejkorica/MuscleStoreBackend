package hr.unipu.MuscleStore.resources

import hr.unipu.MuscleStore.Services.Implementation.WorkoutPlanServiceImpl
import hr.unipu.MuscleStore.Services.workoutPlanService
import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.domain.WorkoutPlan
import hr.unipu.MuscleStore.entity.PlanSection
import hr.unipu.MuscleStore.entity.Exercise
import hr.unipu.MuscleStore.exception.WorkoutPlanCreationException
import hr.unipu.MuscleStore.exception.WorkoutPlanNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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

    @PostMapping("/{workoutPlanId}/set-active")
    fun setActiveWorkoutPlan(
        httpRequest: HttpServletRequest,
        @PathVariable workoutPlanId: Int
    ): ResponseEntity<Map<String, Any>> {
        return try {
            val userId = httpRequest.getAttribute("userId") as Int
            val user = User(userId)

            // Set or update the active workout plan
            val activeWorkout = workoutPlanService.setActiveWorkout(user, workoutPlanId)

            // Provide default values if nullable
            val response: Map<String, Any> = mapOf(
                "message" to "Active workout plan set successfully",
                "activeWorkoutPlanId" to (activeWorkout.id ?: -1), // Default value if id is null
                "userId" to (activeWorkout.user?.userId ?: -1), // Default value if userId is null
                "workoutPlanId" to (activeWorkout.workoutPlan?.planId ?: -1) // Default value if planId is null
            )

            ResponseEntity.ok(response)
        } catch (e: WorkoutPlanNotFoundException) {
            val errorResponse: Map<String, Any> = mapOf(
                "error" to (e.message ?: "Workout plan not found")
            )
            ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            val errorResponse: Map<String, Any> = mapOf(
                "error" to "Failed to set active workout plan"
            )
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/active")
    fun getActiveWorkoutPlan(httpRequest: HttpServletRequest): ResponseEntity<Any> {
        val userId = httpRequest.getAttribute("userId") as Int

        return try {
            val user = User(userId)
            val activeWorkout = workoutPlanService.getActiveWorkoutByUser(user)

            val response = mapOf(
                "activeWorkoutPlanId" to (activeWorkout.id ?: -1),
                "userId" to (activeWorkout.user?.userId ?: -1),
                "workoutPlanId" to (activeWorkout.workoutPlan?.planId ?: -1)
            )

            ResponseEntity.ok(response)
        } catch (e: WorkoutPlanNotFoundException) {
            val errorResponse = mapOf("error" to (e.message ?: "Active workout not found"))
            ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            val errorResponse = mapOf("error" to "Failed to retrieve active workout plan")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping("/add-from-store")
    fun addWorkoutFromStore(
        httpRequest: HttpServletRequest,
        @RequestBody request: AddWorkoutFromStoreRequest
    ): ResponseEntity<Map<String, Any>> {
        return try {
            // Extract user ID from the HttpServletRequest
            val userId = httpRequest.getAttribute("userId") as Int
            val user = User(userId)  // Create a User instance with the extracted userId

            // Get the workout plan
            val workoutPlan = workoutPlanService.getWorkoutPlanById(request.workoutPlanId)

            // Add the workout plan to the store
            val addedFromStore = workoutPlanService.addWorkoutFromStore(user, workoutPlan)

            val response = mapOf(
                "message" to "Workout added to store successfully",
                "addedFromStoreId" to (addedFromStore.id ?: -1)
            )

            ResponseEntity.ok(response)
        } catch (e: WorkoutPlanNotFoundException) {
            val errorResponse = mapOf("error" to (e.message ?: "Workout plan not found"))
            ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            val errorResponse = mapOf("error" to "Failed to add workout to store")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/add-from-store")
    fun getAllAddedFromStore(httpRequest: HttpServletRequest): ResponseEntity<Any> {
        return try {
            // Retrieve user ID from the HTTP request
            val userId = httpRequest.getAttribute("userId") as Int

            // Fetch all records from the add-from-store table
            val addedFromStoreRecords = workoutPlanService.getAllAddedFromStore()

            // Filter records for the specified user ID
            val filteredRecords = addedFromStoreRecords.filter { record ->
                record.user?.userId == userId
            }

            // Map filtered records to a response format with full WorkoutPlan details
            val response = filteredRecords.map { record ->
                AddFromStoreResponse(
                    id = record.id ?: -1,
                    userId = record.user?.userId ?: -1,
                    workoutPlanId = record.workoutPlan?.planId ?: -1, // Explicitly set the workoutPlanId
                    workoutPlan = record.workoutPlan // Include the full WorkoutPlan object
                )
            }

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            val errorResponse = mapOf("error" to "Failed to retrieve records from store")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }


    @GetMapping("/by-ids")
    fun getWorkoutPlansByIds(@RequestBody idsRequest: IdsRequest): ResponseEntity<List<WorkoutPlan>> {
        val workoutPlans = workoutPlanService.getWorkoutPlansByIds(idsRequest.ids)
        return ResponseEntity.ok(workoutPlans)
    }

    @PostMapping("/workout-notations")
    fun createWorkoutNotation(
        @RequestBody request: CreateWorkoutNotationRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        return try {
            val userId = httpRequest.getAttribute("userId") as Int
            val user = User(userId)
            val activeWorkout = workoutPlanService.getActiveWorkoutByUser(user)

            val timestamp = request.timestamp ?: LocalDateTime.now()

            val workoutNotation = workoutPlanService.createWorkoutNotation(
                user = user,
                activeWorkout = activeWorkout,
                timestamp = timestamp
            )

            val response: Map<String, Any> = mapOf(
                "message" to "Workout notation created successfully",
                "workoutNotationId" to (workoutNotation.id ?: -1),
                "userId" to (user.userId ?: -1),
                "activeWorkoutId" to (activeWorkout.id ?: -1),
                "timestamp" to timestamp.toString()
            )

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            val errorResponse = mapOf("error" to "Failed to create workout notation")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/workout-notations")
    fun getWorkoutNotationsForUser(
        httpRequest: HttpServletRequest
    ): ResponseEntity<Any> {
        return try {
            // Retrieve user ID from the HTTP request
            val userId = httpRequest.getAttribute("userId") as Int

            // Fetch workout notations by user ID
            val workoutNotations = workoutPlanService.getWorkoutNotationsByUserId(userId)

            // Map the notations to a response format
            val response = workoutNotations.map { notation ->
                WorkoutNotationResponse(
                    id = notation.id ?: -1,
                    timestamp = notation.timestamp,
                    activeWorkoutId = notation.activeWorkout?.id ?: -1,
                    userId = notation.user?.userId ?: -1
                )
            }

            ResponseEntity.ok(response)
        } catch (e: WorkoutPlanNotFoundException) {
            val errorResponse = mapOf("error" to (e.message ?: "Failed to retrieve workout notations"))
            ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            val errorResponse = mapOf("error" to "Failed to retrieve workout notations")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/workout-notations")
    fun deleteAllWorkoutNotationsForUser(
        httpRequest: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        return try {
            // Retrieve user ID from the HTTP request
            val userId = httpRequest.getAttribute("userId") as Int

            // Call the service to delete all workout notations for the user
            workoutPlanService.deleteAllWorkoutNotationsForUser(userId)

            // Return a successful response
            val response = mapOf("message" to "All workout notations deleted successfully")
            ResponseEntity.ok(response)
        } catch (e: WorkoutPlanNotFoundException) {
            val errorResponse = mapOf("error" to (e.message ?: "No workout notations found for the user"))
            ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            val errorResponse = mapOf("error" to "Failed to delete workout notations")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }


    data class WorkoutNotationResponse(
        val id: Long,
        val timestamp: LocalDateTime?,
        val activeWorkoutId: Int,
        val userId: Int
    )

    data class CreateWorkoutNotationRequest(
        val timestamp: LocalDateTime
    )

    // Define a request DTO for creating a workout plan
    data class CreateWorkoutPlanRequest(
        val title: String,
        val sections: List<PlanSectionDTO>,
        val timestamp: LocalDateTime? = null
    )

    data class IdsRequest(val ids: List<Int>)

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

    data class SetActiveWorkoutResponse(
        val message: String,
        val activeWorkoutPlanId: Int,
        val userId: Int,
        val workoutPlanId: Int
    )
    data class AddWorkoutFromStoreRequest(
        val userId: Int,
        val workoutPlanId: Int
    )

    data class AddFromStoreResponse(
        val id: Int,
        val userId: Int,
        val workoutPlanId: Int,
        val workoutPlan: WorkoutPlan? // Include the full WorkoutPlan object
    )

}
