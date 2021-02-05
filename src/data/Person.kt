package data

import com.fasterxml.jackson.annotation.JsonFormat
import org.joda.time.DateTime

data class Person(
    val id: Int?,
    val firstname: String,
    val lastname: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val date: DateTime
)
