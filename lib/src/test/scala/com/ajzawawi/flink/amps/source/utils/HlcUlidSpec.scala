package com.ajzawawi.flink.amps.source.utils

import org.scalatest.funspec.AnyFunSpec
import java.util.Base64
import com.github.f4b6a3.ulid.Ulid

class HlcUlidSpec extends AnyFunSpec {

  describe("HlcUlid") {
    it("should convert to bytes and back to the same ULID") {
      val original = HlcUlid.generate(3, 42, System.currentTimeMillis())
      val restored = Ulid.from(original.toBase32)
      assert(restored.getMostSignificantBits == original.ulid.getMostSignificantBits)
      assert(restored.getLeastSignificantBits == original.ulid.getLeastSignificantBits)
    }

    it("should produce a valid ULID string") {
      val hlcUlid = HlcUlid.generate(1, 100, System.currentTimeMillis())
      assert(hlcUlid.toBase32.length == 26)
      assert(Ulid.from(hlcUlid.toBase32) != null)
    }

    it("should generate a valid ULID with correct structure") {
      val publisherId = 42
      val hlcUlid = HlcUlid.next(publisherId)

      assert(hlcUlid.ulid != null)
      assert(hlcUlid.toBase32.length == 26) // ULID Base32 format is 26 characters
    }

    it("should increment logical counter for same timestamp") {
      val publisherId = 99

      // Simulate same timestamp by directly calling generate
      val ts = System.currentTimeMillis()
      val first = HlcUlid.generate(0, publisherId, ts)
      val second = HlcUlid.generate(1, publisherId, ts)

      assert(second.logicalCounter > first.logicalCounter)
      assert(first.ulid.compareTo(second.ulid) < 0) // should be sorted
    }

    it("should parse a ULID back into HlcUlid with correct fields") {
      val original = HlcUlid.generate(7, 123, System.currentTimeMillis())
      val parsed = HlcUlid.fromString(original.toBase32)

      assert(parsed.logicalCounter == 7)
      assert(parsed.publisherId == 123)
      assert(parsed.toBase32 == original.toBase32)
    }

    it("should produce consistent byte array format") {
      val ulid = HlcUlid.generate(5, 10, System.currentTimeMillis())
      val bytes = ulid.toBytes
      assert(bytes.length == 16) // the final ULID-HLC is 128 bits (16 bytes)
    }
  }
}
