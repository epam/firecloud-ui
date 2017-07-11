import sbt._

object Dependencies {
//  val sprayV = "1.3.3"
  val jacksonV = "2.8.4"
  val akkaV = "2.4.17"
  val akkaHttpV = "10.0.5"

  val rootDependencies = Seq(
    // proactively pull in latest versions of Jackson libs, instead of relying on the versions
    // specified as transitive dependencies, due to OWASP DependencyCheck warnings for earlier versions.
    "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonV,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonV,
    "com.fasterxml.jackson.core" % "jackson-core" % jacksonV,
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % jacksonV,
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.google.apis" % "google-api-services-oauth2" % "v1-rev112-1.20.0",
    "com.google.api-client" % "google-api-client" % "1.22.0" exclude("com.google.guava", "guava-jdk5"),
//    "com.getsentry.raven" % "raven-logback" % "7.8.6",
//    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
//    "org.broadinstitute.dsde.vault" %%  "vault-common"  % "0.1-19-ca8b927",
//    "org.broadinstitute.dsde" %%  "rawls-model"  % "0.1-518a712-SNAP"
//      exclude("com.typesafe.scala-logging", "scala-logging_2.11"),
//    "io.spray"            %%  "spray-can"     % sprayV,
//    "io.spray"            %%  "spray-routing" % sprayV,
//    "io.spray"            %%  "spray-json"    % "1.3.2",
//    "io.spray"            %%  "spray-client"  % sprayV,
//    "io.spray"            %%  "spray-testkit" % sprayV    % "test",
//    "io.spray"            %%  "spray-http"    % sprayV,
    "org.webjars"           %  "swagger-ui"    % "2.2.5",
//    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
//    "com.typesafe.akka"   %%  "akka-contrib"  % akkaV,
    "com.typesafe.akka"   %%  "akka-http-core"     % akkaHttpV,
    "com.typesafe.akka"   %%  "akka-stream-testkit" % "2.4.11",
    "com.typesafe.akka"   %%  "akka-http"           % akkaHttpV,
    "com.typesafe.akka"   %%  "akka-testkit"        % akkaV     % "test",
    "com.typesafe.akka"   %%  "akka-slf4j"          % akkaV,
//    "com.typesafe.akka"   %%  "akka-http-spray-json" % "10.0.6",
  //    "org.elasticsearch"    % "elasticsearch"  % "2.4.1",
//    ("com.google.org.broadinstitute.dsde.firecloud.api-client" % "google-org.broadinstitute.dsde.firecloud.api-client" % "1.22.0").exclude("com.google.guava", "guava-jdk5"),
//    "com.google.apis" % "google-org.broadinstitute.dsde.firecloud.api-services-storage" % "v1-rev58-1.21.0",
//    "com.google.apis" % "google-org.broadinstitute.dsde.firecloud.api-services-compute" % "v1-rev120-1.22.0",
//    "com.jason-goodwin"   %% "authentikat-jwt" % "0.4.1",
//    "com.sun.mail"         % "javax.mail" % "1.5.6",
//    "org.ocpsoft.prettytime" % "prettytime" % "4.0.1.Final",
//    "org.everit.json"      %  "org.everit.json.schema" % "1.4.1",
    "org.specs2"          %%  "specs2-core"   % "3.7"  % "test",
    "org.scalatest"       %%  "scalatest"     % "2.2.6"   % "test",
    "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
//    "org.mock-server"      %  "mockserver-netty" % "3.10.2" % "test"
  )
}