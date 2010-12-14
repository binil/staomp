package staomp.locks

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class TOLock extends Lock {
  import TOLock.Available
  
  val tail = new AtomicReference[QNode]
  val myNode = new ThreadLocal[QNode]
  
  def lock() {
    val success = tryLock(Long.MaxValue, TimeUnit.MILLISECONDS)
    if (!success) throw new IllegalStateException
  }
    
  def tryLock(time: Long, unit: TimeUnit): Boolean = {
    val startTime = now
    
    val qnode = new QNode
    myNode.set(qnode)
    
    var myPred = tail getAndSet qnode
    if (myPred == null || myPred.pred == Available) { // the lock is free
      return true
    }
    
    val patience = TimeUnit.MILLISECONDS.convert(time, unit)
    while (now - startTime < patience) {
      val predPred = myPred.pred
      
      if (predPred == Available) {   // the previous thread just released the lock
        return true
      } else if (predPred != null) { // the previous thread abandoned the lock
        myPred = predPred
      } 
      // predPred == null, the previous thread is still waiting for the lock or using it
    }
    
    // This thread is timing out ...
    // First, try to reset tail to its previous value myPred, so that
    // we can pretend as if this thread never attempted to acquire the lock.
    // If the reset fails, probably because there is some other successor
    // to this thread, indicate abandonment by setting pred to a non-null value, myPred. 
    // The successor will notice this and start spinning on myPred.
    if (! tail.compareAndSet(qnode, myPred)) {
      qnode.pred = myPred
    }
    
    return false
  }
  
  def unlock() {
    // set predPred to Available to indicate relinquishing the lock
    val qnode = myNode.get
    if (! tail.compareAndSet(qnode, null)) {
      qnode.pred = Available
    }
  }
  
  private def now: Long = System.currentTimeMillis
}

object TOLock {
  val Available = new QNode
}

class QNode { 
  var pred: QNode = null
}
