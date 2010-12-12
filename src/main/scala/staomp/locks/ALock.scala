package staomp.locks

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReferenceArray

class ALock(val size: Int) extends Lock {
  val slotIndex = new ThreadLocal[Int] {
    override def initialValue = 0
  }
  val tail = new AtomicInteger(0)
  val flag = new AtomicReferenceArray[Boolean](size)
  flag.set(0, true)
  
  def lock() {
    val slot = tail.getAndIncrement % size
    slotIndex set slot
    while (! (flag get slot)) { }
  }
  
  def unlock() {
    val slot = slotIndex.get
    flag.set(slot, false)
    flag.set((slot + 1) % size, true)
  }
}
