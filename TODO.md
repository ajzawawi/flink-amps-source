
# Brainstorming

Need a source to ingest data
https://nightlies.apache.org/flink/flink-docs-release-1.18/api/java/org/apache/flink/api/connector/source/SourceReader.html

Splits are the smallest unit of data to be read

Splits are discovered via the Split Enumerator assigning them to the source reader
https://nightlies.apache.org/flink/flink-docs-release-1.18/api/java/org/apache/flink/api/connector/source/SplitEnumerator.html

Split reader will read records from a split at runtime

Serializer for persisting the splits and enumerator state for fault tolerance
https://nightlies.apache.org/flink/flink-docs-master/docs/dev/datastream/sources/


## Bounded

SplitEnumerator - Librarian that knows all the books in the library
and all their chapters. Whenever someone walks in, they get handed a book
or chapter to read

Split - a book or a chapter of a book. Some books are broken up,
some can't be!

SourceReader - The person reading, when they're done, they ask
for something to read and the librarian gives them a chapter.
Reader understands it using their own language skills (format)

Format - The language the books are in.. English or binary beep boop

NoMoreSplits! - Once all the books/chapters are done... the librarian
will say - No more books for you! Get out of here! That's when the
reader knows they're done

## Unbounded

Same as above, but it's a magical library where books keep arriving.

The librarian never responds with "NoMoreSplits!"

The readers never leave or keep coming around. So the reading never ends

## How Would This Work for Amps...

    Source Config
        Topic or Topic Regex
            - Specify one or more amps topics
            - Wildcard topics? filtered subs
        Deserializer
            AMPS messages parsed into usage records (nvfix, avro, json, etc)

    Split
        A single subscription (SOW? bookmark replay)

    SplitEnumerator
        Connect to AMPS Server
            - List all available topics matching the provided pattern or topic list
            - Balance or redistributin of splits

    SourceReader
        Subscribe to assigned split
            subscribe, sow_and_subscribe, bookmark
        Receive the messages continously / deserialize

### Things to think about
    Backpressure
    Parallelism
    Rebalancing
    Recovery on restart - bookmark
    Dynamic discovery** think about later

# Preliminary Structure

Will refactor after tests

AMPSSource.scala
AMPSPartitionSplit.scala
AMPSPartitionEnumerator.scala
AMPSPartitionReader.scala
AMPSPartitionSplitSerializer.scala
FlinkAMPSJob.scala         