package staomp.locks

import java.util.concurrent.atomic.AtomicBoolean

class TASLock extends Lock {
  val state = new AtomicBoolean(false)

  def lock() {
    while (state getAndSet true) { }
  }
  
  def unlock() {
    state set false
  }
}
