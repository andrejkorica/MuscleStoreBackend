package hr.unipu.MuscleStore.repositories

import hr.unipu.MuscleStore.domain.WorkoutPlan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutPlanRepository: JpaRepository<WorkoutPlan, Int> {
    // Custom query method to find workout plans by user ID
    fun findByUserUserId(userId: Int): List<WorkoutPlan>
}
