package staomp.sets

import org.scalatest.WordSpec

class CoarseSetSpec extends SetSpec {
  def createSet = new CoarseLockSet[String]
}

class FineLockSetSpec extends SetSpec {
  def createSet = new FineLockSet[String]
}

class OptimisticLockSetSpec extends SetSpec {
  def createSet = new OptimisticLockSet[String]
}

abstract class SetSpec extends WordSpec {
  def createSet:Set[String]

  "A set" should {
    "remember added elements" in {
      val set = createSet
      assert(set add "X")
      assert(set add "Y")

      assert(set contains "X")
      assert(set contains "Y")
      assert(! (set contains "Z"))
    }

    "not include duplicates" in {
      val set = createSet
      assert(set add "X")
      assert(set add "Y")
      assert(! (set add "X"))
      assert(set add "Z")
    }

    "forget removed elements" in {
      val set = createSet
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