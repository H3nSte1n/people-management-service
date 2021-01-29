package controller

import data.Person
import org.jetbrains.exposed.dao.EntityID
import schemas.Persons
import statuspages.InvalidUserException
import statuspages.ThrowableException
import validation.PersonValidation.validateInput
import validation.PersonValidation.validateUserExist
import java.util.*

object PersonController {
    fun removePerson(personId: String): Person {
        if (!validateUserExist(personId.toInt())) throw InvalidUserException()
        val removedPerson = Persons.deletePerson(personId as EntityID<UUID>)

        if(!validateInput(removedPerson?.firstname)) throw ThrowableException()
        return removedPerson
    }
}
