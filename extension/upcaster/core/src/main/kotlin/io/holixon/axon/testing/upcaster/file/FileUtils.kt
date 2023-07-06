package io.holixon.axon.testing.upcaster.file

import java.io.File
import java.io.FileFilter

/**
 * Retrieves a regex to match files with given ending.
 * Will match files starting with digits having a name separated by a double underscore ("9234__my_file.txt")
 * @param ending the desired ending without the leading dot. ("jpg")
 */
fun numberedFileRegex(ending: String) = Regex("^([0-9]+)_{2}(.*)(\\.$ending)$")


/**
 * Extracts the effective part of the file name matching the numbered file regex with given extension ("9234__my_file__123.txt").
 * @param ending the desired ending without the leading dot. ("txt")
 * @return effective name ("my_file__123").
 */
fun File.extractEffectiveName(ending: String): String {
  val regex = numberedFileRegex(ending)
  require(this.name.matches(regex)) { "Provided file name ${this.name} must match the expected numbered file pattern ${regex.pattern}." }
  // 0. string is the entire match, 2. string is the second group
  return regex.find(this.name)?.groups?.get(2)?.value ?: throw IllegalArgumentException("Could not extract effective name from ${this.name}")
}

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
  return files.sortedBy {
    // 0. string is the entire match, 1. string is the first group
    regex.find(it.name)?.groups?.get(1)?.value?.toInt() ?: throw IllegalArgumentException("Could not extract file number from file name ${this.name}")
  }
}
