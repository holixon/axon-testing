package io.holixon.axon.testing.upcaster

/**
 * Event encoding.
 */
enum class MessageEncoding {
  /**
   * XML written by XStream.
   */
  XSTREAM,

  /**
   * JSON written by Jackson.
   */
  JACKSON,

  /**
   * AVRO single object encoded format.
   */
  AVRO_SINGLE_OBJECT;

  /**
   * Retrieves the default file ending for specified encoding.
   */
  fun defaultFileEnding() =
    when (this) {
      XSTREAM -> "xml"
      JACKSON -> "json"
      AVRO_SINGLE_OBJECT -> "avro" // FIXME: what is the best ending here -> @jangalinski
    }

}
