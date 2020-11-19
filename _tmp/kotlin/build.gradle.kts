import _buildsrc.axonframework
import _buildsrc.junit5

plugins {
  kotlin("jvm")
  kotlin("plugin.allopen")
}

dependencies {
  implementation(axonframework("configuration"))

  implementation(kotlin("stdlib-jdk8"))
  testImplementation(junit5("api"))
  testRuntimeOnly(junit5("engine"))
  testImplementation("ch.qos.logback:logback-core:${Versions.Test.logback}")

  testImplementation(axonframework("test"))
  testImplementation("org.hamcrest:hamcrest-core:2.1")
}
