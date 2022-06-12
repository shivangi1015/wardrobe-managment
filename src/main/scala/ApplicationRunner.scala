import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import controller.WardrobeController
import repository.WardrobeManagementRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

object ApplicationRunner extends App {

  implicit val system: ActorSystem = ActorSystem("hello-akka-http")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val wardrobeManagementRepo = WardrobeManagementRepository
  val wardrobeController = new WardrobeController(wardrobeManagementRepo)
  val bindingFuture = Http().bindAndHandle(wardrobeController.route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

  /*val wardrobeManagementRepo = WardrobeManagementRepository

//  val create = wardrobeManagementRepo.create
//  Thread.sleep(10000)

  println("***************************************************")
  println("*************Reading from csv file*****************")
  println("***************************************************")

  val bufferedSource = io.Source.fromFile("src/main/resources/clothing.csv")
  for (line <- bufferedSource.getLines()) {
    val columns = line.split(",").map(_.trim)
    println(s"${columns(0)} -- ${columns(1)}")
//    wardrobeManagementRepo.insert(Clothing(columns(0), columns(1)))
  }

  Thread.sleep(10000)
//  val insertResult = wardrobeManagementRepo.insert(Clothing("iSwim Summer Bikini", " Bikinis"))
//  insertResult.foreach(i => println(s"---------->>>> $i"))
//  Thread.sleep(10000)

  println("********** Searching cloth by name ***************")
  wardrobeManagementRepo.searchCloth("iSwim Summer Bikini").foreach(println)
  Thread.sleep(5000)
//  println("***********************************************************")
//  println("***********************************************************")
//
//  val result = wardrobeManagementRepo.listClothing
//  result.foreach(i => println(s"*************** $i"))
//
//  Thread.sleep(10000)
*/
}
