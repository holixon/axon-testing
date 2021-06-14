package io.holixon.axon.testing.jgiven.aggregate

import org.axonframework.commandhandling.CommandMessage
import org.axonframework.deadline.DeadlineMessage
import org.axonframework.eventsourcing.AggregateFactory
import org.axonframework.messaging.MessageDispatchInterceptor
import org.axonframework.messaging.MessageHandler
import org.axonframework.messaging.MessageHandlerInterceptor
import org.axonframework.messaging.annotation.HandlerDefinition
import org.axonframework.messaging.annotation.HandlerEnhancerDefinition
import org.axonframework.messaging.annotation.ParameterResolverFactory
import org.axonframework.modelling.command.CommandTargetResolver
import org.axonframework.modelling.command.Repository
import org.axonframework.modelling.command.RepositoryProvider
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.matchers.FieldFilter
import kotlin.reflect.KClass

class AggregateTestFixtureBuilder<T>(private val aggregateType: Class<T>) {

  private lateinit var aggregateFactory: AggregateFactory<T>
  private val annotatedCommandHandler: MutableList<Any> = mutableListOf()
  private val commandHandlers: MutableMap<String, MessageHandler<CommandMessage<*>>> = mutableMapOf()
  private val commandDispatchInterceptors: MutableList<MessageDispatchInterceptor<in CommandMessage<*>>> = mutableListOf()
  private val commandHandlerInterceptors: MutableList<MessageHandlerInterceptor<in CommandMessage<*>>> = mutableListOf()
  private lateinit var commandTargetResolver: CommandTargetResolver
  private val deadlineDispatchInterceptors: MutableList<MessageDispatchInterceptor<in DeadlineMessage<*>>> = mutableListOf()
  private val deadlineHandlerInterceptors: MutableList<MessageHandlerInterceptor<in DeadlineMessage<*>>> = mutableListOf()
  private val fieldFilters: MutableList<FieldFilter> = mutableListOf()
  private val ignoredFields: MutableList<Pair<Class<*>, String>> = mutableListOf()
  private val handlerDefinitions: MutableList<HandlerDefinition> = mutableListOf()
  private val handlerEnhancerDefinitions: MutableList<HandlerEnhancerDefinition> = mutableListOf()
  private val injectableResources: MutableList<Any> = mutableListOf()
  private val parameterResolverFactories: MutableList<ParameterResolverFactory> = mutableListOf()
  private var reportIllegalStateChange: Boolean? = null
  private lateinit var repository: Repository<T>
  private lateinit var repositoryProvider: RepositoryProvider
  private lateinit var subtypes: Array<Class<out T>>

  fun registerAggregateFactory(aggregateFactory: AggregateFactory<T>) = apply { this.aggregateFactory = aggregateFactory }
  fun registerAnnotatedCommandHandler(annotatedCommandHandler: Any) = apply { this.annotatedCommandHandler.add(annotatedCommandHandler) }

  fun registerCommandHandler(commandName: String, commandHandler: MessageHandler<CommandMessage<*>>): AggregateTestFixtureBuilder<T> =
    apply { this.commandHandlers[commandName] = commandHandler }

  fun registerCommandHandler(payloadType: Class<*>, commandHandler: MessageHandler<CommandMessage<*>>): AggregateTestFixtureBuilder<T> =
    registerCommandHandler(payloadType.name, commandHandler)

  fun registerCommandDispatchInterceptor(vararg commandDispatchInterceptors: MessageDispatchInterceptor<in CommandMessage<*>>) =
    apply { this.commandDispatchInterceptors.addAll(commandDispatchInterceptors) }

  fun registerCommandHandlerInterceptor(vararg commandHandlerInterceptors: MessageHandlerInterceptor<in CommandMessage<*>>) =
    apply { this.commandHandlerInterceptors.addAll(commandHandlerInterceptors) }

  fun registerCommandTargetResolver(commandTargetResolver: CommandTargetResolver) = apply { this.commandTargetResolver = commandTargetResolver }

