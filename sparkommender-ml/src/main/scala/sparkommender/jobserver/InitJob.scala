package sparkommender.jobserver

import com.typesafe.config.Config
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import spark.jobserver._

object InitJob extends SparkSqlJob {

  val path = "/sparkommender/models/"
  val cfTable = "cf"
  val mixedTable = "mixed"
  val topPerDestTable = "top_per_dest"
  val topGeneralTable = "top_general"

  override def runJob(sqlContext: SQLContext, config: Config): Any = {

    val cfDf = sqlContext.read.load(path + "cfDf")
    cfDf.registerTempTable(cfTable)
    val cf = sqlContext.sql("SELECT COUNT(*) FROM " + cfTable).first()

    val mixedModelKnownUsersDf = sqlContext.read.load(path + "mixedModelKnownUsersDf")
    mixedModelKnownUsersDf.repartition(mixedModelKnownUsersDf("srch_destination_id"))
      .persist(StorageLevel.MEMORY_ONLY).registerTempTable(mixedTable)
    //sqlContext.cacheTable(mixedTable)
    val mm = sqlContext.sql("SELECT COUNT(*) FROM " + mixedTable).first()

    val topPerDestDf = sqlContext.read.load(path + "topPerDestDf")
    topPerDestDf.repartition(topPerDestDf("srch_destination_id"))
      .persist(StorageLevel.MEMORY_ONLY).registerTempTable(topPerDestTable)
    sqlContext.cacheTable(topPerDestTable)
    val tpd = sqlContext.sql("SELECT COUNT(*) FROM " + topPerDestTable).first()


    val topClustersDf = sqlContext.read.load(path + "topClustersDf")
    topClustersDf.registerTempTable(topGeneralTable)
    sqlContext.cacheTable(topGeneralTable)
    val tg = sqlContext.sql("SELECT COUNT(*) FROM " + topGeneralTable).first()

    cf.toString() + mm.toString() + tpd.toString() + tg.toString()
  }

  override def validate(sqlContext: SQLContext, config: Config): SparkJobValidation = {
    SparkJobValid
  }
}

//POST to localhost:8090/contexts/sparkommender-context?num-cpu-cores=4&memory-per-node=2g



//?? TODO
//test on local spark [8] because it's 1.6.1 and not old 1.5.2

///aslo could try repartition by column ($user_id)


//RDD cache and replicate 8 times in memory

//if inside of docker- use coarse grain as it is mesos