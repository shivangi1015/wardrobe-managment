package database

import slick.jdbc.JdbcProfile

trait DbComponent {

  val driver: JdbcProfile

  import driver.api.Database
  val db: Database

}
