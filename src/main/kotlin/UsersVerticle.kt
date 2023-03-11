import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.Tuple
import kotlinx.coroutines.launch

class UsersVerticle: CoroutineVerticle() {
    override suspend fun start() {
        val db = Db.connect(vertx)
        vertx.eventBus().consumer<Int>("users:delete") {
            launch {
                val id = it.body()
                db.preparedQuery("DELETE FROM users WHERE ID = $1")
                    .execute(Tuple.of(id))
                    .await()
                it.reply(null)
            }
        }
        vertx.eventBus().consumer<JsonObject>("users:update") {
            launch {
                val body = it.body()
                db.preparedQuery("UPDATE users SET name = $1, email = $2 WHERE ID = $3")
                    .execute(
                        Tuple.of(
                            body.getString("name"),
                            body.getString("email"),
                            body.getInteger("id"),
                        )
                    ).await()
                it.reply(body.getInteger("id"))
            }
        }
    }
}