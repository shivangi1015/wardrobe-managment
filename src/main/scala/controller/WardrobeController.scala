package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.Sink
import model.{Clothing, WardrobeResponse}
import repository.WardrobeManagementRepository
import spray.json._

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
          case Failure(exception) => complete(s"Failed to list the clothing with $exception")
        }
      }
    } ~ path("update" / "wardrobe") {
      entity(as[Clothing]) { request =>
        onComplete(wardrobeManagementRepo.insert(request)) {
          case Success(_) => complete("Successfully Inserted!")
          case Failure(exception) => complete(s"Failed to update $exception")
        }
      }
    } ~ path("csv") {
      post {
        fileUpload("fileUpload") {
          case (_, fileStream) =>

            val res =
              fileStream.via(CsvParsing.lineScanner())
                .via(CsvToMap.withHeaders("name", "category"))
                .map(_.mapValues(e => e.utf8String))
                .map { m => Clothing(m("name"), m("category"))}
                .runWith(Sink.seq)

             onComplete(res.map(_ => WardrobeManagementRepository.bulkInsert _)) {
               case Success(_) => complete("Successfully updated!")
               case Failure(exception) => complete(s"Failed to update $exception")
             }
           }
        }
      }

}
