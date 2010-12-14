package staomp.locks

trait ReadWriteLock {
  def readLock: Lock
  
  def writeLock: Lock
}
