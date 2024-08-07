package hr.unipu.MuscleStore.entity
import jakarta.persistence.*

@Entity
@Table(name = "exercises")
class Exercise(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    var exerciseId: Int? = null,

    @Column(name = "title")
    var title: String? = null,

    @Column(name = "reps")
    var reps: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    var planSection: PlanSection? = null
) {
    // Default no-arg constructor
    constructor() : this(null, null, null, null)
}
