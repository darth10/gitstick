import AssemblyKeys._

name := "gitstick"

version := "0.5.1"

scalaVersion := "2.9.1"

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % "2.0.3",
  "org.scalatra" %% "scalatra-scalate" % "2.0.3",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "1.1.0.201109151100-r", 
  "org.scalatra" %% "scalatra-specs2" % "2.0.4" % "test",
  "junit" % "junit" % "4.8.1" % "test",
  "org.scalatra" %% "scalatra-auth" % "2.0.4",
  "net.liftweb" % "lift-json_2.9.1" % "2.4-M5",
  "ch.qos.logback" % "logback-classic" % "1.0.3",
  "org.eclipse.jetty" % "jetty-webapp" % "7.6.0.v20120127" % "container; compile",
  "javax.servlet" % "servlet-api" % "2.5" % "provided"
)

seq(webSettings: _*)

seq(assemblySettings: _*)

jarName in assembly := "gitstick.jar"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "JGit Repository" at "http://download.eclipse.org/jgit/maven"
)

resourceGenerators in Compile <+= (resourceManaged, baseDirectory) map { (managedBase, base) => 
  val webappBase = base / "src" / "main" / "webapp" 
  for { 
    (from, to) <- webappBase ** "*" x rebase(webappBase, managedBase / "main" / "webapp") 
  } yield { 
    Sync.copy(from, to) 
    to 
  } 
}

