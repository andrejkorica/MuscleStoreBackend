package hr.unipu.MuscleStore

import hr.unipu.MuscleStore.filters.AuthFilter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean


@SpringBootApplication
class MuscleStoreApplication {
	@Bean
	fun authFilterRegistrationBean(): FilterRegistrationBean<AuthFilter> {
		val registration = FilterRegistrationBean<AuthFilter>()
		val authFilter = AuthFilter()
		registration.filter = authFilter
		registration.addUrlPatterns("/api/workout-plans/*")
		return registration
	}


}

	fun main(args: Array<String>) {
		runApplication<MuscleStoreApplication>(*args)
	}

