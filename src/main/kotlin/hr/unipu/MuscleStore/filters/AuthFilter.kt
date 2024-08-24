package hr.unipu.MuscleStore.filters
import com.brendangoldberg.kotlin_jwt.KtJwtDecoder
import com.brendangoldberg.kotlin_jwt.KtJwtVerifier
import com.brendangoldberg.kotlin_jwt.algorithms.HSAlgorithm
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import hr.unipu.MuscleStore.Constants
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.filter.GenericFilterBean


    class AuthFilter : GenericFilterBean() {
        override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
            val httpRequest: HttpServletRequest = request as HttpServletRequest
            val httpResponse: HttpServletResponse = response as HttpServletResponse

                val authHeader: String? = httpRequest.getHeader("Authorization")
                if (authHeader != null) {

                val authHeaderArr: Array<String> = authHeader.split("Bearer").toTypedArray()

                if (authHeaderArr.size > 1) {
                    val token: String = authHeaderArr[1].trim()
                    try {

                        val constants = Constants()
                        val algorithm = HSAlgorithm.HS256(constants.API_SECRET_KEY)
                        val verifier = KtJwtVerifier(algorithm)
                        val jwt = KtJwtDecoder.decode(token)
                        verifier.verify(token)
                        println("Decoded JWT payload: ${jwt.payload}")

                        val mapper = ObjectMapper()
                        val claims: Map<Any, Any> = mapper.readValue(jwt.payload.toString(), Map::class.java) as Map<Any, Any>
                        httpRequest.setAttribute("userId", Integer.parseInt((claims["userId"] as Int).toString()))

                    } catch (e: Exception) {
                        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired token")
                        return
                    }
                } else {
                    httpResponse.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Authentication token must be Bearer [token]"
                    )
                    return
                }
            } else {
                httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "Authorization token must be provided")
                return;
            }
            // Continue the filter chain
                chain?.doFilter(request, response)
        }
    }
