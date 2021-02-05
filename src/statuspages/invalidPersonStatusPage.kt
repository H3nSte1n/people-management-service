package statuspages
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

fun StatusPages.Configuration.invalidPersonStatusPage() {
    exception<InvalidPersonException> { cause ->
        call.respondText(
            cause.message,
            ContentType.Text.Plain,
            status = HttpStatusCode.BadRequest
        )
    }
}

data class InvalidPersonException(override val message: String = "Invalid User Exception") : Exception()
