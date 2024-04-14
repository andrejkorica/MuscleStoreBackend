package hr.unipu.MuscleStore.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorized")
class EtAuthException(message: String) : RuntimeException(message)