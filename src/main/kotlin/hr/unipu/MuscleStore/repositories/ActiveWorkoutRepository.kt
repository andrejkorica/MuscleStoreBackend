package hr.unipu.MuscleStore.repositories

import hr.unipu.MuscleStore.entity.ActiveWorkout
import hr.unipu.MuscleStore.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ActiveWorkoutRepository : JpaRepository<ActiveWorkout, Int> {

    // Find the active workout associated with a user
    fun findByUser(user: User): ActiveWorkout?

}
