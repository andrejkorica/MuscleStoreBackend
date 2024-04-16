package hr.unipu.MuscleStore.repositories.Implementation

import at.favre.lib.crypto.bcrypt.BCrypt
import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.exception.EtAuthException
import hr.unipu.MuscleStore.repositories.userRepository
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
class userRepositoryImpl : userRepository {

    val SQL_CREATE : String = "INSERT INTO et_users  (USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) VALUES (NEXTVAL('ET_USERS_SEQ'), ?, ?, ?, ?)"
    val SQL_COUNT_BY_EMAIL : String = "SELECT COUNT(*) FROM et_users  WHERE EMAIL = ?"
    val SQL_FIND_BY_ID : String = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD " + "FROM et_users  WHERE USER_ID = ?"
    val SQL_FIND_BY_EMAIL : String = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD " + "FROM et_users  WHERE EMAIL = ?"

    @Autowired
    var jdbcTemplate: JdbcTemplate? = null

    @Override
    @Throws(EtAuthException::class, SQLException::class)
    override fun create(firstName: String, lastName: String, email: String, password: String): Int {
        val hashedPassword: String = BCrypt.withDefaults().hashToString(10, password.toCharArray())
        try {
            val keyHolder : KeyHolder = GeneratedKeyHolder()
            jdbcTemplate!!.update({ connection ->
                val ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)
                ps.setString(1, firstName)
                ps.setString(2, lastName)
                ps.setString(3, email)
                ps.setString(4, hashedPassword)

                ps
            }, keyHolder)
            return keyHolder.keys?.get("USER_ID") as Int;

        } catch (e: Exception) {
            throw EtAuthException("Invalid details. Failed to create account.")
        }
    }

    override fun findByEmailAndPassword(email: String, password: String): User {
    try {
        val result: User? = jdbcTemplate?.queryForObject(SQL_FIND_BY_EMAIL, userRowMapper, email)
        if (!BCrypt.verifyer().verify(password.toCharArray(), result!!.getPassword()).verified) {
            throw EtAuthException("Invalid email or password.")
        }
        return result
    } catch (e: EmptyResultDataAccessException) {
        throw EtAuthException("Invalid email or password.")
    }
}

    override fun getCountByEmail(email: String): Int {
       val result: Int? = jdbcTemplate?.queryForObject(SQL_COUNT_BY_EMAIL, Int::class.java, email)
        return result!!

    }

    override fun findById(id: Int): User {
        val result: User? = jdbcTemplate?.queryForObject(SQL_FIND_BY_ID, userRowMapper, id)
        return result!!
    }


        val userRowMapper: RowMapper<User> = RowMapper<User> { rs, _ ->
    User(
        rs.getInt("USER_ID"),
        rs.getString("FIRST_NAME"),
        rs.getString("LAST_NAME"),
        rs.getString("EMAIL"),
        rs.getString("PASSWORD")
    )
}
}
