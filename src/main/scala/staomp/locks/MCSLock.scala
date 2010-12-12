package staomp.locks

import java.util.concurrent.atomic.AtomicReference

class MCSLock extends Lock {
  val tail = new AtomicReference[QNode](null)
  val myNode = new ThreadLocal[QNode] {
    override def initialValue = new QNode
  }
  
  def lock() {
    val qnode = myNode.get
    val pred = tail getAndSet qnode
    if (pred == null) {
      return
    }
    qnode.locked = true
    pred.next = qnode
    while (qnode.locked) { }
  }
  
  def unlock() {
    val qnode = myNode.get
    if (qnode.next == null) {
      if (tail.compareAndSet(qnode, null)) {
        return
      }
      while (qnode.next == null) { }
    }
    qnode.next.locked = false
    qnode.next = null
  }
  
  class QNode {
    @volatile var locked = false
    var next: QNode = null
  }
}
