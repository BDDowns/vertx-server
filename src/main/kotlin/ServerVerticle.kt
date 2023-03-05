import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle

class ServerVerticle : CoroutineVerticle() {
    override suspend fun start() {
        val mainRouter = Router.router(vertx)
        mainRouter.get("/status").handler {
            it.response()
                .setStatusCode(200)
                .end(getHealth().toString())
        }
        mainRouter.route().subRouter(usersRouter())
        vertx.createHttpServer()
            .requestHandler(mainRouter)
            .listen(8081)
        println("open http://localhost:8081/health")
    }

    private fun getHealth(): JsonObject {
        return json {
            obj(
                "status" to "OK"
            )
        }
    }

    private fun usersRouter(): Router {
        val router = Router.router(vertx)
        router.delete("/users/:id").handler {
            TODO("code for deleting users")
        }
        router.put("/users/:id").handler {
            TODO("code for updating a user")
        }
        return router
    }
}