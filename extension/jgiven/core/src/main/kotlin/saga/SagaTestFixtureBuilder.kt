package io.holixon.axon.testing.jgiven.saga

import org.axonframework.deadline.DeadlineMessage
import org.axonframework.eventhandling.ListenerInvocationErrorHandler
import org.axonframework.messaging.MessageDispatchInterceptor
import org.axonframework.messaging.MessageHandlerInterceptor
import org.axonframework.messaging.annotation.HandlerDefinition
import org.axonframework.messaging.annotation.HandlerEnhancerDefinition
import org.axonframework.messaging.annotation.ParameterResolverFactory
import org.axonframework.modelling.saga.ResourceInjector
import org.axonframework.test.matchers.FieldFilter
import org.axonframework.test.saga.SagaTestFixture
import org.axonframework.test.utils.CallbackBehavior

class SagaTestFixtureBuilder<T>(private val sagaType: Class<T>) {

  private val injectableResources: MutableList<Any> = mutableListOf()
  private var transienceCheckEnabled: Boolean? = null

  private lateinit var resourceInjector: ResourceInjector
  private lateinit var callbackBehavior: CallbackBehavior
  private lateinit var listenerInvocationErrorHandler: ListenerInvocationErrorHandler
  private val onStartRecordingCallbacks: MutableList<Runnable> = mutableListOf()
  private val deadlineDispatchInterceptors: MutableList<MessageDispatchInterceptor<DeadlineMessage<*>>> = mutableListOf()
  private val deadlineHandlerInterceptors: MutableList<MessageHandlerInterceptor<DeadlineMessage<*>>> = mutableListOf()
  private val fieldFilters: MutableList<FieldFilter> = mutableListOf()
  private val ignoredFields: MutableList<Pair<Class<*>, String>> = mutableListOf()
  private val handlerDefinitions: MutableList<HandlerDefinition> = mutableListOf()
  private val handlerEnhancerDefinitions: MutableList<HandlerEnhancerDefinition> = mutableListOf()
  private val parameterResolverFactories: MutableList<ParameterResolverFactory> = mutableListOf()

  fun withTransienceCheckDisabled() = apply { this.transienceCheckEnabled = false }

  fun registerResource(vararg resources: Any) = apply { this.injectableResources.addAll(resources) }

  fun registerParameterResolverFactory(vararg parameterResolverFactories: ParameterResolverFactory) =
    apply { this.parameterResolverFactories.addAll(parameterResolverFactories) }

  fun registerFieldFilter(vararg fieldFilters: FieldFilter) = apply { this.fieldFilters.addAll(fieldFilters) }

  fun registerIgnoredField(declaringClass: Class<*>, fieldName: String) = apply { this.ignoredFields.add(declaringClass to fieldName) }

  fun registerHandlerDefinition(vararg handlerDefinitions: HandlerDefinition) = apply { this.handlerDefinitions.addAll(handlerDefinitions) }
  fun registerHandlerEnhancerDefinition(vararg handlerEnhancerDefinitions: HandlerEnhancerDefinition) =
    apply { this.handlerEnhancerDefinitions.addAll(handlerEnhancerDefinitions) }

  fun registerDeadlineDispatchInterceptor(vararg deadlineDispatchInterceptors: MessageDispatchInterceptor<DeadlineMessage<*>>) =
    apply { this.deadlineDispatchInterceptors.addAll(deadlineDispatchInterceptors) }

  fun registerDeadlineHandlerInterceptor(vararg deadlineHandlerInterceptors: MessageHandlerInterceptor<DeadlineMessage<*>>) =
    apply { this.deadlineHandlerInterceptors.addAll(deadlineHandlerInterceptors) }


  fun registerListenerInvocationErrorHandler(listenerInvocationErrorHandler: ListenerInvocationErrorHandler) =
    apply { this.listenerInvocationErrorHandler = listenerInvocationErrorHandler }

  fun registerResourceInjector(resourceInjector: ResourceInjector) = apply { this.resourceInjector = resourceInjector }

  fun registerStartRecordingCallback(vararg onStartRecordingCallbacks: Runnable) = apply { this.onStartRecordingCallbacks.addAll(onStartRecordingCallbacks) }

  fun setCallbackBehavior(callbackBehavior: CallbackBehavior) = apply { this.callbackBehavior = callbackBehavior }

  fun build(): SagaTestFixture<T> {
    val fixture = SagaTestFixture(sagaType)
    handlerEnhancerDefinitions.forEach { fixture.registerHandlerEnhancerDefinition(it) }
    handlerDefinitions.forEach { fixture.registerHandlerDefinition(it) }

    deadlineDispatchInterceptors.forEach { fixture.registerDeadlineDispatchInterceptor(it) }
    deadlineHandlerInterceptors.forEach { fixture.registerDeadlineHandlerInterceptor(it) }


    if (this::listenerInvocationErrorHandler.isInitialized) fixture.registerListenerInvocationErrorHandler(listenerInvocationErrorHandler)
    if (this::resourceInjector.isInitialized) fixture.registerResourceInjector(resourceInjector)
    fieldFilters.forEach { fixture.registerFieldFilter(it) }
    injectableResources.forEach { fixture.registerResource(it) }
    ignoredFields.forEach { fixture.registerIgnoredField(it.first, it.second) }
    transienceCheckEnabled?.let { fixture.withTransienceCheckDisabled() }
    parameterResolverFactories.forEach { fixture.registerParameterResolverFactory(it) }

    onStartRecordingCallbacks.forEach { fixture.registerStartRecordingCallback(it) }


    return fixture
  }

}
