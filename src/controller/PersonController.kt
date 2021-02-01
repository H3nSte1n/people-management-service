package controller

import data.Person
import helper.Controller.isInputValid
import schemas.Persons
import schemas.Persons.createPerson
import schemas.Persons.getPersons
import statuspages.InvalidUserException
import statuspages.ThrowableException
import validation.PersonValidation.validateInput
import validation.PersonValidation.validateUserExist

object PersonController {
    fun removePerson(id: Int): Int {
        if (!validateUserExist("id", id)) throw InvalidUserException()

        return Persons.deletePerson(id)
    }

    fun addPerson(receivedPerson: Person): Person {
        val inputs = arrayOf(receivedPerson.firstname, receivedPerson.lastname, receivedPerson.date.toString())

        if (!isInputValid(inputs)) throw ThrowableException()
        if (validateUserExist("String", receivedPerson.lastname)) throw InvalidUserException()

        return createPerson(receivedPerson)
    }

    fun returnAllPersons(): Collection<Person> {
        return getPersons()
    }
}
