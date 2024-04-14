package hr.unipu.MuscleStore.repositories.Implementation

import hr.unipu.MuscleStore.domain.User
import hr.unipu.MuscleStore.exception.EtAuthException
import hr.unipu.MuscleStore.repositories.userRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.Cache.Connection
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.Statement
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType


@Repository
class userRepositoryImpl : userRepository {

    val SQL_CREATE : String = "INSERT INTO ET_USER (USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) VALUES (NEXTVAL('ET_USERS_SEQ'), ?, ?, ?, ?)"
    val SQL_COUNT_BY_EMAIL : String = "SELECT COUNT(*) FROM ET_USER WHERE EMAIL = ?"
    val SQL_FIND_BY_ID : String = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD " + "FROM ET_USER WHERE USER_ID = ?"

    @Autowired
    var jdbcTemplate: JdbcTemplate? = null

    override fun create(firstName: String, lastName: String, email: String, password: String): Int {
        try {
            val keyHolder : KeyHolder = GeneratedKeyHolder()
            jdbcTemplate!!.update({ connection ->
                val ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)
                ps.setString(1, firstName)
                ps.setString(2, lastName)
                ps.setString(3, email)
                ps.setString(4, password)
                ps
            }, keyHolder)
            return keyHolder.keys?.get("USER_ID") as Int;

        } catch (e: Exception) {
            throw EtAuthException("Invalid details. Failed to create account.")
        }
    }

    override fun findByEmailAndPassword(email: String, password: String): User {
        TODO("Not yet implemented")
    }

    override fun getCountByEmail(email: String): Int {
       val result: Int? = jdbcTemplate?.queryForObject(SQL_COUNT_BY_EMAIL, Int::class.java, email)
        return result!!

    }

    override fun findById(id: Int): User {
        val result: User? = jdbcTemplate?.queryForObject(SQL_COUNT_BY_EMAIL, userRowMapper, Int::class.java)
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
