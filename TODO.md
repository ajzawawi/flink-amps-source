
# Brainstorming

Need a source to ingest data
https://nightlies.apache.org/flink/flink-docs-release-1.18/api/java/org/apache/flink/api/connector/source/SourceReader.html

Splits are the smallest unit of data to be read

Splits are discovered via the Split Enumerator assigning them to the source reader
https://nightlies.apache.org/flink/flink-docs-release-1.18/api/java/org/apache/flink/api/connector/source/SplitEnumerator.html

Split reader will read records from a split at runtime


# Preliminary Structure

Will refactor after tests


AMPSSource.scala
AMPSPartitionSplit.scala
AMPSPartitionEnumerator.scala
AMPSPartitionReader.scala
AMPSPartitionSplitSerializer.scala
FlinkAMPSJob.scala         