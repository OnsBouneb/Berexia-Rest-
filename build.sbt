
name := "play_rest"
 
version := "1.0" 
      
lazy val `play_rest` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.11.11"


dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.7"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice,
  "org.webjars" % "bootstrap" % "3.3.2",
  "org.webjars" % "metisMenu" % "1.1.3",
  "org.webjars" % "morrisjs" % "0.5.1",
  "org.webjars" % "font-awesome" % "4.3.0",
  "org.webjars" % "jquery" % "2.1.3",
  "org.webjars" % "flot" % "0.8.3",
  "org.webjars" % "datatables" % "1.10.5",
  "org.webjars" % "datatables-plugins" % "1.10.5",
  "com.newrelic.agent.java" % "newrelic-agent" % "3.14.0",
  "com.newrelic.agent.java" % "newrelic-api" % "3.14.0",
  "org.apache.spark" %% "spark-core" % "2.1.0",
  "org.apache.spark" %% "spark-sql" % "2.1.0",
  "org.apache.spark" %%"spark-streaming" % "2.1.0",
  "org.apache.spark" %% "spark-mllib" % "2.1.0",
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.6",
  "org.vegas-viz" %% "vegas" % "0.3.11",
  "org.vegas-viz" %% "vegas-spark" % "0.3.11",
  "org.apache.hadoop" % "hadoop-common" % "2.7.6",
  "org.apache.hadoop" % "hadoop-client" % "2.7.6",
  "org.apache.hadoop" % "hadoop-hdfs" % "2.7.6",
  "com.knockdata" % "spark-highcharts" % "0.6.4",
  "junit" % "junit" % "4.8" % Test,
  "com.crealytics" %% "spark-excel" % "0.9.0")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
libraryDependencies += filters


      