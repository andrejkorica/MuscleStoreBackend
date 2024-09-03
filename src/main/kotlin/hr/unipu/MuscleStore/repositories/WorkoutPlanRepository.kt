package hr.unipu.MuscleStore.repositories

import hr.unipu.MuscleStore.domain.WorkoutPlan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface WorkoutPlanRepository : JpaRepository<WorkoutPlan, Int> {

    // Find all workout plans by user ID
    fun findByUserUserId(userId: Int): List<WorkoutPlan>

    // Find a workout plan by its ID
    override fun findById(id: Int): Optional<WorkoutPlan>

    // Delete a workout plan by its ID
    override fun deleteById(id: Int)
}
