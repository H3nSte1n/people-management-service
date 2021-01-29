package statuspages

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UnknownErrorStatusPageTest {

    @Nested
    inner class when_throw_exception {

        @Test
        fun response_status_and_message() {
            withTestApplication {
                application.install(StatusPages) {
                    unknownErrorStatusPage()
                }
                application.routing {
                    get("/exception") {
                        throw UnknownErrorException()
                    }
                }

                handleRequest(HttpMethod.Get, "/exception").let { call ->
                    assertEquals(
                        HttpStatusCode.InternalServerError,
                        call.response.status(),
                        "Should return status code 500"
                    )
                    assertEquals(
                        UnknownErrorException().message,
                        call.response.content,
                        "Should return error message from InternalServerError"
                    )
                }
            }
        }
    }
}
