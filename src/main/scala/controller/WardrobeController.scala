package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import model.{Clothing, Wardrobe}
import repository.WardrobeManagementRepository

import scala.io.StdIn
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

class WardrobeController(wardrobeManagementRepo: WardrobeManagementRepository)(implicit val system: ActorSystem, val materializer: ActorMaterializer) extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val clothing = jsonFormat2(Clothing)
  implicit val listOfClothing = jsonFormat1(Wardrobe)

  val route =
    path("listClothing") {
      get {
        wardrobeManagementRepo.listClothing.onComplete {
          case Success(value) => complete(value)
          case Failure(exception) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }
    }
}
