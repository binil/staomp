package staomp.sets

import org.scalatest.WordSpec

class CoarseLockSetSpec extends SetSpec {
  def createSet = new CoarseLockSet[String]
}

class FineLockSetSpec extends SetSpec {
  def createSet = new FineLockSet[String]
}

class OptimisticLockSetSpec extends SetSpec {
  def createSet = new OptimisticLockSet[String]
}

class LazyLockSetSpec extends SetSpec {
  def createSet = new LazyLockSet[String]
}

class LockFreeSetSpec extends SetSpec {
  def createSet = new LockFreeSet[String]
}

abstract class SetSpec extends WordSpec {
  def createSet:Set[String]

  "A "+name should {
    "remember added elements" in {
      val set = createSet

      assert(! (set contains "A"))
      assert(set add "A")
      assert(set contains "A")

      assert(set add "B")
      assert(set add "C")
      assert(set add "D")

      assert(set contains "A")
      assert(set contains "B")
      assert(set contains "C")
      assert(set contains "D")
      assert(! (set contains "X"))
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

  def name: String = createSet.getClass.getSimpleName
}