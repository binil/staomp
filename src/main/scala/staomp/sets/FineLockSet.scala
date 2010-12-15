package staomp.sets

import java.util.concurrent.locks.ReentrantLock

class FineLockSet[T] extends Set[T] {
  private class Node(val item: T, val key: Int) {
    private val lck = new ReentrantLock
    var next: Node = null
    def this(item: T) = this(item, item.hashCode)
  
    def lock { lck.lock }
    def unlock { lck.unlock }
  }
  
  private val head = new Node(null.asInstanceOf[T], Int.MinValue)
  head.next = new Node(null.asInstanceOf[T], Int.MaxValue)

  def add(elem: T): Boolean = {
    val key = elem.hashCode
    findWithHandInHandLock(key) { (pred, curr) =>
      if (curr.key > key) {
        val node = new Node(elem)
        node.next = curr
        pred.next = node
        true
      } else {
        false
      }
    }
  }
  
  def remove(elem: T): Boolean = {
    val key = elem.hashCode
    findWithHandInHandLock(key) { (pred, curr) =>
      if (curr.key == key) {
        pred.next = curr.next
        true
      } else {
        false
      }
    }
  }
  
  def contains(elem: T): Boolean = {
    val key = elem.hashCode
    findWithHandInHandLock(key) { (_, curr) =>
      curr.key == key
    }
  }
  
  private def findWithHandInHandLock[T](key: Int)(fn: (Node, Node) => T):T = {
    var (pred, curr) = (head, head.next)
    
    pred.lock
    try {
      curr.lock
      try {
        while (curr.key < key) {
          pred.unlock
          pred = curr
          curr = curr.next
          curr.lock
        }
        // invoke function after finding the pred and curr nodes
        fn(pred, curr)
      } finally {
        curr.unlock
      }
    } finally {
      pred.unlock
    }  
  }
}
