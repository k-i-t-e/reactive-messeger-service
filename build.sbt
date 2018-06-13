name := "reactive-messenger"

version := "1.0"

lazy val `reactive_messenger` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
resolvers += Resolver.jcenterRepo

scalaVersion := "2.11.7"

val reactiveMongoVer = "0.13.0-play26"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  filters,
  "org.reactivemongo" %% "play2-reactivemongo" % reactiveMongoVer,
  guice
)

libraryDependencies += "net.codingwell" %% "scala-guice" % "4.1.0"

libraryDependencies += "com.iheart" %% "ficus" % "1.4.3" // config lib, used by Silhouette,

// Silhouette config
val silhouetteVer = "5.0.0"
lazy val silhouetteLib = Seq(
  "com.mohiva" %% "play-silhouette" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-persistence" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-testkit" % silhouetteVer % "test"
)

libraryDependencies ++= silhouetteLib

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

PlayKeys.externalizeResources := false