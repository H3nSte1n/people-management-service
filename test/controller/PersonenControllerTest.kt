package controller

import factories.User
import helper.Controller
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import schemas.Users
import statuspages.AuthenticationException
import statuspages.InvalidUserException
import statuspages.ThrowableException
import validation.PersonValidation
import kotlin.test.BeforeTest

class LoginControllerTest {
    lateinit var person: data.Person

    @BeforeTest
    fun prepare() {
        person = User.instance
        unmockkAll()
    }

    @AfterEach
    fun afterTest() {
        unmockkAll()
    }

    @Nested
    inner class when_run_login {

        @Test
        fun should_call_specific_methods() {
            mockkObject(Controller)
            mockkObject(Users)
            mockkObject(PersonValidation)
            mockkObject(Jwt)

            every { Controller.isInputValid(any()) } returns true
            every { PersonValidation.validateUserExist(any()) } returns true
            every { Users.findUser(any()) } returns person
            every { PersonValidation.validateLoginCredentials(any(), any()) } returns true
            every { Jwt.generateToken(any()) } returns "asd.asd.asd"

            LoginController.login(person)

            verify {
                Controller.isInputValid(any())
                PersonValidation.validateUserExist(any())
                Users.findUser(any())
                PersonValidation.validateLoginCredentials(any(), any())
                Jwt.generateToken(any())
            }

            verifyOrder {
                Controller.isInputValid(any())
                PersonValidation.validateUserExist(any())
                Users.findUser(any())
                PersonValidation.validateLoginCredentials(any(), any())
                Jwt.generateToken(any())
            }
        }

        @Test
        fun should_break_up_if_input_is_invalid() {
            mockkObject(Controller)

            every { Controller.isInputValid(any()) } returns false

            assertThrows(ThrowableException::class.java) {
                LoginController.login(person)
            }
        }

        @Test
        fun should_break_up_if_user_not_exist() {
            mockkObject(Controller)
            mockkObject(PersonValidation)

            every { Controller.isInputValid(any()) } returns true
            every { PersonValidation.validateUserExist(any()) } returns false

            assertThrows(InvalidUserException::class.java) {
                LoginController.login(person)
            }
        }

        @Test
        fun should_break_up_if_authentication_failed() {
            mockkObject(Controller)
            mockkObject(Users)
            mockkObject(PersonValidation)

            every { Controller.isInputValid(any()) } returns true
            every { PersonValidation.validateUserExist(any()) } returns true
            every { Users.findUser(any()) } returns person
            every { PersonValidation.validateLoginCredentials(any(), any()) } returns false

            assertThrows(AuthenticationException::class.java) {
                LoginController.login(person)
            }
        }
    }
}
