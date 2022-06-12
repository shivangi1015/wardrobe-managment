package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO
import akka.util.ByteString
import model.{Clothing, WardrobeResponse}
import repository.WardrobeManagementRepository
import spray.json._

import java.io.FileOutputStream
import java.nio.file.Paths
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class WardrobeController(wardrobeManagementRepo: WardrobeManagementRepository)
                        (implicit val system: ActorSystem, val materializer: ActorMaterializer)
                         extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val clothing: RootJsonFormat[Clothing] = jsonFormat2(Clothing)
  implicit val listOfClothing: RootJsonFormat[WardrobeResponse] = jsonFormat1(WardrobeResponse)

  val route: Route =
    path("listClothing") {
      get {
        onComplete(wardrobeManagementRepo.listClothing) {
          case Success(value) => complete(WardrobeResponse(value))
          case Failure(exception) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }
    } ~ path("update" / "wardrobe") {
      entity(as[Clothing]) { request =>
        onComplete(wardrobeManagementRepo.insert(request)) {
          case Success(_) => complete("Successfully Inserted!")
          case Failure(_) => complete("")
        }
      }
    }
}
