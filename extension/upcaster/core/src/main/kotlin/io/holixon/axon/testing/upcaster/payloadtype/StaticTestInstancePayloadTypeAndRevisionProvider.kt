package io.holixon.axon.testing.upcaster.payloadtype

import io.holixon.axon.testing.upcaster.PayloadTypeAndRevisionProvider

/**
 * Marker implementation delegating to the provider retrieved from the instance variable of the test.
 */
class StaticTestInstancePayloadTypeAndRevisionProvider(impl: PayloadTypeAndRevisionProvider) : PayloadTypeAndRevisionProvider by impl
