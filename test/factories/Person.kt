package factories

import data.Person
import org.joda.time.DateTime

object Person {
    val instance by lazy {
        Person(0, "foo", "bar", DateTime.now())
    }
}
