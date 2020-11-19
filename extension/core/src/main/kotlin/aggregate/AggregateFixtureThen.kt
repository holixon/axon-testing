@file:Suppress("unused")
package io.holixon.axon.test.jgiven.aggregate

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.*
import io.holixon.axon.test.jgiven.AxonJGivenStage
import org.axonframework.commandhandling.CommandResultMessage
import org.axonframework.deadline.DeadlineMessage
import org.axonframework.eventhandling.EventMessage
import org.axonframework.test.aggregate.ResultValidator
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import java.time.Duration
import java.time.Instant

@AxonJGivenStage
class AggregateFixtureThen<T> : Stage<AggregateFixtureThen<T>>() {

  @ExpectedScenarioState(required = true)
  lateinit var resultValidator: ResultValidator<T>

  @As("expect event:")
  fun expectEvent(@Quoted event: Any) = this.expectEvents(event)

  @As("expect events:")
  fun expectEvents(@Quoted @Table vararg events: Any) = execute { resultValidator.expectEvents(*events) }

  @As("expect events:")
  fun expectEvents(@Quoted @Table vararg events: EventMessage<*>) = execute { resultValidator.expectEvents(*events) }

  fun expectEventsMatching(matcher: Matcher<out MutableList<in EventMessage<*>>>) = execute { resultValidator.expectEventsMatching(matcher) }

  fun expectNoEvents() = execute { resultValidator.expectNoEvents() }

  @As("expect state: $=$")
  fun <E : Any> expectState(@Quoted field: String, @Quoted expected: E, @Hidden accessor: (T) -> E) = execute {
    resultValidator.expectState {
      val e = accessor(it)
      MatcherAssert.assertThat("state failed, expected $field=$expected, but was=$e", e == expected)
      // assert( e == expected ) { "expected state: '$name' not met, was: $e, expected: $expected " } }
    }
  }

  @As("expect state: $")
  fun expectState(@Quoted expected: T) = execute {
    resultValidator.expectState {
      MatcherAssert.assertThat("state failed, expected '$expected', but was=$it", it == expected)
    }
  }

  @As("expect exception message: $")
  fun expectExceptionMessage(@Quoted exceptionMessage: String) = execute {
    resultValidator.expectExceptionMessage(exceptionMessage)
  }

  @As("expect matching exception message")
  fun expectMatchingExceptionMessage(matcher: Matcher<String>) = execute {
    resultValidator.expectExceptionMessage(matcher)
  }

  @As("expect exception: $")
  fun expectException(@Quoted exception: Class<out Throwable>) = execute {
    resultValidator.expectException(exception)
  }

  @As("expect matching exception ")
  fun expectMatchingException(matcher: Matcher<*>) = execute {
    resultValidator.expectException(matcher)
  }

  @As("expect successful handler execution")
  fun expectSuccessfulHandlerExecution() = execute {
    resultValidator.expectSuccessfulHandlerExecution()
  }

  @As("expect message payload")
  fun expectResultMessagePayload(payload: Any) = execute {
    resultValidator.expectResultMessagePayload(payload)
  }

  @As("expect message payload matching")
  fun expectResultMessagePayloadMatching(matcher: Matcher<*>) = execute {
    resultValidator.expectResultMessagePayloadMatching(matcher)
  }

  @As("expect result message")
  fun expectResultMessage(message: CommandResultMessage<*>) = execute {
    resultValidator.expectResultMessage(message)
  }

  @As("expect result message matching")
  fun expectResultMessageMatching(matcher: Matcher<CommandResultMessage<*>>) = execute {
    resultValidator.expectResultMessageMatching(matcher)
  }

  fun expectDeadlinesMet(expected: Any) = execute {
    resultValidator.expectDeadlinesMet(expected)
  }

  fun expectDeadlinesMetMatching(matcher: Matcher<out MutableList<in DeadlineMessage<*>>>) = execute {
    resultValidator.expectDeadlinesMetMatching(matcher)
  }

  fun expectNoScheduledDeadlines() = execute {
    resultValidator.expectNoScheduledDeadlines()
  }

  fun expectScheduledDeadline(duration: Duration, deadline: Any) = execute {
    resultValidator.expectScheduledDeadline(duration, deadline)
  }

  fun expectScheduledDeadlineMatching(duration: Duration, matcher: Matcher<in DeadlineMessage<*>>) = execute {
    resultValidator.expectScheduledDeadlineMatching(duration, matcher)
  }

  fun expectScheduledDeadline(instant: Instant, deadline: Any) = execute {
    resultValidator.expectScheduledDeadline(instant, deadline)
  }

  fun expectScheduledDeadlineMatching(instant: Instant, matcher: Matcher<in DeadlineMessage<*>>) = execute {
    resultValidator.expectScheduledDeadlineMatching(instant, matcher)
  }

  private fun execute(block: () -> ResultValidator<T>) = self().apply { resultValidator = block.invoke() }!!
}
