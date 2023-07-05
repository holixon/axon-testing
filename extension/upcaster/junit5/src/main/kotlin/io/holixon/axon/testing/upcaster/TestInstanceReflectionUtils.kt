package io.holixon.axon.testing.upcaster

import org.axonframework.serialization.Serializer
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.reflect.KClass

/**
 * Extracts the serializer from the test instance.
 */
fun ExtensionContext.getSerializer(encoding: UpcasterTest.EventEncoding): Serializer? {
  TODO("implement me")
}

/**
 * Extracts the PayloadTypeAndRevisionProvider from the tet instance.
 */
fun ExtensionContext.getPayloadTypeAndRevisionProvider(clazz: KClass<PayloadTypeAndRevisionProvider>): PayloadTypeAndRevisionProvider? {
  TODO("implement me")
}
