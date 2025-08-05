package com.ajzawawi.flink.amps.source

/**
 * A custom Flink Source implementation that reads messages from an AMPS topic.
 *
 * This source is unbounded and designed for use in streaming mode.
 * Internally, it delegates record fetching to an AMPSPartitionReader, and split assignment to AMPSPartitionEnumerator.
 *
 * The emitted record type is String (raw JSON messages from AMPS).
 *
 * @see AMPSPartitionReader
 * @see AMPSPartitionEnumerator
 */
class AMPSSource {

}
