import sbt._


class MavenJarClassLoader(info: ProjectInfo) extends DefaultProject(info) {
  val paxURLMVN = "org.ops4j.pax.url" % "pax-url-mvn" % "1.3.4"
}
