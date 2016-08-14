package sparkommender.ml

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SQLContext}

object MixedModel {

  def main(args: Array[String]): Unit = {

    val csv = "com.databricks.spark.csv"
    val csvOptions = Map("delimiter" -> ",", "header" -> "true", "inferSchema" -> "false")

    val sparkConf = new SparkConf().setAppName("Sparkommender-MixedModel")
    //Here point to your spark cluster
    .setMaster("spark://your-spark-master:7077")

    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)

    val trainFile = "data/train.csv"
    val trainDf = sqlContext.read.format(csv).options(csvOptions).load(trainFile)

    val topPerDestinationDf = getTopClustersPerDestination(trainDf, sqlContext)

    val pastHotelClustersDf = getPreviousYearsHotelClusters(trainDf, sqlContext)

    val topClustersDf = getTopClusters(trainDf, sc, sqlContext)

    //save for use in Spark-job-server
    //topClustersDf.coalesce(1).write.save("spark-job-server-docker/models/topClustersDf")

    //save for use outside of spark
    topClustersDf.coalesce(1).write.format(csv).save("../sparkommender-service/models/top-general")

    val topClusters: String = topClustersDf.select("top_clusters").first().getString(0)

    val mergeModelsForKnownUsers = udf((past: String, topDest: String, top: String) => {
      def toList(s: String) = {
        if (s != null && !s.equals("null")) s.split(" ").toList else List()
      }
      val topList = toList(top)
      val pastList = toList(past)
      val topDestList = toList(topDest)
      (pastList ++ topDestList ++ topList).distinct.take(5).mkString(" ")
    })

    val mergeModelsForNewUsers = udf((topDest: String, top: String) => {
      def toList(s: String) = {
        if (s != null && !s.equals("null")) s.split(" ").toList else List()
      }
      val topList = toList(top)
      val topDestList = toList(topDest)
      (topDestList ++ topList).distinct.take(5).mkString(" ")
    })

    val mixedModelForKnownUsersDf = trainDf.select("user_id","srch_destination_id").distinct
      .join(topPerDestinationDf, Seq("srch_destination_id"))
      .join(pastHotelClustersDf, Seq("user_id","srch_destination_id"))
      .withColumn("top_clusters", lit(topClusters))

    val knownUsersDf = mixedModelForKnownUsersDf.withColumn("hotel_cluster_prediction",
      mergeModelsForKnownUsers(mixedModelForKnownUsersDf("hotel_cluster_past"),
        mixedModelForKnownUsersDf("hotel_cluster_top_per_dest"),
        mixedModelForKnownUsersDf("top_clusters")))

    //save for use in Spark-job-server
    //knownUsersDf.write.save("spark-job-server-docker/models/mixedModelKnownUsersDf")

    //save for use outside of spark
    knownUsersDf.select("user_id","srch_destination_id","hotel_cluster_prediction")
      .coalesce(1).write.format(csv).save("../sparkommender-service/models/mixed-model")

    val modelForNewUsersDf = trainDf.select("srch_destination_id").distinct
      .join(topPerDestinationDf, Seq("srch_destination_id"))
      .withColumn("top_clusters", lit(topClusters))

    val newUsersDf = modelForNewUsersDf.withColumn("hotel_cluster_prediction", mergeModelsForNewUsers(
        modelForNewUsersDf("hotel_cluster_top_per_dest"),
        modelForNewUsersDf("top_clusters")))

    //save for use in Spark-job-server
    //newUsersDf.write.save("spark-job-server-docker/models/topPerDestDf")

    //save for use outside of spark
    newUsersDf.select("srch_destination_id","hotel_cluster_prediction")
      .coalesce(1).write.format(csv).save("../sparkommender-service/models/top-per-dest")
  }

  def getTopClustersPerDestination(dfTrain: DataFrame, sqlContext: SQLContext, top: Int=5): DataFrame = {
    val ordered = dfTrain.select("srch_destination_id", "hotel_cluster", "is_booking")
      .groupBy("srch_destination_id", "hotel_cluster").agg(sum("is_booking").as("s"), count("is_booking").as("c"))
    val merge = udf((sum: Long, count: Long) => {
      sum * 0.85 + count * 0.15
    })
    val agg = ordered.withColumn("counts", merge(ordered("s"), ordered("c")))
      .select("srch_destination_id", "hotel_cluster", "counts")
    val sorted = agg.rdd.map(r => (r.getString(0), (r.getDouble(2), r.getString(1))))
      .groupByKey().mapValues(x =>
      x.toList.sortWith(_._1 > _._1).take(top).map(z => z._2).mkString(" "))
    sqlContext.createDataFrame(sorted.map(x => Row.fromTuple(x)), StructType(
      Seq(StructField("srch_destination_id", StringType, false),
        StructField("hotel_cluster_top_per_dest", StringType, false))))
  }

  def getPreviousYearsHotelClusters(dfTrain: DataFrame, sqlContext: SQLContext, top: Int=5): DataFrame = {
    val previousYearsRdd = dfTrain.select("user_id", "srch_destination_id", "hotel_cluster", "is_booking")
      .groupBy("user_id", "srch_destination_id", "hotel_cluster")
      .agg(sum("is_booking").as("s"), count("is_booking").as("c"))

    val merge = udf((sum: Long, count: Long) => {
      sum * 0.9 + count * 0.6
    })

    val agg = previousYearsRdd.withColumn("counts", merge(previousYearsRdd("s"), previousYearsRdd("c")))
      .select("user_id", "srch_destination_id", "hotel_cluster", "counts")

    val sorted = agg.rdd.map(r => ((r.getString(0), r.getString(1)), (r.getString(2), r.getDouble(3)))).groupByKey()
      .mapValues(k => k.toList.sortBy(_._2)(Ordering.Double.reverse).take(top).map(x => x._1).mkString(" "))
      .map(x => (x._1._1, x._1._2, x._2))

    sqlContext.createDataFrame(sorted.map(x => Row.fromTuple(x)), StructType(
      Seq(StructField("user_id", StringType, false), StructField("srch_destination_id", StringType, false),
        StructField("hotel_cluster_past", StringType, false))))
  }

  def getTopClusters(dfTrain: DataFrame, sc: SparkContext, sqlContext: SQLContext, top: Int=5): DataFrame = {
    val ordered = dfTrain.select("hotel_cluster", "is_booking")
      .groupBy("hotel_cluster").agg(sum("is_booking").as("s"), count("is_booking").as("c"))
    val merge = udf((sum: Long, count: Long) => {
      sum * 0.85 + count * 0.15
    })
    val agg = ordered.withColumn("counts", merge(ordered("s"), ordered("c")))

    val topRow = Row(agg.select("hotel_cluster", "counts").orderBy(agg("counts").desc)
      .take(top).map(r => r.getString(0)).toList.mkString(" "))

    sqlContext.createDataFrame(
      sc.parallelize(Seq(topRow), 1),
      StructType(Seq(StructField("top_clusters", StringType, false))))
  }
}
