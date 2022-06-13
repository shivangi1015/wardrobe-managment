package model

case class Clothing(name: String, category: String)

case class WardrobeResponse(listOfClothing: List[Clothing])
