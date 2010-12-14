package staomp.locks

import java.util.concurrent.locks.ReentrantLock

class FifoReadWriteLock extends ReadWriteLock {
  private var writer = false
  private var (readAcquires, readReleases) = (0, 0)
  
  private val lck = new ReentrantLock
  private val condition = lck.newCondition
  
  val readLock = new Lock {
    override def lock {
      lck.lock
      try {
        while (writer) {
          condition.await
        }
        readAcquires += 1
      } finally {
        lck.unlock
      }
    }
    
    override def unlock {
      lck.lock
      try {
        readReleases += 1
        if (readAcquires == readReleases) {
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
        while (writer) {
          condition.await
        }
        writer = true
        while (readAcquires != readReleases) {
          condition.await
        }
      } finally {
        lck.unlock
      }
    }
    
    override def unlock {
      writer = false
      condition.signalAll
    }
  }
}
