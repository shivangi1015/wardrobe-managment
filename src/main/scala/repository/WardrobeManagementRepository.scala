package repository

import database.{DbComponent, PostgresDbComponent}
import model.Clothing

import scala.concurrent.Future

object WardrobeManagementRepository extends WardrobeManagementRepository with WardrobeReadRepositoryImpl with
  WardrobeWriteRepositoryImpl with DbComponent with ClothingTable with PostgresDbComponent

trait WardrobeManagementRepository extends WardrobeReadRepository with WardrobeWriteRepository

trait WardrobeReadRepository {
  def listClothing: Future[List[Clothing]]
  def searchCloth(name: String): Future[Option[Clothing]]
}

trait WardrobeReadRepositoryImpl extends WardrobeReadRepository {
  this: DbComponent with ClothingTable =>

  import driver.api._

  def listClothing: Future[List[Clothing]] = db.run(wardrobeTableQuery.to[List].result)

  def searchCloth(name: String): Future[Option[Clothing]] =
    db.run(wardrobeTableQuery.filter(_.name === name).result.headOption)

}

trait WardrobeWriteRepository {
  def insert(clothing: Clothing): Future[Int]
}

  trait WardrobeWriteRepositoryImpl extends WardrobeWriteRepository {
    this: DbComponent with ClothingTable =>
    import driver.api._

    def insert(clothing: Clothing): Future[Int] = db.run(wardrobeTableQuery += clothing)
    def create: Future[Unit] = db.run(wardrobeTableQuery.schema.create)
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

