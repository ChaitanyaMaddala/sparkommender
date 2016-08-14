package sparkommender.ml

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}

object SimpleCollaborativeFilteringModel {

  def main(args: Array[String]): Unit = {
    val csv = "com.databricks.spark.csv"
    val csvOptions = Map("delimiter" -> ",", "header" -> "true", "inferSchema" -> "false")

    val sparkConf = new SparkConf().setAppName("Sparkommender-CF")
    //Here point to your spark cluster
    .setMaster("spark://your-spark-master:7077")

    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)

    val trainFile = "data/train.csv"
    val trainDf = sqlContext.read.format(csv).options(csvOptions).load(trainFile)

    val modelFileName = "spark-job-server-docker/models/simpleCfModel"

    //uncomment if the model doesn't exist
    //buildCFModel(modelFileName, trainDf, sc)

    val modelCf = MatrixFactorizationModel.load(sc, modelFileName)
    val cfDf = getRecommendationsCF(modelCf, sqlContext)

    //save for use in Spark-job-server
    //cfDf.write.save("spark-job-server-docker/models/cfDf")

    //save for use outside of spark
    cfDf.select("user_id", "hotel_cluster")
      .coalesce(1).write.format(csv).save("../sparkommender-service/models/cf-model")
  }

  def scoreProductsPerUser(dfTrain: DataFrame) = {
    val previousYearsDf = dfTrain.select("user_id", "hotel_cluster", "is_booking")
      .groupBy("user_id", "hotel_cluster").agg(sum("is_booking").as("s"), count("is_booking").as("c"))

    val merge = udf((sum: Long, count: Long) => {
      sum + count
    })

    previousYearsDf.withColumn("counts", merge(previousYearsDf("s"), previousYearsDf("c")))
      .select("user_id", "hotel_cluster", "counts")
  }

  def buildCFModel(fileName: String, train: DataFrame, sc: SparkContext) = {
    val ratings = scoreProductsPerUser(train).map(r => Rating(r.getString(0).toInt, r.getString(1).toInt, r.getLong(2).toDouble))
    val rank = 10
    val numIterations = 10
    val lambda = 0.01
    val blocks = -1
    val alpha = 1.0
    val seed = 42L
    val model = ALS.trainImplicit(ratings, rank, numIterations, lambda, blocks, alpha, seed)
    model.save(sc, fileName)
  }

  def getRecommendationsCF(modelCf: MatrixFactorizationModel, sqlContext: SQLContext) = {
    val recommendedProducts = modelCf.recommendProductsForUsers(5)
      .map(r => (r._1.toString, r._2.map(x => x.product).mkString(" ")))

    sqlContext.createDataFrame(recommendedProducts.map(x => Row.fromTuple(x)), StructType(
      Seq(StructField("user_id", StringType, false), StructField("hotel_cluster", StringType, false))))
  }

}

