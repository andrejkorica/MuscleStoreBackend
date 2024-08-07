package hr.unipu.MuscleStore.repositories

import hr.unipu.MuscleStore.entity.Exercise
import hr.unipu.MuscleStore.entity.PlanSection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExerciseRepository : JpaRepository<Exercise, Int> {
    fun findByPlanSectionSectionId(sectionId: Int): List<Exercise>

     fun findByTitleAndPlanSection(title: String?, planSection: PlanSection): Exercise?
}
