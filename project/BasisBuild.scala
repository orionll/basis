/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis

import sbt._
import sbt.Keys._
import Defaults.defaultSettings

object BasisBuild extends Build {
  lazy val all = Project(
    id           = "all",
    base         = file("."),
    settings     = basicSettings,
    dependencies =
      Seq(Basis,
          BasisData,
          BasisCollection,
          BasisContainer,
          BasisText),
    aggregate    =
      Seq(Basis,
          BasisData,
          BasisCollection,
          BasisContainer,
          BasisText)
  )
  
  lazy val Basis = Project(
    id           = "basis",
    base         = file("basis"),
    settings     = commonSettings
  )
  
  lazy val BasisData = Project(
    id           = "basis-data",
    base         = file("basis-data"),
    settings     = commonSettings
  )
  
  lazy val BasisCollection = Project(
    id           = "basis-collection",
    base         = file("basis-collection"),
    settings     = commonSettings,
    dependencies = Seq(Basis)
  )
  
  lazy val BasisContainer = Project(
    id           = "basis-container",
    base         = file("basis-container"),
    settings     = commonSettings,
    dependencies = Seq(Basis, BasisData, BasisCollection)
  )
  
  lazy val BasisText = Project(
    id           = "basis-text",
    base         = file("basis-text"),
    settings     = commonSettings,
    dependencies = Seq(Basis)
  )
  
  lazy val basicSettings =
    defaultSettings ++
    Unidoc.settings ++
    projectSettings ++
    scalaSettings   ++
    docSettings
  
  lazy val commonSettings =
    basicSettings   ++
    compileSettings ++
    publishSettings
  
  lazy val projectSettings = Seq(
    version      := "0.0-SNAPSHOT",
    organization := "com.scalabasis",
    description  := "A library of building blocks",
    homepage     := Some(url("http://www.scalabasis.com/")),
    licenses     := Seq("MIT" -> url("http://www.opensource.org/licenses/mit-license.php")),
    resolvers    += Resolver.sonatypeRepo("snapshots")
  )
  
  lazy val scalaSettings = Seq(
    scalaVersion   := "2.10.0-RC1",
    scalacOptions ++= Seq("-language:_", "-Yno-predef")
  )
  
  lazy val docSettings = Seq(
    scalacOptions in doc <++= (version, baseDirectory in LocalProject("basis")) map { (version, baseDirectory) =>
      val tagOrBranch = if (version.endsWith("-SNAPSHOT")) "master" else "v" + version
      val docSourceUrl = "https://github.com/scalabasis/basis/tree/" + tagOrBranch + "€{FILE_PATH}.scala"
      Seq("-implicits", "-diagrams", "-sourcepath", baseDirectory.getAbsolutePath, "-doc-source-url", docSourceUrl)
    }
  )
  
  lazy val compileSettings = Seq(
    scalacOptions in Compile ++= Seq("-optimise", "-Xno-forwarders", "-Ywarn-all"),
    libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.0-RC1" % "provided",
    libraryDependencies += "org.scalatest" % "scalatest_2.10.0-RC1" % "1.8" % "test"
  )
  
  lazy val publishSettings = Seq(
    publishMavenStyle := true,
    publishTo <<= version { version =>
      val nexus = "https://oss.sonatype.org/"
      if (version.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomIncludeRepository := (_ => false),
    pomExtra := {
      <scm>
        <url>git@github.com:scalabasis/basis.git</url>
        <connection>scm:git:git@github.com:scalabasis/basis.git</connection>
      </scm>
      <developers>
        <developer>
          <id>c9r</id>
          <name>Chris Sachs</name>
          <email>chris@scalabasis.com</email>
        </developer>
      </developers>
    }
  )
}
