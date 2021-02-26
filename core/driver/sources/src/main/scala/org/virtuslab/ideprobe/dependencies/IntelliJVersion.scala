package org.virtuslab.ideprobe.dependencies

import org.virtuslab.ideprobe.BuildInfo
import pureconfig.ConfigConvert
import pureconfig.generic.semiauto.deriveConvert

final case class IntelliJVersion(build: String, release: Option[String]) {
  def major: Option[String] = {
    release.map(_.split("\\.").take(2).mkString("."))
  }

  def inferredMajor: String = {
    major.getOrElse {
      val firstDigits = build.split('.')(0)
      val year = firstDigits.take(2)
      val version = firstDigits.drop(2)
      s"20$year.$version"
    }
  }
}

object IntelliJVersion {
  implicit val configConvert: ConfigConvert[IntelliJVersion] = deriveConvert[IntelliJVersion]

  val Latest = IntelliJVersion.release("2020.3.2", "203.7148.57")

  def snapshot(build: String): IntelliJVersion = IntelliJVersion(build, None)
  def release(version: String, build: String): IntelliJVersion = IntelliJVersion(build, Some(version))
}
