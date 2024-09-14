package hr.unipu.MuscleStore.repositories.Implementation

import hr.unipu.MuscleStore.entity.WorkoutNotation
import hr.unipu.MuscleStore.repositories.CustomWorkoutNotationRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class WorkoutNotationRepositoryImpl : CustomWorkoutNotationRepository {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun findByUserId(userId: Int): List<WorkoutNotation> {
        val query = """
            SELECT wn FROM WorkoutNotation wn
            WHERE wn.user.userId = :userId
        """
        return entityManager.createQuery(query, WorkoutNotation::class.java)
            .setParameter("userId", userId)
            .resultList
    }
}
