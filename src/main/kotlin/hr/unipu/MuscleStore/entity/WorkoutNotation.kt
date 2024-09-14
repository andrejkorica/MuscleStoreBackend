package hr.unipu.MuscleStore.entity

import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.entity.ActiveWorkout
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "workout_notations")
data class WorkoutNotation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)  // Change to ManyToOne
    @JoinColumn(name = "active_workout_id")
    val activeWorkout: ActiveWorkout? = null,

    @Column(name = "timestamp")
    val timestamp: LocalDateTime? = null
) {
    constructor() : this(null, null, null, null)
}

