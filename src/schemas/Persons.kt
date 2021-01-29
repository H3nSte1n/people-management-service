package schemas

import data.Person
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.*

object Persons: UUIDTable() {
    val firstname: Column<String> = varchar("firstname", 60)
    val lastname: Column<String> = varchar("lastname", 120)
    val birthdate: Column<DateTime> = date("birthdate")

    fun getPerson(customerUUID: EntityID<UUID>): Person {
        return transaction {
            select { id eq customerUUID }.first()
        }.let { it ->
            Person(it[id], it[firstname], it[lastname], it[birthdate])
        }
    }

    fun personExist(customerUUID: EntityID<UUID>): Boolean {
        return transaction {
            select { id eq customerUUID }.empty().not()
        }
    }

    fun deletePerson(givenUUID: EntityID<UUID>): Person {
        return transaction {
            deleteWhere { id eq givenUUID }
        }.let {
            getPerson(givenUUID)
        }
    }

    fun updatePerson(person: Person): Person {
        return transaction {
            update ({ id eq person.id }) {
                it[firstname] = person.firstname
                it[lastname] = person.lastname
                it[birthdate] = person.date
            }
        }.let {
            getPerson(person.id)
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
