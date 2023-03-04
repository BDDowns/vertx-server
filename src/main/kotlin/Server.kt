import io.vertx.core.Vertx

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.createHttpServer().requestHandler {
        it.response().end("OK")
    }.listen(8081)
    println("open http://localhost:8081")
}