package repository

import database.{DbComponent, PostgresDbComponent}
import model.Clothing
import slick.dbio.DBIO

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object WardrobeManagementRepository extends WardrobeManagementRepository with WardrobeReadRepositoryImpl with
  WardrobeWriteRepositoryImpl with ClothingTable with DbComponent with PostgresDbComponent

trait WardrobeManagementRepository extends WardrobeReadRepository with WardrobeWriteRepository

trait WardrobeReadRepository {
  def listClothing: Future[List[Clothing]]
  def searchCloth(name: String): Future[Option[Clothing]]
}

trait WardrobeWriteRepository {
  def create: Future[Int]
  def insert(clothing: Clothing): Future[Int]
}

trait WardrobeReadRepositoryImpl extends WardrobeReadRepository {
  this: DbComponent with ClothingTable =>
   import driver.api._

    def listClothing: Future[List[Clothing]] = {
      println("Running the method!!!")

      val l = db.run(wardrobeTableQuery.to[List].result)
      l.onComplete {
        case Success(value) => println("-------")
          println(value)
        case Failure(exception) => println("1111111111")
          println(exception)
      }
      println("================")
      l
    }

  def searchCloth(name: String): Future[Option[Clothing]] ={
   db.run(wardrobeTableQuery.filter(_.name === name).result.headOption)
  }

}

trait WardrobeWriteRepositoryImpl extends WardrobeWriteRepository {

  this: DbComponent with ClothingTable =>
  import driver.api._

  def insert(clothing: Clothing): Future[Int] = {
    println("inserting in clothing table!")
    db.run(wardrobeTableQuery += clothing)
  }

  def create: Future[Int] = {
    println("Creating table clothing table!")
    val createQuery =
      sqlu"""create table "clothing"("name" varchar primary key, "category" varchar not null) """
    db.run(createQuery)
  }
}

trait ClothingTable {
  this: DbComponent =>

  import driver.api._

  val wardrobeTableQuery: TableQuery[ClothingSlickMapping] = TableQuery[ClothingSlickMapping]
  class ClothingSlickMapping(tag: Tag) extends Table[Clothing](tag, "clothing") {
    val name: Rep[String] = column[String]("name", O.PrimaryKey,O.Unique)
    val category: Rep[String] = column[String]("category", O.Unique)
    override def * = (name, category) <> (Clothing.tupled, Clothing.unapply)
  }
}

