package hr.unipu.MuscleStore.repositories

import hr.unipu.MuscleStore.entity.PlanSection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlanSectionRepository : JpaRepository<PlanSection, Int> {
    fun findByWorkoutPlanPlanId(planId: Int): List<PlanSection>
}
