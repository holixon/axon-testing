@file:Suppress("unused")

package io.holixon.axon.testing.jgiven.aggregate

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.As
import com.tngtech.jgiven.annotation.Hidden
import com.tngtech.jgiven.annotation.ProvidedScenarioState
import com.tngtech.jgiven.annotation.Quoted
import io.holixon.axon.testing.jgiven.AxonJGivenStage
import io.holixon.axon.testing.jgiven.step
import org.axonframework.commandhandling.CommandResultMessage
import org.axonframework.deadline.DeadlineMessage
import org.axonframework.eventhandling.EventMessage
import org.axonframework.test.aggregate.ResultValidator
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import java.time.Duration
import java.time.Instant
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * Then stage for aggregate fixture.
 * @param T aggregate type.
 */
@AxonJGivenStage
class AggregateFixtureThen<T> : Stage<AggregateFixtureThen<T>>() {

  @ProvidedScenarioState
  private  var context: AggregateTestFixtureContext<T> = AggregateTestFixtureContext()

  /**
   * Expect event.
   * @param event expected event.
   */
  @As("expect event:")
  fun expectEvent(@Quoted event: Any) = this.expectEvents(event)

  /**
   * Expect a series of events.
   * @param events events to expect.
   */
  @As("expect events:")
  fun expectEvents(@Quoted vararg events: Any) = execute {
    expectEvents(*events)
  }

  /**
   * Expect a series of events.
   * @param events events to expect.
   */
  @As("expect events:")
  fun expectEvents(@Quoted vararg events: EventMessage<*>) = execute {
    expectEvents(*events)
  }

  /**
   * Expect events matching criteria.
   * @param matcher matcher specifying events.
   */
  fun expectEventsMatching(matcher: Matcher<out MutableList<in EventMessage<*>>>) = execute {
    expectEventsMatching(matcher)
  }

  /**
   * Expect no events.
   */
  fun expectNoEvents() = execute {
    expectNoEvents()
  }

  /**
   * Expects the aggregate to be in a provided state.
   * @param field name of the field holding a state.
   * @param expected expected value of the field.
   * @param accessor a function to retrieve the value of the field.
   */
  @As("expect state: $=$")
  fun <E : Any> expectState(@Quoted field: String, @Quoted expected: E, @Hidden accessor: (T) -> E) = execute {
    expectState {
      val e = accessor(it)
      MatcherAssert.assertThat("state failed, expected $field=$expected, but was=$e", e == expected)
      // assert( e == expected ) { "expected state: '$name' not met, was: $e, expected: $expected " } }
    }
  }

  /**
   * Expects the aggregate to be in a provided state.
   * @param expected aggregate value (will be compared using equals()).
   */
  @As("expect state: $")
  fun expectState(@Quoted expected: T) = execute {
    expectState {
      MatcherAssert.assertThat("state failed, expected '$expected', but was=$it", it == expected)
    }
  }


  @As("expect state: $")
  fun expectState(@Quoted message: String = "by validator", @Hidden validator: Consumer<T>) = execute {
    expectState(validator)
  }

  /**
   * Expects an exception with provided message to be thrown.
   * @param exceptionMessage message to expect.
   */
  @As("expect exception message: $")
  fun expectExceptionMessage(@Quoted exceptionMessage: String) = execute {
    expectExceptionMessage(exceptionMessage)
  }

  /**
   * Expects an exception with provided message to be thrown.
   * @param matcher message critera.
   */
  @As("expect matching exception message")
  fun expectMatchingExceptionMessage(matcher: Matcher<String>) = execute {
    expectExceptionMessage(matcher)
  }

  /**
   * Expects an exception of provided type to be thrown.
   * @param clazz type of exception.
   */
  @As("expect exception: $")
  fun expectException(@Quoted clazz: Class<out Throwable>) = execute {
    expectException(clazz)
  }

