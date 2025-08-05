package com.ajzawawi.flink.amps.source.utils
import com.github.f4b6a3.ulid.{Ulid, UlidCreator}

import java.nio.ByteBuffer
import scala.util.Random

/**
 *
 * Implement an HlcUlid using:
 * [ 48-bit Timestamp ][ 16-bit Logical Counter ][ 16-bit Publisher ID ][ 48-bit Randomness/Tie-breaker ]
 * Add link to the blogpost I wrote
 *
 * @param ulid a Universally Unique Lexicographically Sortable Identifier
 * @param logicalCounter : The Hybrid Logical Clock (HLC) component
 * @param publisherId: Unique id per publisher
 */
case class HlcUlid(
                    ulid: Ulid,
                    logicalCounter: Int,
                    publisherId: Int
                  ) {
  def toBase32: String = ulid.toString

  def toBytes: Array[Byte] = {
    val buffer = ByteBuffer.allocate(16)
    buffer.putLong(ulid.getMostSignificantBits)
    buffer.putLong(ulid.getLeastSignificantBits)
    buffer.array()
  }

  def timestampMillis: Long = ulid.getTime

  override def toString: String =
    s"$toBase32|counter=$logicalCounter|publisher=$publisherId"
}

object HlcUlid {
  @volatile private var lastTimestampMillis: Long = 0L
  @volatile private var logicalCounter: Int = 0

  def next(publisherId: Int): HlcUlid = synchronized {
    val now = System.currentTimeMillis()

    if (now == lastTimestampMillis) {
      logicalCounter += 1
    } else if (now > lastTimestampMillis) {
      lastTimestampMillis = now
      logicalCounter = 0
    } else {
      // Clock moved backward — still increment to preserve order
      logicalCounter += 1
    }

    generate(logicalCounter, publisherId, lastTimestampMillis)
  }

  def generate(
                logicalCounter: Int,
                publisherId: Int,
                timestampMillis: Long
              ): HlcUlid = {
    val entropy = buildEntropy(logicalCounter, publisherId)
    val ulid = new Ulid(timestampMillis, entropy)
    HlcUlid(ulid, logicalCounter, publisherId)
  }

  // Build 80 bit entropy: [ 16-bit Logical Counter ][ 16-bit Publisher ID ][ 48-bit Randomness/Tie-breaker ]
  private def buildEntropy(logicalCounter: Int, publisherId: Int): Array[Byte] = {
    val buffer = ByteBuffer.allocate(10) // Total: 10 bytes = 80 bits

    // 2 bytes = [ 16-bit Logical Counter ]
    buffer.putShort(logicalCounter.toShort)

    // 2 bytes = [ 16-bit Publisher ID ]
    buffer.putShort(publisherId.toShort)

    // 6 bytes - [ 48-bit Randomness/Tie-breaker ]
    val randomBytes = new Array[Byte](6)
    new Random().nextBytes(randomBytes)
    buffer.put(randomBytes)
    buffer.array()
  }

  def parse(ulid: Ulid): HlcUlid = {
    val lsb = ulid.getLeastSignificantBits
    val buffer = ByteBuffer.allocate(8).putLong(lsb).array()

    // ULID entropy is 10 bytes, so we need the next 2 from MSB
    val msb = ulid.getMostSignificantBits
    val fullEntropy = ByteBuffer.allocate(10)
    fullEntropy.putShort((msb >>> 48).toShort) // top 16 bits from MSB
    fullEntropy.put(buffer) // lower 8 bytes from LSB

    val entropyArray = fullEntropy.array()
    val entropyBuf = ByteBuffer.wrap(entropyArray)
    val logicalCounter = entropyBuf.getShort() & 0xFFFF
    val publisherId = entropyBuf.getShort() & 0xFFFF

    HlcUlid(ulid, logicalCounter, publisherId)
  }

  def fromString(base32: String): HlcUlid = {
    val ulid = Ulid.from(base32)
    parse(ulid)
  }
}