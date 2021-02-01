
package api.v1

import controller.PersonController
import data.Person
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.personenManagement() {
    delete("persons/{id}") {
        val personId = call.parameters["id"]
        val removedPerson = personId!!.let { id -> PersonController.removePerson(id.toInt()) }
        call.respond(removedPerson)
    }
    post("persons") {
        val receivedPerson = call.receive<Person>()
        val addedPerson = PersonController.addPerson(receivedPerson)
        call.respond(addedPerson)
    }
    get("persons") {
        val storedPersons = PersonController.returnAllPersons()
        call.respond(storedPersons)
    }
}
