package hr.unipu.MuscleStore.Services

import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.exception.EtAuthException

interface userServices {

    @Throws(EtAuthException::class)
    fun validateUser(email :String, password :String) : User

    @Throws(EtAuthException::class)
    fun registerUser(firstName :String, lastName :String, email :String, password :String) : User
}