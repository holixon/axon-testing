package io.holixon.axon.testing.upcaster

import kotlin.reflect.KClass

fun <T : Any> Class<*>.getInstanceVariableValue(variableType: KClass<out T>, instance: Any?, variableValueTypeHint: KClass<out T>): T {
  val candidates = this.declaredFields.filter {
    it.type == variableType.java
  }


  val values = candidates.mapNotNull { field ->
    try {
      field.isAccessible = true
      val value = field.get(instance)
      if (variableValueTypeHint.java.isAssignableFrom(value.javaClass)) {
        @Suppress("UNCHECKED_CAST")
        value as T
      } else {
        null
      }
    } catch (e: Exception) {
      null
    }
  }

  require(values.size == 1) {
    "Expecting to find exactly one instance variable of type ${variableType.simpleName}, but found ${candidates.size}: ${
      candidates.joinToString(
        ", "
      ) { candidate -> candidate.name }
    }"
  }
  return values.first()
}

