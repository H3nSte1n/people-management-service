package helper

import validation.PersonValidation

object Controller {
    fun isInputValid(methods: List<String>): Boolean {
        for (input in methods) {
            if (!PersonValidation.validateInput(input)) return false
        }

        return true
    }
}
