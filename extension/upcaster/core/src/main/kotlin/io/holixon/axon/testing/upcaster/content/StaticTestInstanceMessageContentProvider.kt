package io.holixon.axon.testing.upcaster.content

import io.holixon.axon.testing.upcaster.MessageContentProvider

/**
 * Marker implementation delegating to the provider retrieved from the instance variable of the test.
 */
class StaticTestInstanceMessageContentProvider(impl: MessageContentProvider) : MessageContentProvider by impl
