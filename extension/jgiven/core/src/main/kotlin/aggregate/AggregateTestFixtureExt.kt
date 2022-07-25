package io.holixon.axon.testing.jgiven.aggregate

import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.ResultValidator
import java.lang.reflect.Method

object AggregateTestFixtureExt {

  private val method_buildResultValidator: Method = AggregateTestFixture::class.java.getDeclaredMethod("buildResultValidator").apply {
    isAccessible = true
  }

  @Suppress("UNCHECKED_CAST")
  fun <T> buildResultValidator(fixture:AggregateTestFixture<T>) = method_buildResultValidator.invoke(fixture) as ResultValidator<T>
}
