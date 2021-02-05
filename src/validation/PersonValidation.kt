package validation

import schemas.Persons

object PersonValidation {

    fun <T> validatePersonExist(attribute: String, attributeValue: T): Boolean {
        when (attribute) {
            "id" -> if (Persons.personExistById(attributeValue as Int)) return true
            "lastname" -> if (Persons.personExistByLastname(attributeValue.toString())) return true
        }

        return false
    }

    fun validateInput(input: String): Boolean {
        if (input.isEmpty()) return false
        if (input.isBlank()) return false

        return true
    }
}
