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

    @OneToMany(mappedBy = "workoutPlan", cascade = [CascadeType.ALL], orphanRemoval = true)
    var sections: MutableList<PlanSection> = mutableListOf()
) {
    // Default no-arg constructor
    constructor() : this(null, null, null, mutableListOf())
}
