name := "playlesson"

version := "1.0"

lazy val `playlesson` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

val reactiveMongoVer = "0.11.14"

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test,
  javaJpa, filters,
  "org.hibernate" % "hibernate-entitymanager" % "5.1.0.Final")

libraryDependencies += "org.postgresql" % "postgresql" % "42.1.1"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

PlayKeys.externalizeResources := false