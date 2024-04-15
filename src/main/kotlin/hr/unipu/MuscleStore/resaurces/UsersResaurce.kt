package hr.unipu.MuscleStore.resaurces

import hr.unipu.MuscleStore.Services.userServices
import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.repositories.userRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
        return ResponseEntity(map, HttpStatus.OK)
    }

    @PostMapping("/register")
    fun  registerUser(@RequestBody userMap: Map<String, String>): ResponseEntity<Map<String, String>> {
        val firstName = userMap["firstName"]
        val lastName = userMap["lastName"]
        val email = userMap["email"]
        val password = userMap["password"]

        val user : User = userServices.registerUser(firstName!!, lastName!!, email!!, password!!)
        var map = HashMap<String, String>()
        map.put("message", "registered successfully")
        return ResponseEntity(map, HttpStatus.CREATED)
    }

}