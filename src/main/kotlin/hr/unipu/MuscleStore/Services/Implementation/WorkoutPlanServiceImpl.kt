package hr.unipu.MuscleStore.Services.Implementation

import hr.unipu.MuscleStore.Services.workoutPlanService
import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.domain.WorkoutPlan
import hr.unipu.MuscleStore.entity.Exercise
import hr.unipu.MuscleStore.entity.PlanSection
import hr.unipu.MuscleStore.entity.ActiveWorkout // Ensure ActiveWorkout entity is imported
import hr.unipu.MuscleStore.entity.AddedFromStore
import hr.unipu.MuscleStore.exception.WorkoutPlanCreationException
import hr.unipu.MuscleStore.exception.WorkoutPlanNotFoundException
import hr.unipu.MuscleStore.repositories.*
import hr.unipu.MuscleStore.entity.WorkoutNotation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class WorkoutPlanServiceImpl @Autowired constructor(
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val planSectionRepository: PlanSectionRepository,
    private val exerciseRepository: ExerciseRepository,
    private val activeWorkoutRepository: ActiveWorkoutRepository,
    private val addedFromStoreRepository: AddedFromStoreRepository,
    private val workoutNotationRepository: WorkoutNotationRepository,

) : workoutPlanService {

    @Throws(WorkoutPlanCreationException::class)
    override fun createWorkoutPlan(user: User, title: String, timestamp: LocalDateTime, sections: List<PlanSection>): WorkoutPlan {
        try {
            println("Creating workout plan with title: $title at timestamp: $timestamp")

            val workoutPlan = WorkoutPlan(
                user = user,
                title = title,
                timestamp = timestamp,
                sections = sections.toMutableList()
            )

            val savedWorkoutPlan = workoutPlanRepository.save(workoutPlan)

            sections.forEachIndexed { index, section ->

                section.workoutPlan = savedWorkoutPlan
                val savedSection = planSectionRepository.save(section)

                section.exercises.forEachIndexed { exerciseIndex, exercise ->

                    exercise.planSection = savedSection

                    val existingExercise = exerciseRepository.findByTitleAndPlanSection(exercise.title, savedSection)
                    if (existingExercise == null) {
                        val savedExercise = exerciseRepository.save(exercise)
                        println("Saved exercise with ID: ${savedExercise.exerciseId}")
                    } else {
                        println("Exercise '${exercise.title}' already exists, skipping save.")
                    }
                }
            }

            return savedWorkoutPlan
        } catch (e: Exception) {
            println("Exception occurred while creating workout plan: ${e.message}")
            throw WorkoutPlanCreationException("Failed to create workout plan: ${e.message}")
        }
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun getWorkoutPlansByUserId(userId: Int): List<WorkoutPlan> {
        return workoutPlanRepository.findByUserUserId(userId).takeIf { it.isNotEmpty() }
            ?: throw WorkoutPlanNotFoundException("No workout plans found for user ID $userId")
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun addSectionsToWorkoutPlan(workoutPlanId: Int, sections: List<PlanSection>): WorkoutPlan {
        val workoutPlan = workoutPlanRepository.findById(workoutPlanId)
            .orElseThrow { WorkoutPlanNotFoundException("Workout plan not found with ID $workoutPlanId") }

        sections.forEach { section ->
            section.workoutPlan = workoutPlan
            planSectionRepository.save(section)
        }

        return workoutPlan
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun addExercisesToSection(sectionId: Int, exercises: List<Exercise>): PlanSection {
        val planSection = planSectionRepository.findById(sectionId)
            .orElseThrow { WorkoutPlanNotFoundException("Plan section not found with ID $sectionId") }

        exercises.forEach { exercise ->
            exercise.planSection = planSection
            exerciseRepository.save(exercise)
        }

        return planSection
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun getAllWorkoutPlans(): List<WorkoutPlan> {
        val workoutPlans = workoutPlanRepository.findAll()
        if (workoutPlans.isEmpty()) {
            throw WorkoutPlanNotFoundException("No workout plans found")
        }
        return workoutPlans
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun getWorkoutPlanById(planId: Int): WorkoutPlan {
        return workoutPlanRepository.findById(planId)
            .orElseThrow { WorkoutPlanNotFoundException("Workout plan with ID $planId not found") }
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun deleteWorkoutPlanById(planId: Int) {
        val workoutPlan = workoutPlanRepository.findById(planId)
            .orElseThrow { WorkoutPlanNotFoundException("Workout plan with ID $planId not found") }

        workoutPlanRepository.delete(workoutPlan)
        println("Deleted workout plan with ID: $planId")
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun setActiveWorkout(user: User, workoutPlanId: Int): ActiveWorkout {
        val workoutPlan = workoutPlanRepository.findById(workoutPlanId)
            .orElseThrow { WorkoutPlanNotFoundException("Workout plan with ID $workoutPlanId not found") }

        val existingActiveWorkout = activeWorkoutRepository.findByUser(user)

        return if (existingActiveWorkout != null) {
            existingActiveWorkout.workoutPlan = workoutPlan
            activeWorkoutRepository.save(existingActiveWorkout)
        } else {
            val newActiveWorkout = ActiveWorkout(user = user, workoutPlan = workoutPlan)
            activeWorkoutRepository.save(newActiveWorkout)
        }
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun getActiveWorkoutByUser(user: User): ActiveWorkout {
        return activeWorkoutRepository.findByUser(user)
            ?: throw WorkoutPlanNotFoundException("No active workout found for user with ID ${user.userId}")
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun addWorkoutFromStore(user: User, workoutPlan: WorkoutPlan): AddedFromStore {
        val addedFromStore = AddedFromStore(user = user, workoutPlan = workoutPlan)
        return addedFromStoreRepository.save(addedFromStore)
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun getAllAddedFromStore(): List<AddedFromStore> {
        return addedFromStoreRepository.findAll()
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun getWorkoutPlansByIds(ids: List<Int>): List<WorkoutPlan> {
        return workoutPlanRepository.findAllById(ids)
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun createWorkoutNotation(user: User, activeWorkout: ActiveWorkout, timestamp: LocalDateTime): WorkoutNotation {
        requireNotNull(workoutNotationRepository) { "WorkoutNotationRepository is not injected" }

        try {
            println("Creating workout notation for user ID: ${user.userId} at timestamp: $timestamp")

            val workoutNotation = WorkoutNotation(
                user = user,
                activeWorkout = activeWorkout,
                timestamp = timestamp
            )

            val savedWorkoutNotation = workoutNotationRepository.save(workoutNotation)
            println("Saved workout notation with ID: ${savedWorkoutNotation.id}")

            return savedWorkoutNotation
        } catch (e: Exception) {
            println("Exception occurred while creating workout notation: ${e.message}")
            throw WorkoutPlanNotFoundException("Failed to create workout notation: ${e.message}")
        }
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun getWorkoutNotationsByUserId(userId: Int): List<WorkoutNotation> {
        // Retrieve workout notations by user ID using the repository
        val notations = workoutNotationRepository.findByUserId(userId)

        // Check if the list is empty and throw an exception if necessary
        if (notations.isEmpty()) {
            throw WorkoutPlanNotFoundException("No workout notations found for user with ID $userId")
        }

        return notations
    }

    @Throws(WorkoutPlanNotFoundException::class)
    override fun deleteAllWorkoutNotationsForUser(userId: Int) {
        // Your logic to delete all workout notations by user ID
        workoutNotationRepository.deleteByUserId(userId)
    }

}
