package hr.unipu.MuscleStore.repositories.Implementation

import at.favre.lib.crypto.bcrypt.BCrypt
import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.exception.EtAuthException
import hr.unipu.MuscleStore.repositories.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import java.sql.SQLException
import java.sql.Statement

@Repository
class UserRepositoryImpl : UserRepository {

    private val SQL_CREATE: String = "INSERT INTO et_users (FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) VALUES (?, ?, ?, ?)"
    private val SQL_COUNT_BY_EMAIL: String = "SELECT COUNT(*) FROM et_users WHERE EMAIL = ?"
    private val SQL_FIND_BY_ID: String = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, PROFILE_PICTURE FROM et_users WHERE USER_ID = ?"
    private val SQL_FIND_BY_EMAIL: String = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, PROFILE_PICTURE FROM et_users WHERE EMAIL = ?"
    private val SQL_UPDATE_PROFILE_PICTURE = "UPDATE et_users SET PROFILE_PICTURE = ? WHERE USER_ID = ?"
    private val SQL_DELETE_USER: String = "DELETE FROM et_users WHERE USER_ID = ?"
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Throws(EtAuthException::class, SQLException::class)
    override fun create(firstName: String, lastName: String, email: String, password: String): Int {
        val hashedPassword: String = BCrypt.withDefaults().hashToString(10, password.toCharArray())
        return try {
            val keyHolder: KeyHolder = GeneratedKeyHolder()
            jdbcTemplate.update({ connection ->
                val ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)
                ps.setString(1, firstName)
                ps.setString(2, lastName)
                ps.setString(3, email)
                ps.setString(4, hashedPassword)
                ps
            }, keyHolder)
            keyHolder.keys?.get("USER_ID") as Int
        } catch (e: Exception) {
            throw EtAuthException("Invalid details. Failed to create account.")
        }
    }

    @Throws(EtAuthException::class)
    override fun findByEmailAndPassword(email: String, password: String): User {
        return try {
            val result: User? = jdbcTemplate.queryForObject(SQL_FIND_BY_EMAIL, userRowMapper, email)
            if (result == null || !BCrypt.verifyer().verify(password.toCharArray(), result.password).verified) {
                throw EtAuthException("Invalid email or password.")
            }
            result
        } catch (e: EmptyResultDataAccessException) {
            throw EtAuthException("Invalid email or password.")
        }
    }

    override fun getCountByEmail(email: String): Int {
        val result: Int? = jdbcTemplate.queryForObject(SQL_COUNT_BY_EMAIL, Int::class.java, email)
        return result ?: 0
    }

    override fun findById(id: Int): User {
        val result: User? = jdbcTemplate.queryForObject(SQL_FIND_BY_ID, userRowMapper, id)
        return result ?: throw EtAuthException("User not found.")
    }

    private val userRowMapper: RowMapper<User> = RowMapper { rs, _ ->
        User(
            userId = rs.getInt("USER_ID"),
            firstName = rs.getString("FIRST_NAME"),
            lastName = rs.getString("LAST_NAME"),
            email = rs.getString("EMAIL"),
            password = rs.getString("PASSWORD"),
            profilePicture = rs.getString("PROFILE_PICTURE")
        )
    }

    override fun updateProfilePicture(userId: Int, profilePicture: String) {
        // Log the values being used for debugging
        println("Updating profile picture for userId: $userId")
        println("Profile picture data: $profilePicture")

        if (userId <= 0) {
            throw EtAuthException("Invalid userId.")
        }

        try {
            val rowsAffected = jdbcTemplate.update(SQL_UPDATE_PROFILE_PICTURE, profilePicture, userId)
            if (rowsAffected == 0) {
                throw EtAuthException("Failed to update profile picture. User not found.")
            }
        } catch (e: Exception) {
            // Log exception details
            println("Exception occurred while updating profile picture: ${e.message}")
            throw EtAuthException("An error occurred while updating the profile picture.")
        }
    }

    @Transactional
    override fun deleteById(id: Int) {
        try {

            // Finally, delete the user record
            jdbcTemplate.update(SQL_DELETE_USER, id)
        } catch (e: Exception) {
            throw EtAuthException("Failed to delete user. ${e.message}")
        }
    }

}
