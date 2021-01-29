package validation

import org.jetbrains.exposed.dao.EntityID
import schemas.Persons
import java.util.*

object PersonValidation {

    fun validateUserExist(id: Int): Boolean {
        if (Persons.personExist(id as EntityID<UUID>)) return true

        return false
    }

    fun validateInput(input: String): Boolean {
        if (input.isEmpty()) return false
        if (input.isBlank()) return false

        return true
    }
}
