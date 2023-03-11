import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.Tuple
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerTest {
    private val vertx: Vertx = Vertx.vertx()
    lateinit var client: WebClient
    lateinit var db: SqlClient

    @BeforeAll
    fun setup() {
        runBlocking {
            vertx.deployVerticle(ServerVerticle()).await()
            vertx.deployVerticle(UsersVerticle()).await()
            client = WebClient.create(
                vertx,
                WebClientOptions()
                    .setDefaultPort(8081)
                    .setDefaultHost("localhost")
            )
            db = Db.connect(vertx)
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

    @Nested
    inner class `With user`() {
        lateinit var userRow: Row
        @BeforeEach
        fun createUsers() {
            runBlocking {
                val result = db.preparedQuery(
                    """
                        INSERT INTO users (name, email)
                        values($1, $2)
                        RETURNING ID
                    """.trimIndent()
                ).execute(Tuple.of("user1", "user1@email.com")).await()
                userRow = result.first()
            }
        }

        @AfterEach
        fun deleteAll() {
            runBlocking {
                db.preparedQuery("DELETE FROM users").execute().await()
            }
        }

        @Test
        fun `delete deletes a user by ID`() {
            runBlocking {
                val userId = userRow.getInteger(0)
                client.delete("/users/$userId").send().await()
                val result = db.preparedQuery("SELECT * FROM users WHERE id = $1").execute(Tuple.of(userId)).await()
                assertEquals(0, result.size())
            }
        }

        @Test
        fun `put updates a user by ID`() {
            runBlocking {
                val userId = userRow.getInteger(0)
                client.put("/users/$userId")
                    .sendJsonObject(json { obj("name" to "user2", "email" to "user2@email.com") })
                    .await()
                val result = db.preparedQuery("SELECT * FROM users WHERE id = $1")
                    .execute(Tuple.of(userId))
                    .await()
                assertEquals("user2", result.first().getString("name"))
                assertEquals("user2@email.com", result.first().getString("email"))
            }
        }
    }
}