package hr.unipu.MuscleStore.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.domain.WorkoutPlan
import jakarta.persistence.*

@Entity
@Table(name = "added_from_store")
data class AddedFromStore(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")  // Reference to the User
    val user: User? = null,

    @ManyToOne
    @JoinColumn(name = "plan_id")  // Reference to the WorkoutPlan
    val workoutPlan: WorkoutPlan? = null
) {
    constructor() : this(null, null, null)
}
