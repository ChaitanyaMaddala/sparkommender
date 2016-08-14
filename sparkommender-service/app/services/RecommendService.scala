package services

import scala.io.Source

object RecommendService {

  val mixedModelName = "mixed-model"
  val cfModelName = "cf-model"
  val validModels = List(mixedModelName, cfModelName)

  def loadMixedModel(): Map[String, String] = {
    Source.fromFile("models/mixed-model.csv").getLines()
      .map(line => line.split(","))
      .map(s =>(s"${s(0)}-${s(1)}", s(2))).toMap
  }

  def loadCfModel(): Map[String, String] = {
    Source.fromFile("models/cf-model.csv").getLines()
      .map(line => line.split(","))
      .map(s =>(s(0), s(1))).toMap
  }

  def loadTopPerDestModel(): Map[String, String] = {
    Source.fromFile("models/top-per-dest.csv").getLines()
      .map(line => line.split(","))
      .map(s =>(s(0), s(1))).toMap
  }

  def loadTopGeneral(): String = {
    Source.fromFile("models/top-general.csv").getLines().next()
  }

  val mixedModel = loadMixedModel()
  val topPerDestModel = loadTopPerDestModel()
  val topGeneral = loadTopGeneral()
  val cfModel = loadCfModel()

  def recommend(model: String, destination: Int, user: Option[Int]): String = {

    def recommendForDest(destination: String): String = {
      topPerDestModel.getOrElse(destination, topGeneral)
    }

    lazy val perDestination = recommendForDest(destination.toString)

    if(user.isDefined) {
      if(model.equals(mixedModelName)){
        mixedModel.getOrElse(s"${user.get}-${destination}", perDestination)
      } else if(model.equals(cfModelName)) {
        cfModel.getOrElse(user.get.toString, perDestination)
      } else {
        perDestination
      }
    } else {
      perDestination
    }
  }
}
