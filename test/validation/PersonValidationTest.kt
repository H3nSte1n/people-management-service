package validation

import DatabaseConnection
import factories.Person
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import schemas.Persons
import kotlin.test.assertEquals

class PersonValidationTest {

    @AfterEach
    fun afterTest() {
        unmockkAll()
    }

    @Nested
    inner class validatePersonExist : DatabaseConnection() {

        @Test
        fun should_run_personExistByLastname_when_call_method_with_lastname() {
            val person = Person.instance
            mockkObject(Persons)

            every { Persons.personExistByLastname(any()) } returns true

            PersonValidation.validatePersonExist("lastname", person.firstname)

            verify {
                Persons.personExistByLastname(any())
            }
        }

        @Test
        fun should_run_personExistById_when_call_method_with_id() {
            val person = Person.instance
            mockkObject(Persons)

            every { Persons.personExistById(any()) } returns true

            PersonValidation.validatePersonExist("id", person.id)

            verify {
                Persons.personExistById(any())
            }
        }

        @Test
        fun should_return_false_when_call_method_with_invalid_attribute() {
            val person = Person.instance
            mockkObject(Persons)

            every { Persons.personExistByLastname(any()) } returns true
            every { Persons.personExistById(any()) } returns true

            PersonValidation.validatePersonExist("foo", person.lastname)

            verify(exactly = 0) {
                Persons.personExistById(any())
                Persons.personExistByLastname(any())
            }
        }

        @Test
        fun should_return_true_if_person_exist() {
            val person = Person.instance

            val storedPerson = transaction {
                Persons.insert {
                    it[firstname] = person.firstname
                    it[lastname] = person.lastname
                    it[birthdate] = person.date
                }
            }.let {
                data.Person(it[Persons.id], it[Persons.firstname], it[Persons.lastname], it[Persons.birthdate])
            }

            val returnValue = PersonValidation.validatePersonExist<String>("lastname", storedPerson.lastname)
            assertEquals(true, returnValue)
        }

        @Test
        fun should_return_false_if_person_not_exist() {
            val returnValue = PersonValidation.validatePersonExist<String>("lastname", "foobar")
            assertEquals(false, returnValue)
        }
    }

    @Nested
    inner class validateInput {

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
}
