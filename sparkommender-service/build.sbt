name := """sparkommender-service"""

version := "1.0.4"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


//Docker stuff
packageName in Docker := "radek1st/sparkommender"

dockerBaseImage := "radek1st/java8-scala-sbt"

maintainer := "Radek Ostrowski radek@fastdata.com"

dockerExposedPorts in Docker := Seq(80)

daemonUser in Docker := "root"

mappings in Universal += file("models/mixed-model/part-00000") -> "models/mixed-model.csv"

mappings in Universal += file("models/cf-model/part-00000") -> "models/cf-model.csv"

mappings in Universal += file("models/top-general/part-00000") -> "models/top-general.csv"

mappings in Universal += file("models/top-per-dest/part-00000") -> "models/top-per-dest.csv"
