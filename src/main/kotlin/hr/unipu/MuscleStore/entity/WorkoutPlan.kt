package hr.unipu.MuscleStore.domain

import hr.unipu.MuscleStore.entity.PlanSection
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "workout_plans")
class WorkoutPlan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    var planId: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null,

    @Column(name = "title")
    var title: String? = null,

    @Column(name = "timestamp")
    var timestamp: LocalDateTime? = null,

    @OneToMany(mappedBy = "workoutPlan", cascade = [CascadeType.MERGE], orphanRemoval = true)
    var sections: MutableList<PlanSection> = mutableListOf()
) {
    // Default no-arg constructor
    constructor() : this(null, null, null, null, mutableListOf() )
}
