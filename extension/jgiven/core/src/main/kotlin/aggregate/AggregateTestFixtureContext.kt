package io.holixon.axon.testing.jgiven.aggregate

import io.holixon.axon.testing.jgiven.aggregate.AggregateTestFixtureReflection.aggregateIdentifier
import io.holixon.axon.testing.jgiven.aggregate.AggregateTestFixtureReflection.buildResultValidator
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.ResultValidator
import org.axonframework.test.aggregate.TestExecutor

internal class AggregateTestFixtureContext<T>(
  var fixture: AggregateTestFixture<T>? = null,
  var testExecutor: TestExecutor<T>? = null,
  var resultValidator: ResultValidator<T>? = null
) {

  constructor(fixture: AggregateTestFixture<T>) : this(
    fixture,
    fixture.givenNoPriorActivity(),
    buildResultValidator(fixture)
  )

  fun checkInitialized() {
    if (!isInitialized()) {
      throw IllegalStateException("context is not initialized. You will at least need to add the line `given().noPriorActivity()` to your test.")
    }
  }

  fun isInitialized() = fixture != null && testExecutor != null && resultValidator != null

  var isFirstGiven = true

  val aggregateIdentifier: String? get() = fixture?.aggregateIdentifier
}
