package database

import slick.jdbc.PostgresProfile

trait PostgresDbComponent extends DbComponent {

  val driver = PostgresProfile
  import driver.api.Database
  val db: driver.backend.DatabaseDef = Database.forConfig("myPostgresDB")
  println(s"DB:::::::::::::: $db")
}
