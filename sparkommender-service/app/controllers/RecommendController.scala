package controllers

import javax.inject._

import play.api.mvc._
import services.RecommendService

@Singleton
class RecommendController extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def recommend(model: String, destination: Int, user: Option[Int]) = Action {
    val allowOriginHeader = ("Access-Control-Allow-Origin", "*")
    val modelLC = model.toLowerCase
    if(!RecommendService.validModels.contains(modelLC)){
      BadRequest(s"Unrecognized model: $model").withHeaders(allowOriginHeader)
    } else {
      Ok(RecommendService.recommend(modelLC, destination, user)).withHeaders(allowOriginHeader)
    }
  }

}
