
package api.v1

import controller.PersonController
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.personenManagement() {
    delete("/persons/{id}") {
        val personId = call.parameters["id"]
        val removedPerson = personId!!.let { id -> PersonController.removePerson(id) }
        call.respond(removedPerson)
    }
}
