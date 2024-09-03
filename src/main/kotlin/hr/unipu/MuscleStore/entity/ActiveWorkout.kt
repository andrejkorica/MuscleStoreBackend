package hr.unipu.MuscleStore.entity

import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.domain.WorkoutPlan
import jakarta.persistence.*

@Entity
@Table(name = "active_workouts")
data class ActiveWorkout(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @OneToOne
    @JoinColumn(name = "user_id")  // Ensure this matches the column in the User table
    val user: User? = null,

    @OneToOne
    @JoinColumn(name = "plan_id")  // Ensure this matches the column in the WorkoutPlan table
    var workoutPlan: WorkoutPlan? = null
) {
    constructor() : this(null, null, null)
}
