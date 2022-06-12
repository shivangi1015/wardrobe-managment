import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import controller.WardrobeController
import repository.WardrobeManagementRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

object ApplicationServer extends App {

  implicit val system: ActorSystem = ActorSystem("hello-akka-http")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val wardrobeManagementRepo = WardrobeManagementRepository
  val wardrobeController = new WardrobeController(wardrobeManagementRepo)
  val bindingFuture = Http().bindAndHandle(wardrobeController.route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
