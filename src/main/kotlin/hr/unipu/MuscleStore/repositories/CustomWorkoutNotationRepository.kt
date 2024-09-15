package hr.unipu.MuscleStore.repositories

import hr.unipu.MuscleStore.entity.WorkoutNotation

interface CustomWorkoutNotationRepository {
    fun findByUserId(userId: Int): List<WorkoutNotation>
    fun deleteByUserId(userId: Int): Int
}
