package hr.unipu.MuscleStore.resaurces

import com.brendangoldberg.kotlin_jwt.KtJwtCreator
import com.brendangoldberg.kotlin_jwt.algorithms.HSAlgorithm

import hr.unipu.MuscleStore.Constants
import hr.unipu.MuscleStore.Services.UserServices
import hr.unipu.MuscleStore.domain.User

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import java.time.LocalDateTime

import kotlin.collections.HashMap

@RestController
@RequestMapping("/api/users")
class UsersResaurce {

    @Autowired
    private lateinit var userServices: UserServices

    @PostMapping("/login")
    fun login(@RequestBody userMap: Map<String, Any>) : ResponseEntity<Map<String, String>> {
        val email = userMap["email"] as String
        val password = userMap["password"] as String
        val user : User = userServices.validateUser(email, password)
        val map = HashMap<String, String>()
        map["message"] = "loggedIn successfully"
        return ResponseEntity(generateJWTToken(user), HttpStatus.OK)
    }

    @PostMapping("/register")
    fun  registerUser(@RequestBody userMap: Map<String, String>): ResponseEntity<Map<String, String>> {
        val firstName = userMap["firstName"]
        val lastName = userMap["lastName"]
        val email = userMap["email"]
        val password = userMap["password"]

        val user : User = userServices.registerUser(firstName!!, lastName!!, email!!, password!!)
        val map = HashMap<String, String>()
        map["message"] = "registered successfully"
        return ResponseEntity(generateJWTToken(user), HttpStatus.CREATED)
    }

    private fun generateJWTToken(user: User): Map<String, String> {
        val constants = Constants()
        val algorithm = HSAlgorithm.HS256(constants.API_SECRET_KEY)

        // Define issuedAt LocalDateTime
        val issuedAtLocalDateTime = LocalDateTime.now()

        // Define expiration LocalDateTime (e.g., 1 hour from issuedAt)
        val expirationLocalDateTime = issuedAtLocalDateTime.plusHours(1)

        // Create JWT
        val jwt = KtJwtCreator.init()
            .setIssuedAt(issuedAtLocalDateTime)
            .setExpiresAt(expirationLocalDateTime)
            .addClaim("userId", user.userId ?: 0)
            .addClaim("email", user.email ?: "")
            .addClaim("firstName", user.firstName ?: "")
            .addClaim("lastName", user.lastName ?: "")
            .sign(algorithm)

        return mapOf("token" to jwt)
    }

 @PostMapping("/{userId}/profile-picture")
    fun updateProfilePicture(
        @PathVariable userId: Int,
        @RequestBody profilePictureMap: Map<String, String>
    ): ResponseEntity<Map<String, String>> {
        val profilePicture = profilePictureMap["profilePicture"]
        if (profilePicture.isNullOrBlank()) {
            return ResponseEntity(mapOf("message" to "Profile picture is required"), HttpStatus.BAD_REQUEST)
        }

        userServices.updateProfilePicture(userId, profilePicture)
        return ResponseEntity(mapOf("message" to "Profile picture updated successfully"), HttpStatus.OK)
    }

}