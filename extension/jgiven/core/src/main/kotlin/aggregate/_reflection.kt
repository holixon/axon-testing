package io.holixon.axon.testing.jgiven.aggregate

import io.holixon.axon.testing.jgiven.AxonJGiven.getDeclaredAccessibleField
import io.holixon.axon.testing.jgiven.AxonJGiven.getDeclaredAccessibleMethod
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.ResultValidator
import java.lang.reflect.Field
import java.lang.reflect.Method

internal object AggregateTestFixtureReflection {

  private val method_buildResultValidator: Method = AggregateTestFixture::class.java.getDeclaredAccessibleMethod("buildResultValidator")
  private val field_aggregateIdentifier: Field = AggregateTestFixture::class.java.getDeclaredAccessibleField("aggregateIdentifier")

  @Suppress("UNCHECKED_CAST")
  fun <T> buildResultValidator(fixture: AggregateTestFixture<T>): ResultValidator<T> = method_buildResultValidator.invoke(fixture) as ResultValidator<T>

  val <T> AggregateTestFixture<T>.aggregateIdentifier: String? get() = field_aggregateIdentifier.get(this) as String?
}
