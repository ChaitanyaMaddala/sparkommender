package sparkommender.jobserver

import com.typesafe.config.Config
import org.apache.spark.sql.{DataFrame, SQLContext}
import spark.jobserver.{SparkJobValidation, _}
import sparkommender.jobserver.InitJob._

object RecommendJob extends SparkSqlJob {

  val mixed = "Mixed"
  val cf = "CF"
  val topPerDestination = "TopPerDestination"

//  val cachedDf = new collection.mutable.HashMap[String, DataFrame]

  override def runJob(sqlContext: SQLContext, config: Config): Any = {
    val model = config.getString("model")
    val searchId = config.getString("srch_destination_id")

    //cache Spark SQL plan???
    //query is different every time
    //https://www.youtube.com/watch?v=nAX53vQy9AQ&feature=youtu.be
//    def getCachedDf(query: String): DataFrame = {
//      cachedDf.getOrElseUpdate(query, sqlContext.sql(query))
//    }

    def getTop(searchId: String) = {
      val topPerDestDf = sqlContext.table(topPerDestTable).cache()
      val topGeneral = sqlContext.table(topGeneralTable).cache()
      val matched =
//        getCachedDf(s"SELECT hotel_cluster_prediction FROM ${topPerDestTable} WHERE srch_destination_id = ${searchId}")
        topPerDestDf.where(s"srch_destination_id = '${searchId}'").select("hotel_cluster_prediction")
      if(matched.count() == 1) {
        matched.first().getString(0)
      } else {
        topGeneral.first().getString(0)
      }
    }

    if(config.hasPath("user_id")) {
      val userId = config.getString("user_id")
      if(model.equals(mixed)) {
        val mixedDf = sqlContext.table(mixedTable).cache()
        val matchedMixed =
     //     getCachedDf(s"SELECT hotel_cluster_prediction FROM ${mixedTable} " +
     //     s"WHERE srch_destination_id = ${searchId} AND user_id = ${userId}")
          mixedDf.where(s"user_id = '${userId}' AND srch_destination_id = '${searchId}'")
            .select("hotel_cluster_prediction")
        if(matchedMixed.count() == 1) {
          matchedMixed.first().getString(0)
        } else {
          getTop(searchId)
        }
      } else if(model.equals(topPerDestination)) {
        val topPerDestDf = sqlContext.table(topPerDestTable).cache()
        topPerDestDf.where("srch_destination_id = '${searchId}'").first().getString(0)
      } else {
        val cfDF = sqlContext.table(cfTable).cache()
        val matchedCf = cfDF.where(s"user_id = '${userId}'").select("hotel_cluster")
        if(matchedCf.count() == 1) {
          matchedCf.first().getString(0)
        } else {
          getTop(searchId)
        }
      }
    } else {
      getTop(searchId)
    }
  }

  override def validate(sqlContext: SQLContext, config: Config): SparkJobValidation = {
    SparkJobValid
    //for performance reasons don't validate

//    if(!config.hasPath("model")) SparkJobInvalid("Missing parameter 'model'")
//    else if(!config.hasPath("srch_destination_id")) SparkJobInvalid("Missing parameter 'srch_destination_id'")
//    else if(config.hasPath("user_id")) {
//      val userId = config.getString("user_id")
//      try {
//        userId.toInt
//        SparkJobValid
//      } catch {
//        case e: Exception => SparkJobInvalid("Expected integer value for 'user_id' or nothing at all")
//      }
//    } else {
//      val model = config.getString("model")
//      if(model.equals(mixed) || model.equals(cf)) SparkJobValid
//      else SparkJobInvalid("Unrecognised model")
//    }
  }
}
