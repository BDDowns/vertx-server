import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    val json = json {
        obj(
            "status" to "OK"
        )
    }
    router.get("/status").handler {
        it.response()
            .setStatusCode(200)
            .end(json.toString())
    }
    vertx.createHttpServer()
        .requestHandler(router)
        .listen(8081)
    println("open http://localhost:8081")
}