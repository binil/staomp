package staomp.sets

import org.scalatest.WordSpec
import staomp.sets.{FineLockSet => SetImpl}
class SetSpec extends WordSpec {
  "A set" should {
    "remember added elements" in {
      val set = new SetImpl[String]
      assert(set add "X")
      assert(set add "Y")

      assert(set contains "X")
      assert(set contains "Y")
      assert(! (set contains "Z"))
    }

    "not include duplicates" in {
      val set = new SetImpl[String]
      assert(set add "X")
      assert(set add "Y")
      assert(! (set add "X"))
      assert(set add "Z")
    }

    "forget removed elements" in {
      val set = new SetImpl[String]
      assert(set add "X")
      assert(set add "Y")

      assert(set contains "X")
      assert(set remove "X")
      assert(! (set contains "X"))
      assert(! (set remove "X"))

      assert(! (set remove "Z"))
    }
  }
}