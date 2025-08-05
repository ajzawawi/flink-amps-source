package com.ajzawawi.flink.amps.source.split

import org.apache.flink.api.connector.source.SourceSplit

/**
 * Represents a unit of work (split) in the AMPS source connector.
 *
 * In this simple example, each split corresponds to a logical AMPS subscription (e.g., topic, filter).
 * All records read from the same subscription belong to the same split.
 *
 * @param splitId A unique identifier for this split.
 */
case class AMPSPartitionSplit(splitId: String) extends SourceSplit
