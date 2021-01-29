import api.api
import com.fasterxml.jackson.databind.SerializationFeature
import controller.LoginController
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class AuthenticationTest {
    val body =
        """{"name":"Henry","password":"foobar"}"""

    @Nested
    inner class api_v1_sign_in {
        val path = "/api/v1/sign-in"

        @BeforeTest
        fun prepare() {
            mockkObject(LoginController)
            every { LoginController.login(any()) } returns "xxx.xxx.xxx"
        }

        @Test
        fun should_call_LoginController_login() {
            fun tests(call: TestApplicationCall) {
                verify {
                    LoginController.login(any())
                }
            }
            initApplication(::tests, path, body)
        }

        @Test
        fun should_respond_jwt_token() {
            fun tests(call: TestApplicationCall) {
                assertEquals("xxx.xxx.xxx", call.response.content)
            }
            initApplication(::tests, path, body)
        }

        @Test
        fun should_return_200_http_status_code() {
            fun tests(call: TestApplicationCall) {
                assertEquals(HttpStatusCode.OK, call.response.status())
            }
            initApplication(::tests, path, body)
        }
    }

    @Nested
    inner class api_v1_sign_up {
        val path = "/api/v1/sign-up"

        @BeforeTest
        fun prepare() {
            mockkObject(RegisterController)
            every { RegisterController.register(any()) } returns "xxx.xxx.xxx"
        }

        @Test
        fun should_call_RegisterController_register() {
            fun tests(call: TestApplicationCall) {
                verify {
                    RegisterController.register(any())
                }
            }
            initApplication(::tests, path, body)
        }

        @Test
        fun should_respond_jwt_token() {
            fun tests(call: TestApplicationCall) {
                assertEquals("xxx.xxx.xxx", call.response.content)
            }
            initApplication(::tests, path, body)
        }

        @Test
        fun should_return_200_http_status_code() {
            fun tests(call: TestApplicationCall) {
                assertEquals(HttpStatusCode.OK, call.response.status())
            }
            initApplication(::tests, path, body)
        }
    }

    private fun initApplication(tests: (call: TestApplicationCall) -> Unit, path: String, body: String) {
        withTestApplication {
            application.routing {
                api()
            }

            application.install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                }
            }

            handleRequest(HttpMethod.Post, path) {
                addHeader("Accept", "text/plain")
                addHeader("Content-Type", "application/json; charset=UTF-16")
                setBody(body.toByteArray(charset = Charsets.UTF_16))
            }.let { call ->
                tests(call)
            }
        }
    }
}
