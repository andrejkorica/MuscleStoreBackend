package hr.unipu.MuscleStore.domain

class User(
    private var userId: Int? = null,
    private var firstName: String? = null,
    private var lastName: String? = null,
    private var email: String? = null,
    private var password: String? = null
) {
    // Getters
    fun getUserId(): Int? {
        return userId
    }

    fun getFirstName(): String? {
        return firstName
    }

    fun getLastName(): String? {
        return lastName
    }

    fun getEmail(): String? {
        return email
    }

    fun getPassword(): String? {
        return password
    }

    // Setters
    fun setUserId(userId: Int?) {
        this.userId = userId
    }

    fun setFirstName(firstName: String?) {
        this.firstName = firstName
    }

    fun setLastName(lastName: String?) {
        this.lastName = lastName
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun setPassword(password: String?) {
        this.password = password
    }
}
