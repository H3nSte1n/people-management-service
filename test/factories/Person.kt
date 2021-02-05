package factories

import com.turnierverwaltung_api_auth.enums.UserRole
import data.Person

object User {
    val instance by lazy {
        val generatedSalt = Password.generateSalt()
        Person(
            null,
            "foo",
            "foo@bar.com",
            Password.generateHash("foo", generatedSalt),
            generatedSalt,
            UserRole.admin
        )
    }
}
