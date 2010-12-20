import reaktor.scct.ScctProject
import sbt._

class staompProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject with ScctProject {
  val sc = "org.scala-tools.testing" % "scalacheck_2.8.1" % "1.8" withSources()
  // val st = "org.scalatest" % "scalatest" % "1.2" withSources()


  val scalaToolsSnapshots = ScalaToolsSnapshots
  val scalatest = "org.scalatest" % "scalatest" % "1.2.1-SNAPSHOT" withSources()
}