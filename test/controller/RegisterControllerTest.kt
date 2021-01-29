package controller

import factories.User
import helper.Controller
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import schemas.Users
import statuspages.InvalidUserException
import statuspages.ThrowableException
import validation.PersonValidation
import kotlin.test.BeforeTest

class RegisterControllerTest {
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
    inner class when_run_register {

        @Test
        fun should_call_specific_methods() {
            mockkObject(Controller)
            mockkObject(PersonValidation)
            mockkObject(Users)
            mockkObject(Jwt)

            every { Controller.isInputValid(any()) } returns true
            every { PersonValidation.validateUserExist(any()) } returns false
            every { Users.createUser(any()) } returns person
            every { Jwt.generateToken(any()) } returns "asd.asd.asd"

            RegisterController.register(person)

            verify {
                Controller.isInputValid(any())
                PersonValidation.validateUserExist(any())
                Users.createUser(any())
                Jwt.generateToken(any())
            }

            verifyOrder {
                Controller.isInputValid(any())
                PersonValidation.validateUserExist(any())
                Users.createUser(any())
                Jwt.generateToken(any())
            }
        }

        @Test
        fun should_break_up_if_input_is_invalid() {
            mockkObject(Controller)

            every { Controller.isInputValid(any()) } returns false

            assertThrows(ThrowableException::class.java) {
                RegisterController.register(person)
            }
        }

        @Test
        fun should_break_up_if_user_exist() {
            mockkObject(Controller)
            mockkObject(PersonValidation)

            every { Controller.isInputValid(any()) } returns true
            every { PersonValidation.validateUserExist(any()) } returns true

            assertThrows(InvalidUserException::class.java) {
                RegisterController.register(person)
            }
        }
    }
}