  fun registerDeadlineDispatchInterceptor(vararg deadlineDispatchInterceptors: MessageDispatchInterceptor<in DeadlineMessage<*>>) =
    apply { this.deadlineDispatchInterceptors.addAll(deadlineDispatchInterceptors) }

  fun registerDeadlineHandlerInterceptor(vararg deadlineHandlerInterceptors: MessageHandlerInterceptor<in DeadlineMessage<*>>) =
    apply { this.deadlineHandlerInterceptors.addAll(deadlineHandlerInterceptors) }

  fun registerFieldFilter(vararg fieldFilters: FieldFilter) = apply { this.fieldFilters.addAll(fieldFilters) }

  fun registerHandlerDefinition(vararg handlerDefinitions: HandlerDefinition) = apply { this.handlerDefinitions.addAll(handlerDefinitions) }
  fun registerHandlerEnhancerDefinition(vararg handlerEnhancerDefinitions: HandlerEnhancerDefinition) =
    apply { this.handlerEnhancerDefinitions.addAll(handlerEnhancerDefinitions) }


  fun registerIgnoredField(declaringClass: KClass<*>, fieldName: String) = apply { ignoredFields.add(declaringClass.java to fieldName) }
  fun registerIgnoredField(declaringClass: Class<*>, fieldName: String) = apply { ignoredFields.add(declaringClass to fieldName) }


  fun registerInjectableResource(vararg resources: Any) = apply { this.injectableResources.addAll(resources) }

  fun registerParameterResolverFactory(vararg parameterResolverFactories: ParameterResolverFactory) =
    apply { this.parameterResolverFactories.addAll(parameterResolverFactories) }

  fun registerRepository(repository: Repository<T>) = apply { this.repository = repository }
  fun registerRepositoryProvider(repositoryProvider: RepositoryProvider) = apply { this.repositoryProvider = repositoryProvider }


  fun setReportIllegalStateChange(reportIllegalStateChange: Boolean) = apply { this.reportIllegalStateChange = reportIllegalStateChange }

  fun withSubtypes(vararg subtypes: Class<out T>) = withSubtypes(subtypes.asList())
  fun withSubtypes(subtypes: Collection<Class<out T>>) = apply { this.subtypes = subtypes.toTypedArray() }

  fun build(): AggregateTestFixture<T> {
    val fixture = AggregateTestFixture<T>(aggregateType)

    if (this::aggregateFactory.isInitialized) fixture.registerAggregateFactory(aggregateFactory)

    annotatedCommandHandler.forEach { fixture.registerAnnotatedCommandHandler(it) }
    commandHandlers.forEach { fixture.registerCommandHandler(it.key, it.value) }
    commandDispatchInterceptors.forEach { fixture.registerCommandDispatchInterceptor(it) }
    commandHandlerInterceptors.forEach { fixture.registerCommandHandlerInterceptor(it) }
    if (this::commandTargetResolver.isInitialized) fixture.registerCommandTargetResolver(commandTargetResolver)
    deadlineDispatchInterceptors.forEach { fixture.registerDeadlineDispatchInterceptor(it) }
    deadlineHandlerInterceptors.forEach { fixture.registerDeadlineHandlerInterceptor(it) }
    fieldFilters.forEach { fixture.registerFieldFilter(it) }

    handlerEnhancerDefinitions.forEach { fixture.registerHandlerEnhancerDefinition(it) }
    handlerDefinitions.forEach { fixture.registerHandlerDefinition(it) }

    injectableResources.forEach { fixture.registerInjectableResource(it) }
    ignoredFields.forEach { fixture.registerIgnoredField(it.first, it.second) }

    parameterResolverFactories.forEach { fixture.registerParameterResolverFactory(it) }

    if (this::repository.isInitialized) fixture.registerRepository(repository)
    if (this::repositoryProvider.isInitialized) fixture.registerRepositoryProvider(repositoryProvider)
    if (this::subtypes.isInitialized) fixture.withSubtypes(*this.subtypes)
    reportIllegalStateChange?.let { fixture.setReportIllegalStateChange(it) }

    return fixture
  }
}
