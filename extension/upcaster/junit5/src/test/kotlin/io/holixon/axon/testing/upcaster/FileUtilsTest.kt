package io.holixon.axon.testing.upcaster

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.io.File
import java.util.stream.Stream

class FileUtilsTest {

  @ParameterizedTest
  @ArgumentsSource(DummyFolderProvider::class)
  fun `finds this folder`(file: File?) {
    assertThat(file).isNotNull
  }

  @ParameterizedTest
  @ArgumentsSource(DummyFolderProvider::class)
  fun `don't find this folder`(file: File?) {
    assertThat(file).isNull()
  }

  @ParameterizedTest
  @ArgumentsSource(DummyFileStreamProvider::class)
  fun `lists files`(files: List<File>) {
    assertThat(files).isNotEmpty
    assertThat(files.map { it.name }).containsExactly(
      "1__some_text.json",
      "2__some_text.json",
      "10__some_text.json",
    )
  }

  @Test
  fun regex_matches() {
    val regex = numberedFileRegex("json")
    assertThat("1__x.json".matches(regex)).isTrue()
    assertThat("n1__x.json".matches(regex)).isFalse()
    assertThat("1__x.xml".matches(regex)).isFalse()
  }

  internal class DummyFolderProvider : ArgumentsProvider {
    override fun provideArguments(ctx: ExtensionContext): Stream<out Arguments> =
      Stream.of(Arguments.of(ctx.getTestFolder()))
  }

  internal class DummyFileStreamProvider : ArgumentsProvider {
    override fun provideArguments(ctx: ExtensionContext): Stream<out Arguments> =
      Stream.of(
        Arguments.of(
          ctx.getTestFolder()?.getFiles("json")
        )
      )
  }
}
