package staomp

class RichLock(val lock: java.util.concurrent.locks.Lock) {
  def withLock[T](fn: => T): T = {
    lock.lock
    try {
      fn
    } finally {
      lock.unlock
    }
  }
}

object RichLock {
  implicit def lock2RichLock(lock: java.util.concurrent.locks.Lock) = new RichLock(lock)
}
