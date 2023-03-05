import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerTest {
    private val vertx: Vertx = Vertx.vertx()
    lateinit var client: WebClient

    @BeforeAll
    fun setup() {
        runBlocking {
            vertx.deployVerticle(ServerVerticle()).await()
            client = WebClient.create(
                vertx,
                WebClientOptions()
                    .setDefaultPort(8081)
                    .setDefaultHost("localhost")
            )
        }
    }

    @AfterAll
    fun tearDown() {
        vertx.close()
    }

    @Test
    fun `status returns 200`() {
        runBlocking {
            val response = client.get("/status").send().await()
            assertEquals(200, response.statusCode())
        }
    }
}