package com.ajzawawi.flink.amps.source.split

import org.apache.flink.api.connector.source.SourceSplit

case class AMPSPartitionSplitSpec(splitId: String) extends SourceSplit
