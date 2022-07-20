@file:Suppress("unused")

package io.holixon.axon.testing.jgiven.aggregate

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.As
import com.tngtech.jgiven.annotation.ExpectedScenarioState
import com.tngtech.jgiven.annotation.Hidden
import com.tngtech.jgiven.annotation.Quoted
import io.holixon.axon.testing.jgiven.AxonJGivenStage
import org.axonframework.commandhandling.CommandResultMessage
import org.axonframework.deadline.DeadlineMessage
import org.axonframework.eventhandling.EventMessage
import org.axonframework.test.aggregate.ResultValidator
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import java.time.Duration
import java.time.Instant
import java.util.function.Consumer

/**
 * Then stage for aggregate fixture.
 * @param T aggregate type.
 */
@AxonJGivenStage
class AggregateFixtureThen<T> : Stage<AggregateFixtureThen<T>>() {

  @ExpectedScenarioState(required = true)
  private lateinit var resultValidator: ResultValidator<T>

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
  fun expectEvents(@Quoted vararg events: Any) = execute { resultValidator.expectEvents(*events) }

  /**
   * Expect a series of events.
   * @param events events to expect.
   */
  @As("expect events:")
  fun expectEvents(@Quoted vararg events: EventMessage<*>) = execute { resultValidator.expectEvents(*events) }

  /**
   * Expect events matching criteria.
   * @param matcher matcher specifying events.
   */
  fun expectEventsMatching(matcher: Matcher<out MutableList<in EventMessage<*>>>) = execute { resultValidator.expectEventsMatching(matcher) }

  /**
   * Expect no events.
   */
  fun expectNoEvents() = execute { resultValidator.expectNoEvents() }

  /**
   * Expects the aggregate to be in a provided state.
   * @param field name of the field holding a state.
   * @param expected expected value of the field.
   * @param accessor a function to retrieve the value of the field.
   */
  @As("expect state: $=$")
  fun <E : Any> expectState(@Quoted field: String, @Quoted expected: E, @Hidden accessor: (T) -> E) = execute {
    resultValidator.expectState {
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
    resultValidator.expectState {
      MatcherAssert.assertThat("state failed, expected '$expected', but was=$it", it == expected)
    }
  }


  @As("expect state: $")
  fun expectState(@Quoted message: String = "by validator", @Hidden validator: Consumer<T>) = execute {
    resultValidator.expectState(validator)
  }

  /**
   * Expects an exception with provided message to be thrown.
   * @param exceptionMessage message to expect.
   */
  @As("expect exception message: $")
  fun expectExceptionMessage(@Quoted exceptionMessage: String) = execute {
    resultValidator.expectExceptionMessage(exceptionMessage)
  }

  /**
   * Expects an exception with provided message to be thrown.
   * @param matcher message critera.
   */
  @As("expect matching exception message")
  fun expectMatchingExceptionMessage(matcher: Matcher<String>) = execute {
    resultValidator.expectExceptionMessage(matcher)
  }

  /**
   * Expects an exception of provided type to be thrown.
   * @param clazz type of exception.
   */
  @As("expect exception: $")
  fun expectException(@Quoted clazz: Class<out Throwable>) = execute {
    resultValidator.expectException(clazz)
  }

  /**
   * Expects an exception to be thrown matching criteria.
   * @param matcher criteria to match exception.
   */
  @As("expect matching exception ")
  fun expectMatchingException(matcher: Matcher<*>) = execute {
    resultValidator.expectException(matcher)
  }

  @As("expect successful handler execution")
  fun expectSuccessfulHandlerExecution() = execute {
    resultValidator.expectSuccessfulHandlerExecution()
  }

  /**
   * Expects the result message to carry payload.
   * @param payload expected payload.
   */
  @As("expect message payload")
  fun expectResultMessagePayload(payload: Any) = execute {
    resultValidator.expectResultMessagePayload(payload)
  }

  /**
   * Expects the result message to carry payload.
   * @param matcher expected payload criteria.
   */
  @As("expect message payload matching")
  fun expectResultMessagePayloadMatching(matcher: Matcher<*>) = execute {
    resultValidator.expectResultMessagePayloadMatching(matcher)
  }

  /**
   * Expects the specified result message.
   * @param message expected result message.
   */
  @As("expect result message")
  fun expectResultMessage(message: CommandResultMessage<*>) = execute {
    resultValidator.expectResultMessage(message)
  }

  /**
   * Expects the specified result message.
   * @param matcher expected result message criteria.
   */
  @As("expect result message matching")
  fun expectResultMessageMatching(matcher: Matcher<CommandResultMessage<*>>) = execute {
    resultValidator.expectResultMessageMatching(matcher)
  }

  /**
   * Expects one or several deadlines to met.
   * @param deadlines deadlines to be met. Will be compared with equals.
   */
  fun expectDeadlinesMet(vararg deadlines: Any) = execute {
    resultValidator.expectDeadlinesMet(deadlines)
  }

  /**
   * Expects one or several deadlines to met.
   * @param matcher deadlines criteria.
   */
  fun expectDeadlinesMetMatching(matcher: Matcher<out MutableList<in DeadlineMessage<*>>>) = execute {
    resultValidator.expectDeadlinesMetMatching(matcher)
  }

  /**
   * Expects no deadlines to match.
   */
  fun expectNoScheduledDeadlines() = execute {
    resultValidator.expectNoScheduledDeadlines()
  }

  /**
   * Expects a deadline to be scheduled.
   * @param duration duration after the deadline has been scheduled.
   * @param deadline a deadline.
   */
  fun expectScheduledDeadline(duration: Duration, deadline: Any) = execute {
    resultValidator.expectScheduledDeadline(duration, deadline)
  }

  /**
   * Expects a deadline to be scheduled.
   * @param duration duration after the deadline has been scheduled.
   * @param matcher deadline criteria.
   */
  fun expectScheduledDeadlineMatching(duration: Duration, matcher: Matcher<in DeadlineMessage<*>>) = execute {
    resultValidator.expectScheduledDeadlineMatching(duration, matcher)
  }

  /**
   * Expects a deadline to be scheduled on a certain point of time.
   * @param instant point of time.
   * @param deadline a deadline.
   */
  fun expectScheduledDeadline(instant: Instant, deadline: Any) = execute {
    resultValidator.expectScheduledDeadline(instant, deadline)
  }

  /**
   * Expects a deadline to be scheduled on a certain point of time.
   * @param instant point of time.
   * @param matcher deadline criteria.
   */
  fun expectScheduledDeadlineMatching(instant: Instant, matcher: Matcher<in DeadlineMessage<*>>) = execute {
    resultValidator.expectScheduledDeadlineMatching(instant, matcher)
  }

  private fun execute(block: () -> ResultValidator<T>) = self().apply { resultValidator = block.invoke() }!!
}
