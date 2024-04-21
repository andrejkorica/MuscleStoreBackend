package hr.unipu.MuscleStore.resaurces

import com.brendangoldberg.kotlin_jwt.KtJwtCreator
import com.brendangoldberg.kotlin_jwt.algorithms.HSAlgorithm

import hr.unipu.MuscleStore.Constants
import hr.unipu.MuscleStore.Services.userServices
import hr.unipu.MuscleStore.domain.User

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.time.LocalDateTime
import java.time.ZoneId

import java.util.*
import kotlin.collections.HashMap

@RestController
@RequestMapping("/api/users")
class UsersResaurce {

    @Autowired
    private lateinit var userServices: userServices

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

    private fun generateJWTToken(user : User) : Map <String, String> {
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
            .addClaim("userId", user.getUserId() ?: 0)
            .addClaim("email", user.getEmail() ?: "")
            .addClaim("firstName", user.getFirstName() ?: "")
            .addClaim("lastName", user.getLastName() ?: "")
            .sign(algorithm)

        val map = mutableMapOf<String, String>()
        map["token"] = jwt
        return map
    }

}