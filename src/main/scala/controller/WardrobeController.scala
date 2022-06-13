package controller

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.alpakka.csv.scaladsl.CsvParsing
import akka.stream.alpakka.csv.scaladsl.CsvToMap
import akka.stream.scaladsl.{FileIO, Flow, Sink}
import akka.util.ByteString
import model.{Clothing, WardrobeResponse}
import repository.WardrobeManagementRepository
import spray.json._

import java.io.FileOutputStream
import java.nio.file.Paths
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class WardrobeController(wardrobeManagementRepo: WardrobeManagementRepository)(implicit val system: ActorSystem, val materializer: ActorMaterializer) extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val clothing: RootJsonFormat[Clothing] = jsonFormat2(Clothing)
  implicit val listOfClothing: RootJsonFormat[WardrobeResponse] = jsonFormat1(WardrobeResponse)

  val route: Route =
    path("listClothing") {
      get {
        onComplete(wardrobeManagementRepo.listClothing) {
          case Success(value) => complete(value)
          case Failure(exception) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Exception while getting clothes! $exception</h1>"))
        }
      }
    } ~ path("add" / "wardrobe") {
      entity(as[Clothing]) { request =>
        onComplete(wardrobeManagementRepo.insert(request)) {
          case Success(value) => complete(s"$request inserted successfully!!!")
          case Failure(exception) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Exception while inserting record! $exception</h1>"))
        }
      }
    } ~ path("search" ) {
      get {
        parameter("name") {name =>
          onComplete(wardrobeManagementRepo.searchCloth(name)) {
            case Success(value) => complete(s"Clothing found! : $value")
            case Failure(exception) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Exception while searching record! $exception</h1>"))
          }
        }
      } ~ path("csv") {
        post {
          fileUpload("fileUpload") {
            case (_, fileStream) =>
              onComplete(fileStream.via(CsvParsing.lineScanner())
                .via(CsvToMap.withHeaders("name", "category"))
                .map(_.mapValues(e => e.utf8String))
                .map { m => Clothing(m("name"), m("category"))}
                .map(WardrobeManagementRepository.insert)
                .runWith(Sink.ignore)) {
                case Success(value) => complete("Successfully updated!")
                case Failure(exception) => complete(s"Failed to update $exception")
              }
          }
        }
      }

    }
}
