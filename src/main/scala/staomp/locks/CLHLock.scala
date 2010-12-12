package staomp.locks

import java.util.concurrent.atomic.AtomicReference

class CLHLock extends Lock {
  val tail = new AtomicReference[QNode](new QNode)
  
  val myNode = new ThreadLocal[QNode] {
    override def initialValue = new QNode
  }
  
  val myPred = new ThreadLocal[QNode]
  
  def lock() {
    val qnode = myNode.get
    qnode.locked = true
    val pred = tail getAndSet qnode
    myPred set pred
    while (pred.locked) { }
  }
  
  def unlock() {
    val qnode = myNode.get
    qnode.locked = false
    myNode set myPred.get // reusing the QNode object
  }
  
  
  class QNode {
    @volatile var locked: Boolean = false
  }
}
