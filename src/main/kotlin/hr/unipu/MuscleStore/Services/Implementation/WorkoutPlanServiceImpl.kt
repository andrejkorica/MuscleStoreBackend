package hr.unipu.MuscleStore.Services.Implementation

import hr.unipu.MuscleStore.Services.workoutPlanService
import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.domain.WorkoutPlan
import hr.unipu.MuscleStore.entity.Exercise
import hr.unipu.MuscleStore.entity.PlanSection
import hr.unipu.MuscleStore.exception.WorkoutPlanCreationException
import hr.unipu.MuscleStore.exception.WorkoutPlanNotFoundException
import hr.unipu.MuscleStore.repositories.WorkoutPlanRepository
import hr.unipu.MuscleStore.repositories.PlanSectionRepository
import hr.unipu.MuscleStore.repositories.ExerciseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WorkoutPlanServiceImpl @Autowired constructor(
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val planSectionRepository: PlanSectionRepository,
    private val exerciseRepository: ExerciseRepository
) : workoutPlanService {

    @Throws(WorkoutPlanCreationException::class)
    override fun createWorkoutPlan(user: User, title: String, sections: List<PlanSection>): WorkoutPlan {
        try {
            println("Creating workout plan with title: $title ")

            val workoutPlan = WorkoutPlan(
                user = user,
                title = title
            )

            // Save WorkoutPlan first
            println("Saving workout plan...")
            val savedWorkoutPlan = workoutPlanRepository.save(workoutPlan)
            println("Saved workout plan with ID: ${savedWorkoutPlan.planId}")

            // Save PlanSections
            sections.forEachIndexed { index, section ->
                println("Processing section ${index + 1}: ${section.title}")

                section.workoutPlan = savedWorkoutPlan
                val savedSection = planSectionRepository.save(section)
                println("Saved section with ID: ${savedSection.sectionId}")

                // Save Exercises
                section.exercises.forEachIndexed { exerciseIndex, exercise ->
                    println("Processing exercise ${exerciseIndex + 1}: ${exercise.title}")

                    exercise.planSection = savedSection

                    // Check for existing exercise to avoid duplication
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
}
