package io.holixon.axon.testing.upcaster.content

import io.holixon.axon.testing.upcaster.MessageContentProvider

/**
 * Marker implementation auto-detecting the message content provider, based on message encoding.
 */
class MessageEncodingAutoDetectingMessageContentProvider(impl: MessageContentProvider) : MessageContentProvider by impl
