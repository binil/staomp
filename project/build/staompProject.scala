import sbt._

class staompProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {
  val sc = "org.scala-tools.testing" % "scalacheck_2.8.1" % "1.8" withSources()
}