package hr.unipu.MuscleStore.repositories

import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.exception.EtAuthException
import kotlin.jvm.Throws

interface UserRepository {

    @Throws(EtAuthException::class)
    fun create(firstName: String, lastName: String, email: String, password: String): Int;

    @Throws(EtAuthException::class)
    fun findByEmailAndPassword(email: String, password: String): User;

    fun getCountByEmail(email: String): Int;

    fun findById(id: Int): User;

    @Throws(EtAuthException::class)
    fun updateProfilePicture(userId: Int, profilePicture: String)

}