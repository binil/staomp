package staomp.locks

import java.util.concurrent.atomic.AtomicBoolean

class TTASLock extends Lock {
  private val state = new AtomicBoolean(false)
  
  def lock() {
    while (true) {
      while (state.get) { }
      if (! (state getAndSet true)) {
        return
      }
    }
  }
  
  def unlock() {
    state set false
  }
}
