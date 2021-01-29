package data

import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime
import java.util.*

data class Person(
    val id: EntityID<UUID>,
    val firstname: String,
    val lastname: String,
    val date: DateTime
) {}
