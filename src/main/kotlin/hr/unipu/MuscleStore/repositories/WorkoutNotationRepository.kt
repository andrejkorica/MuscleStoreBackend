package hr.unipu.MuscleStore.repositories

import hr.unipu.MuscleStore.entity.WorkoutNotation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutNotationRepository : JpaRepository<WorkoutNotation, Long>, CustomWorkoutNotationRepository
