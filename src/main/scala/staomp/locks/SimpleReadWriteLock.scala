package staomp.locks

import java.util.concurrent.locks.ReentrantLock

class SimpleReadWriteLock {
  private var readers = 0
  private var writer = false
  
  private val lck = new ReentrantLock
  private val condition = lck.newCondition
  
  val readLock = new Lock {
    override def lock {
      lck.lock
      try {
        while (writer) {
          condition.await
        }
        readers += 1
      } finally {
        lck.unlock
      }
    }
    
    override def unlock {
      lck.lock
      try {
        readers -= 1
        if (readers == 0) {
          condition.signalAll
        }
      } finally {
        lck.unlock
      }
    }
  }

  val writeLock = new Lock {
    override def lock {
      lck.lock
      try {
        while (writer || readers > 0) {
          condition.await
        }
        writer = true
      } finally {
        lck.unlock
      }
    }
    
    override def unlock {
      lck.lock
      try {
        writer = false
        condition.signalAll
      } finally {
        lck.unlock
      }
    }
  }
}

