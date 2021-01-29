package validation

import DatabaseConnection
import factories.User
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import schemas.Users
import kotlin.test.assertEquals

class PersonValidationTest {

    @AfterEach
    fun afterTest() {
        unmockkAll()
    }

    @Nested
    inner class when_run_validateLoginCredentials {

        @Test
        fun should_call_validatePassword_method() {
            val user = User.instance
            mockkObject(PersonValidation)
            every { PersonValidation.validatePassword(any(), any(), any()) } returns true
            PersonValidation.validateLoginCredentials(user, "foo")
            verify {
                PersonValidation.validatePassword(any(), any(), any())
            }
        }

        @Test
        fun should_return_true_if_password_is_valid() {
            val user = User.instance
            val returnValue = PersonValidation.validateLoginCredentials(user, "foo")
            assertEquals(true, returnValue)
        }

        @Test
        fun should_return_false_if_password_is_invalid() {
            val user = User.instance
            val returnValue = PersonValidation.validateLoginCredentials(user, "foobar")
            assertEquals(false, returnValue)
        }
    }

    @Nested
    inner class when_run_validatePersonExist : DatabaseConnection() {

        @BeforeEach
        fun prepare() {
            val user = User.instance
            val generatedSalt = Password.generateSalt()

            transaction {
                Users.insert {
                    it[name] = user.name
                    it[email] = user.email
                    it[salt] = generatedSalt
                    it[passwordHash] = Password.generateHash(user.password, generatedSalt)
                    it[role] = user.role!!
                }
            }
        }

        @Test
        fun should_call_userExist_method() {
            val user = User.instance
            mockkObject(Users)
            every { Users.userExist(any()) } returns true
            PersonValidation.validateUserExist(user.name)
            verify {
                Users.userExist(any())
            }
        }

        @Test
        fun should_return_true_if_user_exist() {
            val user = User.instance
            val returnValue = PersonValidation.validateUserExist(user.name)
            assertEquals(true, returnValue)
        }

        @Test
        fun should_return_false_if_user_not_exist() {
            val returnValue = PersonValidation.validateUserExist("foobar")
            assertEquals(false, returnValue)
        }
    }

    @Nested
    inner class when_run_validateInput {

        @Test
        fun should_return_false_if_string_is_blank() {
            val returnValue = PersonValidation.validateInput(" ")
            assertEquals(false, returnValue)
        }

        @Test
        fun should_return_false_if_string_is_empty() {
            val returnValue = PersonValidation.validateInput("")
            assertEquals(false, returnValue)
        }

        @Test
        fun should_return_true_if_string_is_not_empty_or_blank() {
            val returnValue = PersonValidation.validateInput("foobar")
            assertEquals(true, returnValue)
        }
    }

    @Nested
    inner class when_run_validatePassword {

        @Test
        fun should_call_generateHash_method() {
            mockkObject(Password)
            every { Password.generateHash(any(), any()) } returns "hash"
            PersonValidation.validatePassword("", "", "")
            verify {
                Password.generateHash(any(), any())
            }
        }

        @Test
        fun should_return_false_if_gen_hash_length_neq_exp_pwd_length() {
            val user = User.instance
            mockkObject(Password)
            every { Password.generateHash(any(), any()) } returns "foo"
            val returnValue = PersonValidation.validatePassword("foobar", user.salt!!, user.password)
            assertEquals(false, returnValue)
        }

        @Test
        fun should_return_false_if_gen_hash_neq_exp_pwd() {
            val user = User.instance
            val returnValue = PersonValidation.validatePassword("test", user.salt!!, user.password)
            assertEquals(false, returnValue)
        }

        @Test
        fun should_return_true_if_gen_hash_eq_exp_pwd() {
            val user = User.instance
            val returnValue = PersonValidation.validatePassword("foo", user.salt!!, user.password)
            assertEquals(true, returnValue)
        }
    }
}
