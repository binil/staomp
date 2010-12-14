package staomp.locks

import java.lang.Thread.currentThread

class SimpleReentrantLock(val lck: java.util.concurrent.locks.Lock) extends Lock {
  private var owner: Thread = null
  private var holdCount = 0
  private val condition = lck.newCondition
  
  def lock {
    lck.lock
    try {
      if (owner == currentThread) {
        holdCount += 1
        return
      }
      while (holdCount != 0) {
        condition.await
      }
      owner = currentThread
      holdCount = 1
    } finally {
      lck.unlock
    }
  }
  
  def unlock {
    lck.lock
    try {
      if (holdCount == 0 || owner != currentThread) {
        throw new IllegalMonitorStateException
      }
      holdCount -= 1
      if (holdCount == 0) {
        condition.signalAll
      }
    } finally {
      lck.unlock
    }
  }
}
