package controller

import factories.Person
import helper.Controller
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import schemas.Persons
import statuspages.InvalidPersonException
import statuspages.ThrowableException
import validation.PersonValidation
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class PersonenControllerTest {
    lateinit var person: data.Person

    @BeforeTest
    fun prepare() {
        person = Person.instance
        unmockkAll()
    }

    @AfterEach
    fun afterTest() {
        unmockkAll()
    }

    @Nested
    inner class when_run_removePerson {

        @Test
        fun should_call_specific_methods() {
            mockkObject(PersonValidation)
            mockkObject(Persons)

            every { PersonValidation.validatePersonExist<Int>(any(), any()) } returns true
            every { Persons.deletePerson(any()) } returns 1

            PersonController.removePerson(3)

            verify {
                PersonValidation.validatePersonExist<Int>(any(), any())
                Persons.deletePerson(any())
            }

            verifyOrder {
                PersonValidation.validatePersonExist<Int>(any(), any())
                Persons.deletePerson(any())
            }
        }

        @Test
        fun should_break_up_if_input_is_invalid() {
            mockkObject(PersonValidation)

            every { PersonValidation.validatePersonExist<Int>(any(), any()) } returns false

            assertThrows(InvalidPersonException::class.java) {
                PersonController.removePerson(3)
            }
        }

        @Test
        fun should_return_count_of_removed_Persons_on_valid_request() {
            mockkObject(PersonValidation)
            mockkObject(Persons)

            every { PersonValidation.validatePersonExist<Int>(any(), any()) } returns true
            every { Persons.deletePerson(any()) } returns 1

            assertEquals(PersonController.removePerson(3), 1)
        }
    }

    @Nested
    inner class when_run_addPerson {

        @Test
        fun should_call_specific_methods() {
            mockkObject(Controller)
            mockkObject(PersonValidation)
            mockkObject(Persons)

            every { Controller.isInputValid(any()) } returns true
            every { PersonValidation.validatePersonExist<String>(any(), any()) } returns false
            every { Persons.createPerson(any()) } returns person

            PersonController.addPerson(person)

            verify {
                Controller.isInputValid(any())
                PersonValidation.validatePersonExist<String>(any(), any())
                Persons.createPerson(any())
            }

            verifyOrder {
                Controller.isInputValid(any())
                PersonValidation.validatePersonExist<String>(any(), any())
                Persons.createPerson(any())
            }
        }

        @Test
        fun should_break_up_if_input_is_invalid() {
            mockkObject(Controller)

            every { Controller.isInputValid(any()) } returns false

            assertThrows(ThrowableException::class.java) {
                PersonController.addPerson(person)
            }
        }

        @Test
        fun should_break_up_if_person_already_exist() {
            mockkObject(Controller)
            mockkObject(PersonValidation)

            every { Controller.isInputValid(any()) } returns true
            every { PersonValidation.validatePersonExist<String>(any(), any()) } returns true

            assertThrows(InvalidPersonException::class.java) {
                PersonController.addPerson(person)
            }
        }

        @Test
        fun should_return_added_person_on_valid_request() {
            mockkObject(Controller)
            mockkObject(PersonValidation)
            mockkObject(Persons)

            every { Controller.isInputValid(any()) } returns true
            every { PersonValidation.validatePersonExist<String>(any(), any()) } returns false
            every { Persons.createPerson(any()) } returns person

            assertEquals(PersonController.addPerson(person), person)
        }
    }

    @Nested
    inner class when_run_getAllPersons {

        @Test
        fun should_call_specific_method() {
            mockkObject(Persons)

            every { Persons.getPersons() } returns listOf(person)

            PersonController.getAllPersons()

            verify {
                Persons.getPersons()
            }
        }

        @Test
        fun should_return_list_of_stored_Persons_on_valid_request() {
            mockkObject(Persons)

            every { Persons.getPersons() } returns listOf(person)

            assertEquals(PersonController.getAllPersons(), listOf(person))
        }
    }

    @Nested
    inner class when_run_updatePerson {

        @Test
        fun should_call_specific_method() {
            mockkObject(Controller)
            mockkObject(PersonValidation)
            mockkObject(Persons)

            every { Controller.isInputValid(any()) } returns true
            every { PersonValidation.validatePersonExist<String>(any(), any()) } returns true
            every { Persons.updatePerson(any(), any()) } returns person

            PersonController.updatePerson(1, person)

            verify {
                Controller.isInputValid(any())
                PersonValidation.validatePersonExist<String>(any(), any())
                Persons.updatePerson(any(), any())
            }

            verifyOrder {
                Controller.isInputValid(any())
                PersonValidation.validatePersonExist<String>(any(), any())
                Persons.updatePerson(any(), any())
            }
        }

        @Test
        fun should_break_up_if_input_is_invalid() {
            mockkObject(Controller)

            every { Controller.isInputValid(any()) } returns false

            assertThrows(ThrowableException::class.java) {
                PersonController.updatePerson(1, person)
            }
        }

        @Test
        fun should_break_up_if_person_not_exist() {
            mockkObject(Controller)
            mockkObject(PersonValidation)

            every { Controller.isInputValid(any()) } returns true
            every { PersonValidation.validatePersonExist<String>(any(), any()) } returns false

            assertThrows(InvalidPersonException::class.java) {
                PersonController.updatePerson(1, person)
            }
        }

        @Test
        fun should_return_updated_person_on_valid_request() {
            mockkObject(Controller)
            mockkObject(PersonValidation)
            mockkObject(Persons)

            every { Controller.isInputValid(any()) } returns true
            every { PersonValidation.validatePersonExist<String>(any(), any()) } returns true
            every { Persons.updatePerson(any(), any()) } returns person

            assertEquals(PersonController.updatePerson(1, person), person)
        }
    }
}
