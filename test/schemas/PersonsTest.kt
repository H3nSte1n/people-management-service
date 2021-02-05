package schemas

import DatabaseConnection
import DatabaseUtil.assertColumnExist
import DatabaseUtil.assertColumnType
import data.Person
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import kotlin.test.*

class PersonsTest : DatabaseConnection() {

    @BeforeTest
    fun prepare() {
        prepareTables()
    }

    @Nested
    inner class integration_tests {
        @Test
        @DisplayName("All Columns are avaiable")
        fun testColumnsExist() {
            transaction {
                assertColumnExist("id", Persons)
                assertColumnExist("firstname", Persons)
                assertColumnExist("lastname", Persons)
                assertColumnExist("birthdate", Persons)
            }
        }

        @Test
        @DisplayName("All Columns has the correct sql-type")
        fun testColumnsType() {
            transaction {
                assertColumnType("id", Persons, "serial")
                assertColumnType("firstname", Persons, "varchar(60)")
                assertColumnType("lastname", Persons, "varchar(120)")
                assertColumnType("birthdate", Persons, "timestamp")
            }
        }
    }

    @Nested
    inner class getPerson {

        @Test
        fun should_find_and_return_Person_by_name() {
            val persistedPerson = createAndStorePerson()

            val selectedPerson = Persons.getPerson(persistedPerson.id!!)
            assertNotNull(selectedPerson.id)
            assertEquals(persistedPerson.firstname, selectedPerson.firstname)
            assertEquals(persistedPerson.lastname, selectedPerson.lastname)
            assertEquals(persistedPerson.date, selectedPerson.date)
        }
    }

    @Nested
    inner class getPersons {

        @Test
        fun should_find_and_return_Persons_Collection() {
            val persistedPerson = createAndStorePerson()

            val personCollection = Persons.getPersons()
            assertNotNull(personCollection.first().id)
            assertEquals(persistedPerson.firstname, personCollection.first().firstname)
            assertEquals(persistedPerson.lastname, personCollection.first().lastname)
            assertEquals(persistedPerson.date, personCollection.first().date)
        }
    }

    @Nested
    inner class personExistById {

        @Test
        fun should_return_true_if_person_exists() {
            val persistedPerson = createAndStorePerson()

            val personExist = Persons.personExistById(persistedPerson.id!!)
            assertTrue(personExist)
        }

        @Test
        fun should_return_false_if_person_exists() {
            val persistedPerson = createAndStorePerson()

            val personExist = Persons.personExistById(persistedPerson.id!! + 1)
            assertFalse(personExist)
        }
    }

    @Nested
    inner class personExistByLastname {
        lateinit var persistedPerson: Person

        @BeforeEach
        fun prepare() {
            persistedPerson = createAndStorePerson()
        }

        @Test
        fun should_return_true_if_person_exists() {
            val personExist = Persons.personExistByLastname(persistedPerson.lastname)
            assertTrue(personExist)
        }

        @Test
        fun should_return_false_if_person_exists() {
            val personExist = Persons.personExistByLastname(persistedPerson.lastname)
            assertTrue(personExist)
        }
    }

    @Nested
    inner class deletePerson {
        lateinit var persistedPerson: Person

        @BeforeEach
        fun prepare() {
            persistedPerson = createAndStorePerson()
        }

        @Test
        fun should_remove_person_if_exists() {
            Persons.deletePerson(persistedPerson.id!!)

            val personExist = transaction {
                Persons.select { Persons.id eq persistedPerson.id!! }.empty().not()
            }
            assertFalse(personExist)
        }

        @Test
        fun should_ignore_call_if_person_not_exists() {
            assertDoesNotThrow { Persons.deletePerson(persistedPerson.id!! + 1) }
        }
    }

    @Nested
    inner class updatePerson {
        lateinit var persistedPerson: Person

        @BeforeEach
        fun prepare() {
            persistedPerson = createAndStorePerson()
        }

        @Test
        fun should_update_person_if_exists() {
            val newPersonData = Person(
                null,
                "hallo",
                "foobar",
                DateTime.now()
            )
            val updatedPerson = Persons.updatePerson(persistedPerson.id!!, newPersonData)

            assertNotNull(persistedPerson.id)
            assertEquals(updatedPerson.firstname, newPersonData.firstname)
            assertEquals(updatedPerson.lastname, newPersonData.lastname)
            assertEquals(updatedPerson.date, newPersonData.date)
        }
    }

    @Nested
    inner class createPerson {

        @Test
        fun should_create_and_persist_a_new_Person() {
            val person = factories.Person.instance
            Persons.createPerson(person)
            val persistedPerson = transaction {
                Persons.select { Persons.firstname eq person.firstname }.first()
            }.let {
                Person(it[Persons.id], it[Persons.firstname], it[Persons.lastname], it[Persons.birthdate])
            }

            assertNotNull(persistedPerson.id)
            assertEquals(person.firstname, persistedPerson.firstname)
            assertEquals(person.lastname, persistedPerson.lastname)
            assertEquals(person.date, persistedPerson.date)
        }
    }

    private fun createAndStorePerson(): Person {
        val person = factories.Person.instance

        return transaction {
            Persons.insert {
                it[firstname] = person.firstname
                it[lastname] = person.lastname
                it[birthdate] = person.date
            }
        }.let {
            Person(it[Persons.id], it[Persons.firstname], it[Persons.lastname], it[Persons.birthdate])
        }
    }

    private fun prepareTables() {
        transaction {
            exec("TRUNCATE persons RESTART IDENTITY CASCADE;")
        }
    }
}
