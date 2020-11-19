package io.toolisticon.addons.axon.examples.giftcard.projection

import io.toolisticon.addons.axon.examples.giftcard.api.GiftcardEvent.*
import io.toolisticon.addons.axon.examples.giftcard.api.GiftcardQuery.*
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import java.util.concurrent.ConcurrentHashMap


class GiftcardSummariesProjection(
  private val summaries : GiftcardSummaries
) {

  @EventHandler
  fun on(evt: IssuedEvent) {
    summaries[evt.id] = GiftcardSummary(id = evt.id, initialBalance = evt.initialBalance, currentBalance = evt.initialBalance)
  }

  @QueryHandler
  @Suppress("UNUSED_PARAMETER")
  fun on(query: FindAll) = summaries.values.toList()

}

typealias GiftcardSummaries = ConcurrentHashMap<String, GiftcardSummary>

data class GiftcardSummary(
  val id: String,
  val initialBalance: Int,
  val currentBalance: Int
)

