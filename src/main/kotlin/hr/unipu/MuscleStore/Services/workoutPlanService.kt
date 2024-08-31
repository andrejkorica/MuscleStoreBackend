package hr.unipu.MuscleStore.Services

import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.domain.WorkoutPlan
import hr.unipu.MuscleStore.entity.Exercise
import hr.unipu.MuscleStore.entity.PlanSection
import hr.unipu.MuscleStore.exception.WorkoutPlanCreationException
import hr.unipu.MuscleStore.exception.WorkoutPlanNotFoundException
import java.time.LocalDateTime

interface workoutPlanService {
    @Throws(WorkoutPlanCreationException::class)
    fun createWorkoutPlan(user: User, title: String, timestamp: LocalDateTime, sections: List<PlanSection>): WorkoutPlan

    @Throws(WorkoutPlanNotFoundException::class)
    fun getWorkoutPlansByUserId(userId: Int): List<WorkoutPlan>

    @Throws(WorkoutPlanNotFoundException::class)
    fun addSectionsToWorkoutPlan(workoutPlanId: Int, sections: List<PlanSection>): WorkoutPlan

    @Throws(WorkoutPlanNotFoundException::class)
    fun addExercisesToSection(sectionId: Int, exercises: List<Exercise>): PlanSection

    @Throws(WorkoutPlanNotFoundException::class)
    fun getAllWorkoutPlans(): List<WorkoutPlan>

    @Throws(WorkoutPlanNotFoundException::class)
    fun getWorkoutPlanById(planId: Int): WorkoutPlan
}
