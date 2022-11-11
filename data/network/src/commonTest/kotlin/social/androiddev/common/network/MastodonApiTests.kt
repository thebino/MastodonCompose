@file:OptIn(ExperimentalCoroutinesApi::class)

package social.androiddev.common.network

import com.goncalossilva.resources.Resource
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.*

class MastodonApiTests {
    @Test
    @Ignore // this is broken
    fun `Instance request should fail with invalid response`() = runTest {
        // given
        val content: String = Resource("src/commonTest/resources/response_instance_invalid.json").readText()
        val mastodonApi = MastodonApiImpl(
            httpClient = createMockClient(
                statusCode = HttpStatusCode.Unauthorized, content = ByteReadChannel(text = content)
            )
        )

        // when
        val result = mastodonApi.getInstance("")

        // then
        assertFalse(actual = result.isSuccess)
        assertNull(actual = result.getOrNull()?.uri)
    }

    @Test
    @Ignore // this is broken
    fun `Instance request should succeed with required field response`() = runTest {
        // given
        val content = Resource("src/commonTest/resources/response_instance_valid.json").readText()

        val mastodonApi = MastodonApiImpl(
            httpClient = createMockClient(
                statusCode = HttpStatusCode.Unauthorized, content = ByteReadChannel(text = content)
            )
        )

        // when
        val result = mastodonApi.getInstance("")

        // then
        assertTrue(actual = result.isSuccess)
        assertNotNull(actual = result.getOrNull()?.uri)
    }


    private fun createMockClient(
        statusCode: HttpStatusCode = HttpStatusCode.OK, content: ByteReadChannel
    ): HttpClient {
        return HttpClient(MockEngine {
            respond(
                content = content, status = statusCode, headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }, contentType = ContentType.Application.Json
                )
            }
        }
    }
}
