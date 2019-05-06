import com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile

name := """pro_play_java"""
organization := "com.heigvd-pro-b-08"

version := "1.0-SNAPSHOT"

lazy val myProject = (project in file("."))
  .enablePlugins(PlayJava)

scalaVersion := "2.12.8"

libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"
libraryDependencies += guice
libraryDependencies += "junit" % "junit" % "4.12" % "test"
libraryDependencies += javaJdbc
libraryDependencies += jdbc
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5.jre6"
libraryDependencies ++= Seq("com.novocode" % "junit-interface" % "0.10" % "test")

libraryDependencies += "com.itextpdf" % "itextpdf" % "5.5.13"
libraryDependencies += "com.itextpdf.tool" % "xmlworker" % "5.5.13"

// "org.postgresql" % "postgresql" % "42.1.5"