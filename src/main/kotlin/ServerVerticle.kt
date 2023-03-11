import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
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
        mainRouter.route()
            .subRouter(usersRouter())
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
        router.route().handler(BodyHandler.create())
        router.delete("/users/:id").handler { ctx ->
            val id = ctx.request().getParam("id").toInt()
            vertx.eventBus().request<Int>("users:delete", id) {
                ctx.end()
            }
        }
        router.put("/users/:id").handler { ctx ->
            val id = ctx.request().getParam("id").toInt()
            val body = ctx.body().asJsonObject().mergeIn(
                json {
                    obj (
                        "id" to id
                    )
            })
            vertx.eventBus().request<JsonObject>("users:update", body) {
                ctx.end()
            }
        }
        return router
    }
}