package staomp.locks

import java.util.concurrent.locks.ReentrantLock

class Semaphore(val capacity: Int) {
  var state = 0
  val lock = new ReentrantLock
  val condition = lock.newCondition
  
  def acquire {
    lock.lock
    try {
      while (state == capacity) {
        condition.await
      }
      state += 1
    } finally {
      lock.unlock
    }
  }
  
  def release {
    lock.lock
    try {
      state -= 1
      condition.signalAll
    } finally {
      lock.unlock
    }
  }
}
