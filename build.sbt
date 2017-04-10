version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.21",
  "net.liftweb" %% "lift-json" % "2.5.1",
  "ws.securesocial" %% "securesocial" % "2.1.4"
)     

play.Project.playScalaSettings
