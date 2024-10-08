package hr.unipu.MuscleStore.Services.Implementation

import hr.unipu.MuscleStore.Services.UserServices
import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.exception.EtAuthException
import hr.unipu.MuscleStore.repositories.UserRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.regex.Pattern

@Service
@Transactional
class UserServiceImpl : UserServices {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun validateUser(email: String, password: String): User {
        val emailLower = email.lowercase(Locale.getDefault())
        return userRepository.findByEmailAndPassword(emailLower, password)
    }

    override fun registerUser(firstName: String, lastName: String, email: String, password: String): User {
        var modifiedEmail = email
        val pattern = Pattern.compile("^(.+)@(.+)$")
        modifiedEmail = modifiedEmail.lowercase(Locale.getDefault())
        if (!pattern.matcher(modifiedEmail).matches()) {
            throw EtAuthException("Invalid email format")
        }
        val count: Int = userRepository.getCountByEmail(modifiedEmail)
        if (count > 0) {
            throw EtAuthException("User already exists")
        }

        val userId = userRepository.create(firstName, lastName, email, password)
        return userRepository.findById(userId)
    }

    override fun updateProfilePicture(userId: Int, profilePicture: String): User {

        val user = userRepository.findById(userId)
        userRepository.updateProfilePicture(userId, profilePicture)
        user.profilePicture = profilePicture

        return user
    }

    @Transactional
    override fun deleteUserAndAssociatedData(userId: Int) {
        // Delete user data
        userRepository.deleteById(userId)

    }
}