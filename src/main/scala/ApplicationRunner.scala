import model.Clothing
import repository.WardrobeManagementRepository

import scala.concurrent.ExecutionContext.Implicits.global
object ApplicationRunner extends App {

  val wardrobeManagementRepo = WardrobeManagementRepository

  val insertResult = wardrobeManagementRepo.insert(Clothing(1, "a", "b"))
  insertResult.foreach(i => println("---------->>>> $i"))
  Thread.sleep(10000)

  val result = wardrobeManagementRepo.listClothing
  result.foreach(i => println(s"*************** $i"))

  Thread.sleep(10000)

  val create = wardrobeManagementRepo.create
  create.foreach(i => println(s"-------->>> ${i}"))
  Thread.sleep(10000)
}
