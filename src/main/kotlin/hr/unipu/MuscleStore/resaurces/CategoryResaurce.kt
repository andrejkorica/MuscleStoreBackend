package hr.unipu.MuscleStore.resaurces

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/categories")
class CategoryResaurce {
    @GetMapping
    fun getAllCategories (request: HttpServletRequest): String{
        val userId = request.getAttribute("userId") as? Int
        return "Authenticated! UserId $userId"
    }
}