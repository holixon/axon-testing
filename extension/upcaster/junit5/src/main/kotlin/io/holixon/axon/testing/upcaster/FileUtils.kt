package io.holixon.axon.testing.upcaster

import org.junit.jupiter.api.extension.ExtensionContext
import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import kotlin.io.path.Path

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
 * Retrieves a regex to match files with given ending.
 * Will match files starting with digits having a name separated by a double underscore ("9234__my_file.txt")
 * @param ending the desired ending without the leading dot. ("jpg")
 */
fun numberedFileRegex(ending: String) = Regex("^([0-9]+)_{2}(.*)(\\.$ending)$")

/**
 * Retrieve numbered files with given ending, sorted by the leading number descending.
 * @param ending desired end of file without dot.
 */
fun File.getFiles(ending: String): List<File> {
  require(this.isDirectory) { "Provided reference ${this.name} must be a folder, but it was not." }
  val regex = numberedFileRegex(ending)
  val files = this.listFiles(FileFilter {
    it.isFile
      && it.name.matches(regex)
  })?.toList() ?: listOf()
  return files.sortedBy { regex.find(it.name)!!.groups[1]!!.value.toInt() }
}