  /**
   * Expects an exception of provided type to be thrown.
   * @param clazz type of exception.
   */
  @As("expect exception: $")
  fun expectException(@Quoted clazz: KClass<out Throwable>) = expectException(clazz.java)

  /**
   * Expects an exception to be thrown matching criteria.
   * @param matcher criteria to match exception.
   */
  @As("expect matching exception ")
  fun expectMatchingException(matcher: Matcher<*>) = execute {
    expectException(matcher)
  }

  @As("expect successful handler execution")
  fun expectSuccessfulHandlerExecution() = execute {
    expectSuccessfulHandlerExecution()
  }

  /**
   * Expects the result message to carry payload.
   * @param payload expected payload.
   */
  @As("expect message payload")
  fun expectResultMessagePayload(payload: Any) = execute {
    expectResultMessagePayload(payload)
  }

  /**
   * Expects the result message to carry payload.
   * @param matcher expected payload criteria.
   */
  @As("expect message payload matching")
  fun expectResultMessagePayloadMatching(matcher: Matcher<*>) = execute {
    expectResultMessagePayloadMatching(matcher)
  }

  /**
   * Expects the specified result message.
   * @param message expected result message.
   */
  @As("expect result message")
  fun expectResultMessage(message: CommandResultMessage<*>) = execute {
    expectResultMessage(message)
  }

  /**
   * Expects the specified result message.
   * @param matcher expected result message criteria.
   */
  @As("expect result message matching")
  fun expectResultMessageMatching(matcher: Matcher<CommandResultMessage<*>>) = execute {
    expectResultMessageMatching(matcher)
  }

  /**
   * Expects one or several deadlines to met.
   * @param deadlines deadlines to be met. Will be compared with equals.
   */
  fun expectDeadlinesMet(vararg deadlines: Any) = execute {
    expectTriggeredDeadlines(deadlines)
  }

  /**
   * Expects one or several deadlines to met.
   * @param matcher deadlines criteria.
   */
  fun expectDeadlinesMetMatching(matcher: Matcher<out MutableList<in DeadlineMessage<*>>>) = execute {
    expectTriggeredDeadlinesMatching(matcher)
  }

  /**
   * Expects no deadlines to match.
   */
  fun expectNoScheduledDeadlines() = execute {
    expectNoScheduledDeadlines()
  }

  /**
   * Expects a deadline to be scheduled.
   * @param duration duration after the deadline has been scheduled.
   * @param deadline a deadline.
   */
  fun expectScheduledDeadline(duration: Duration, deadline: Any) = execute {
    expectScheduledDeadline(duration, deadline)
  }

  /**
   * Expects a deadline to be scheduled.
   * @param duration duration after the deadline has been scheduled.
   * @param matcher deadline criteria.
   */
  fun expectScheduledDeadlineMatching(duration: Duration, matcher: Matcher<in DeadlineMessage<*>>) = execute {
    expectScheduledDeadlineMatching(duration, matcher)
  }

  /**
   * Expects a deadline to be scheduled on a certain point of time.
   * @param instant point of time.
   * @param deadline a deadline.
   */
  fun expectScheduledDeadline(instant: Instant, deadline: Any) = execute {
    expectScheduledDeadline(instant, deadline)
  }

  /**
   * Expects a deadline to be scheduled on a certain point of time.
   * @param instant point of time.
   * @param matcher deadline criteria.
   */
  fun expectScheduledDeadlineMatching(instant: Instant, matcher: Matcher<in DeadlineMessage<*>>) = execute {
    expectScheduledDeadlineMatching(instant, matcher)
  }

  /**
   * Every execution gets the store instance, executes the given receiver block and returns the modified result.
   */
  private fun execute(block: ResultValidator<T>.() -> ResultValidator<T>): AggregateFixtureThen<T> = step {
    context.checkInitialized()
    context.resultValidator = block(context.resultValidator!!)
  }
}
