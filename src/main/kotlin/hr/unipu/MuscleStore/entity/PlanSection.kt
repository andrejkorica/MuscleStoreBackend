package hr.unipu.MuscleStore.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import hr.unipu.MuscleStore.domain.WorkoutPlan
import jakarta.persistence.*

@Entity
@Table(name = "plan_sections")
class PlanSection(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    var sectionId: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnore  // Ignore serialization of workoutPlan from AddedFromStore side
    var workoutPlan: WorkoutPlan? = null,

    @Column(name = "title")
    var title: String? = null,

    @OneToMany(mappedBy = "planSection", cascade = [CascadeType.MERGE], orphanRemoval = true)
    var exercises: MutableList<Exercise> = mutableListOf()
) {
    // Default no-arg constructor
    constructor() : this(null, null, null, mutableListOf())
}
