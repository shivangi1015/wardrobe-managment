package repository

import database.{DbComponent, PostgresDbComponent}
import model.Clothing

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object WardrobeManagementRepository extends WardrobeReadRepository with WardrobeReadRepositoryImpl with ClothingTable with DbComponent with PostgresDbComponent

trait WardrobeReadRepository {
  def listClothing: Future[Vector[String]]
  def create: Future[Int]
}

trait WardrobeReadRepositoryImpl extends WardrobeReadRepository {
  this: DbComponent with ClothingTable =>
   import driver.api._

    def listClothing: Future[Vector[String]] = {
      println("Running the method!!!")

      db.run(sql"""select name from clothing where id = 1;""".as[String])
     /* val l = db.run(wardrobeTableQuery.to[List].result)
      l.onComplete {
        case Success(value) => println("-------")
          println(value)
        case Failure(exception) => println("1111111111")
          println(exception)
      }
      println("================")*/
    }

  /*def insert(clothing: Clothing): Future[Int] = {
    println("inserting.....")
    db.run(wardrobeTableQuery += clothing)
  }*/

  def create: Future[Int] = {
    val createQuery: DBIO[Int] = sqlu"""create table "Player"(
"player_id" bigserial primary key,
"name" varchar not null,
"country" varchar not null,
"dob" date
) """
    db.run(createQuery)
  }


}
trait ClothingTable {
  this: DbComponent =>

  import driver.api._

  val wardrobeTableQuery: TableQuery[ClothingSlickMapping] = TableQuery[ClothingSlickMapping]
  class ClothingSlickMapping(tag: Tag) extends Table[Clothing](tag, "clothing") {
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val name: Rep[String] = column[String]("name", O.Unique)
    val category: Rep[String] = column[String]("category", O.Unique)
    override def * = (id, name, category) <> (Clothing.tupled, Clothing.unapply)
  }
}

