enablePlugins(GatlingPlugin)

name := "sparkommender-ml"

version := "1.0.0"

scalaVersion := "2.11.8"

resolvers += "Job Server Bintray" at "https://dl.bintray.com/spark-jobserver/maven"

val sparkVersion = "1.6.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core"        % sparkVersion ,//% "provided",
  "org.apache.spark" %% "spark-sql"         % sparkVersion ,//% "provided",
  "org.apache.spark" %% "spark-mllib"       % sparkVersion ,//% "provided",
  "com.databricks"   %% "spark-csv"         % "1.4.0"      ,//% "provided",
  "spark.jobserver"  %% "job-server-api"    % "0.6.2"      ,//% "provided",
  "spark.jobserver"  %% "job-server-extras" % "0.6.2"      ,//% "provided",

  //for accessing files in S3
  "org.apache.hadoop" % "hadoop-common" % "2.4.0" % "provided" excludeAll ExclusionRule(organization = "javax.servlet")
)

//For Performance Tests
libraryDependencies ++= Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.0" % "test",
  "io.gatling"            % "gatling-test-framework"    % "2.2.0" % "test"
)

assemblyJarName in assembly := "sparkommender.jar"