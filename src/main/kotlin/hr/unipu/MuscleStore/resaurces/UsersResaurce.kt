package hr.unipu.MuscleStore.resources

import com.brendangoldberg.kotlin_jwt.KtJwtCreator
import com.brendangoldberg.kotlin_jwt.algorithms.HSAlgorithm

import hr.unipu.MuscleStore.Constants
import hr.unipu.MuscleStore.Services.UserServices
import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.repositories.UserRepository

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import java.time.LocalDateTime

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.collections.HashMap

@RestController
@RequestMapping("/api/users")
class UsersResource {
    private val logger: Logger = LoggerFactory.getLogger(WorkoutPlanResource::class.java)

    @Autowired
    private lateinit var userServices: UserServices

    @Autowired
    private lateinit var userRepository: UserRepository

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

    @GetMapping("/me")
    fun getUserDetails(httpRequest: HttpServletRequest): ResponseEntity<User> {
        val userId = httpRequest.getAttribute("userId") as? Int
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val user: User = userRepository.findById(userId)
        return ResponseEntity(user, HttpStatus.OK)
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

    @PostMapping("/profile-picture")
    fun updateProfilePicture(
        @RequestBody profilePicture: String,  // Expect the Base64 string directly
        httpRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        val userId =  httpRequest.getAttribute("userId") as Int

        userServices.updateProfilePicture(userId, profilePicture)
        return ResponseEntity(mapOf("message" to "Profile picture updated successfully"), HttpStatus.OK)
    }

}
