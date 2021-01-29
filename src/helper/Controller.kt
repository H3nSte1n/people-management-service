package helper

import validation.PersonValidation

object Controller {
    fun isInputValid(methods: Array<String>): Boolean {
        for (input in methods) {
            if (!PersonValidation.validateInput(input)) return false
        }

        return true
    }
}
