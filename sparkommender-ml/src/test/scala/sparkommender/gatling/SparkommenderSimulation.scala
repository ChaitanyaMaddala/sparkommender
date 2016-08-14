package sparkommender.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class SparkommenderSimulation extends Simulation {

  val httpConf = http
    //play-web-app - ibm bluemix containers service
    .baseURL("http://sparkommender.com")
    .acceptHeader("application/json")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val testDataFeeder = csv("data/test.csv").random
  val model = "mixed-model"

  val scn = scenario("SparkommenderSimulation").feed(testDataFeeder).repeat(1) {
    exec(http("request")
      //when running in spark-job-server
//      .post("/jobs?context=sparkommender-context&appName=sparkommender&classPath=sparkommender.jobserver.RecommendJob&sync=true")
//      .body(StringBody("""user_id=${user_id},srch_destination_id=${srch_destination_id},model=""" + model))

      //when running in play web app
      .get("/api/models/" + model + """/destinations/${srch_destination_id}?user=${user_id}""")
    )
  }

  setUp(
    scn.inject(rampUsers(1000000) over (10000 seconds))
  ).protocols(httpConf)


}
