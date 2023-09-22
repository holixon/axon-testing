package io.holixon.axon.testing.jgiven.saga

import io.holixon.axon.testing.jgiven.saga.SagaTestFixtureFields.fieldFixtureFixtureExecutionResult
import io.holixon.axon.testing.jgiven.saga.SagaTestFixtureFields.fieldSagaStore
import io.holixon.axon.testing.jgiven.saga.SagaTestFixtureFields.fieldSagaType
import org.axonframework.modelling.saga.repository.inmemory.InMemorySagaStore
import org.axonframework.test.saga.FixtureExecutionResult
import org.axonframework.test.saga.SagaTestFixture
import java.lang.reflect.Field

/**
 * Handles access to private final fields in axon-test.
 * Might become obsolete when fields are configurable in core.
 */
object SagaTestFixtureFields {
  private val CLASS = SagaTestFixture::class.java

  private fun Class<*>.getDeclaredAccessibleField(fieldName: String): Field = getDeclaredField(fieldName)
    .apply {
      isAccessible = true
    }

  val fieldSagaStore: Field = CLASS.getDeclaredAccessibleField("sagaStore")
  val fieldFixtureFixtureExecutionResult: Field = CLASS.getDeclaredAccessibleField("fixtureExecutionResult")
  val fieldSagaType: Field = CLASS.getDeclaredAccessibleField("sagaType")
}

@Suppress("UNCHECKED_CAST")
val <T> SagaTestFixture<T>.sagaType: Class<T> get() = fieldSagaType.get(this) as Class<T>
val <T> SagaTestFixture<T>.fixtureExecutionResult: FixtureExecutionResult get() = fieldFixtureFixtureExecutionResult.get(this) as FixtureExecutionResult
val <T> SagaTestFixture<T>.sagaStore: InMemorySagaStore get() = fieldSagaStore.get(this) as InMemorySagaStore
