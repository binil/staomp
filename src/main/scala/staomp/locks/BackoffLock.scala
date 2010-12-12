package staomp.locks

import java.util.concurrent.atomic.AtomicBoolean

class BackoffLock extends Lock {
  val state = new AtomicBoolean(false)
  
  def lock() { 
    import BackoffLock._
    val backoff = new Backoff(MinDelay, MaxDelay)
    while (true) {
      while (state.get) { }
      if (!(state.getAndSet(true))) {
        return
      } else {
        backoff.backoff
      }
    }
  }
  
  def unlock() {
    state set false
  }
}

object BackoffLock {
  val MinDelay = 1
  val MaxDelay = 10
}
