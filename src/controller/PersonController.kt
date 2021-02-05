package controller

import ClassHelper
import data.Person
import helper.Controller.isInputValid
import schemas.Persons
import schemas.Persons.createPerson
import schemas.Persons.getPersons
import statuspages.InvalidPersonException
import statuspages.ThrowableException
import validation.PersonValidation.validatePersonExist

object PersonController {
    fun removePerson(id: Int): Int {
        if (!validatePersonExist("id", id)) throw InvalidPersonException()

        return Persons.deletePerson(id)
    }

    fun addPerson(receivedPerson: Person): Person {
        val inputs = listOf(receivedPerson.firstname, receivedPerson.lastname, receivedPerson.date.toString())

        if (!isInputValid(inputs)) throw ThrowableException()
        if (validatePersonExist("String", receivedPerson.lastname)) throw InvalidPersonException()

        return createPerson(receivedPerson)
    }

    fun getAllPersons(): Collection<Person> {
        return getPersons()
    }

    fun getPerson(personId: Int): Person {
        return Persons.getPerson(personId)
    }

    fun updatePerson(id: Int, newPersonValues: Person): Person {
        var inputs: MutableList<String> = ClassHelper.getAllPropertyValues(newPersonValues)

        if (!isInputValid(inputs)) throw ThrowableException()
        if (!validatePersonExist("id", id)) throw InvalidPersonException()

        return Persons.updatePerson(id, newPersonValues)
    }
}
