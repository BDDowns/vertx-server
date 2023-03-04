import io.vertx.core.Vertx
import io.vertx.ext.web.Router

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    router.get("/status").handler {
        it.response()
            .setStatusCode(200)
            .end("OK")
    }
    vertx.createHttpServer()
        .requestHandler(router)
        .listen(8081)
    println("open http://localhost:8081")
}