package hr.unipu.MuscleStore.domain

import jakarta.persistence.*

@Entity
@Table(name = "et_users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var userId: Int? = null,

    @Column(name = "first_name")
    var firstName: String? = null,

    @Column(name = "last_name")
    var lastName: String? = null,

    @Column(name = "email", unique = true)
    var email: String? = null,

    @Column(name = "password")
    var password: String? = null,

    @Column(name = "profile_picture")
    var profilePicture: String? = null

) {
    // Default no-arg constructor
    constructor() : this(null, null, null, null, null, null)

}
