package schemas

import data.Person
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object Persons : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val firstname: Column<String> = varchar("firstname", 60)
    val lastname: Column<String> = varchar("lastname", 120)
    val birthdate: Column<DateTime> = datetime("birthdate")

    fun getPerson(customerUUID: Int): Person {
        return transaction {
            select { id eq customerUUID }.first()
        }.let { it ->
            Person(it[id], it[firstname], it[lastname], it[birthdate])
        }
    }

    fun getPersons(): Collection<Person> {
        return transaction {
            selectAll().map {
                Person(it[id], it[firstname], it[lastname], it[birthdate])
            }
        }
    }

    fun personExistById(customerId: Int): Boolean {
        return transaction {
            select { id eq customerId }.empty().not()
        }
    }

    fun personExistByLastname(customerId: String): Boolean {
        return transaction {
            select { lastname eq customerId }.empty().not()
        }
    }

    fun deletePerson(givenUUID: Int): Int {
        return transaction {
            deleteWhere { id eq givenUUID }
        }
    }

    fun updatePerson(selectedId: Int, person: Person): Person {
        return transaction {
            update({ id eq selectedId }) {
                it[firstname] = person.firstname
                it[lastname] = person.lastname
                it[birthdate] = person.date
            }
        }.let {
            getPerson(selectedId)
        }
    }

    fun createPerson(person: Person): Person {
        return transaction {
            insert {
                it[firstname] = person.firstname
                it[lastname] = person.lastname
                it[birthdate] = person.date
            }
        }.let {
            Person(it[id], it[firstname], it[lastname], it[birthdate])
        }
    }
}
