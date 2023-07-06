package io.holixon.axon.testing.upcaster

import io.holixon.axon.testing.upcaster.content.StaticTestInstanceMessageContentProvider
import io.holixon.axon.testing.upcaster.payloadtype.StaticTestInstancePayloadTypeAndRevisionProvider
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.xml.XStreamSerializer
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.File
import java.lang.reflect.ParameterizedType
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Retrieves the test folder for this test context.
 */
fun ExtensionContext.getTestFolder(): File? {
  val folderPathAsString = IntermediateRepresentationProvider::class.java.getResource("/" + getTestFolderName())?.path ?: return null
  val path = Path(folderPathAsString)
  return if (Files.exists(path) && path.toFile().isDirectory) {
    path.toFile()
  } else {
    null
  }
}

/**
 * Retrieves the test folder name for this test context.
 */
fun ExtensionContext.getTestFolderName(): String =
  (this.requiredTestClass.canonicalName + "." + this.requiredTestMethod.name.replace(' ', '_')).replace('.', '/')

/**
 * Tries to detect the parameter type of the parametrized method.
 */
fun ExtensionContext.detectParameterType(): Class<*>? {
  return if (
    this.requiredTestMethod.genericParameterTypes.isNotEmpty()
    && this.requiredTestMethod.genericParameterTypes[0] is ParameterizedType
    && (this.requiredTestMethod.genericParameterTypes[0] as ParameterizedType).actualTypeArguments.isNotEmpty()
    && ((this.requiredTestMethod.genericParameterTypes[0] as ParameterizedType).actualTypeArguments[0] is Class<*>)
  ) {
    (this.requiredTestMethod.genericParameterTypes[0] as ParameterizedType).actualTypeArguments[0] as Class<*>
  } else {
    null
  }
}

/**
 * Extracts the serializer from the test instance.
 */
fun ExtensionContext.getSerializer(encoding: MessageEncoding): Serializer? {
  if (this.testClass.isEmpty) {
    return null
  }
  val targetType: KClass<out Serializer> = when (encoding) {
    MessageEncoding.XSTREAM -> XStreamSerializer::class
    MessageEncoding.JACKSON -> JacksonSerializer::class
    else -> Serializer::class
  }
  return try {
    this.testClass.get().getInstanceVariableValue(targetType, null, targetType)
  } catch (e: Exception) {
    null
  }
}

/**
 * Extracts the PayloadTypeAndRevisionProvider from the tet instance.
 */
fun ExtensionContext.getPayloadTypeAndRevisionProvider(clazz: KClass<out PayloadTypeAndRevisionProvider>): PayloadTypeAndRevisionProvider? {
  if (this.testClass.isEmpty) {
    return null
  }
  return if (clazz == StaticTestInstancePayloadTypeAndRevisionProvider::class) {
    try {
      this.testClass.get().getInstanceVariableValue(PayloadTypeAndRevisionProvider::class, null, PayloadTypeAndRevisionProvider::class)
    } catch (e: Exception) {
      this.publishReportEntry("ERROR}")
      null
    }
  } else {
    try {
      return clazz.primaryConstructor?.call()
    } catch (e: Exception) {
      this.publishReportEntry("ERROR}")
      null
    }
  }
}

/**
 * Extracts the MessageContentProvider from the instance or instantiates one.
 */
fun ExtensionContext.getMessageContentProvider(clazz: KClass<out MessageContentProvider>): MessageContentProvider? {
  if (this.testClass.isEmpty) {
    return null
  }
  return if (clazz == StaticTestInstanceMessageContentProvider::class) {
    try {
      this.testClass.get().getInstanceVariableValue(MessageContentProvider::class, null, MessageContentProvider::class)
    } catch (e: Exception) {
      null
    }
  } else {
    try {
      return clazz.primaryConstructor?.call()
    } catch (e: Exception) {
      null
    }
  }
}


